package app.controllers;

import app.battle.Battle;
import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import http.Responses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import server.Response;
import app.Settings;

import java.util.concurrent.*;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
//responsible for correctly initializing the battle and returning the result
//not responsible for actual battle logic (see Battle class)
public class BattleController extends Controller {
    static final Exchanger<Battle> exchanger = new Exchanger<>();
    UserProfileRepository userProfileRepository;

    // POST /battles
    public Response battle(UserProfile user) {

        //check if user has a configured deck
        if(user.getDeck().size() != Settings.STANDARD_DECK_SIZE) {
            return Responses.deckNotConfigured();
        }

        try {

            //both thread create a new battle object (only one will eventually be used)
            Battle battle = new Battle(user, getUserProfileRepository());
            Battle receivedBattle;

            //exchange battle object with other thread (blocks until other thread arrives at exchanger or timeout is reached)
            receivedBattle = exchanger.exchange(battle, Settings.FIND_BATTLE_TIMEOUT, TimeUnit.SECONDS);

            //compare usernames to determine which thread will complete battle (works because usernames are unique)
            if(battle.getUser1().getUsername().compareTo(receivedBattle.getUser1().getUsername()) < 0) {
                battle = receivedBattle;  //battle will be completed in other thread, so we can throw away our own battle object and replace it by the other threads battle object
                battle.getBattleComplete().await(); //wait for other thread to complete battle
            } else { //battle will be completed in this thread
                battle.setUser2(receivedBattle.getUser1()); //add user from other thread to battle
                battle.startBattle(); //start the battle
            }

            return Responses.battleLog(battle.getBattleLog()); //both threads can get the battle log from the battle object

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) { //thrown by exchanger when timeout is reached
            return Responses.noOpponentFound();
        }
    }
}