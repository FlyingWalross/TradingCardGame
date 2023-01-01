package app.controllers;

import app.battle.Battle;
import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.Responses;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class BattleController extends Controller {
    public static final SynchronousQueue<Battle> battleQueue = new SynchronousQueue<>();
    UserProfileRepository userProfileRepository;

    // POST /battles
    public Response battle(UserProfile user) {
        try {
            Battle battle = battleQueue.poll(); //try to get battle from queue

            if(battle == null) {
                battle = new Battle(user, userProfileRepository); //if there is no battle in queue, create new one
                if(!battleQueue.offer(battle, 10, TimeUnit.SECONDS)){ //offer battle to other threads for 10 seconds
                    return Responses.noOpponentFound();
                }
                battle.getBattleComplete().await(); //wait for battle to complete in other thread
            } else {
                battle.setUser2(user); //if battle was retrieved from queue, set user2
                battle.startBattle(); //start the battle
            }

            //once the battle is complete, the battle log is in the battle object
            String battleLogJSON = getObjectMapper().writeValueAsString(battle.getBattleLog());

            return Responses.ok(battleLogJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Responses.internalServerError();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}