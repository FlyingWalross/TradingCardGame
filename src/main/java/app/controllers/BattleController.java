package app.controllers;

import app.battle.Battle;
import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
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
public class BattleController extends Controller {
    static final Exchanger<Battle> exchanger = new Exchanger<>();
    UserProfileRepository userProfileRepository;

    // POST /battles
    public Response battle(UserProfile user) {

        if(user.getDeck().size() != Settings.STANDARD_DECK_SIZE) {
            return Responses.deckNotConfigured();
        }

        try {

            Battle battle = new Battle(user, getUserProfileRepository());
            Battle receivedBattle = null;

            receivedBattle = exchanger.exchange(battle, Settings.FIND_BATTLE_TIMEOUT, TimeUnit.SECONDS);

            //compare usernames to determine which thread will complete battle (works because usernames are unique)
            if(battle.getUser1().getUsername().compareTo(receivedBattle.getUser1().getUsername()) < 0) {
                battle = receivedBattle;  //battle will be completed in other thread
                battle.getBattleComplete().await(); //wait for other thread to complete battle
            } else { //battle will be completed in this thread
                battle.setUser2(receivedBattle.getUser1()); //add user from other thread to battle
                battle.startBattle(); //start the battle
            }

            return Responses.battleLog(battle.getBattleLog());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            return Responses.noOpponentFound();
        }
    }
}