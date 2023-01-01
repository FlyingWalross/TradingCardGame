package app.battle;

import app.dtos.UserProfile;
import app.repositories.UserProfileRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CountDownLatch;

@Getter
public class Battle {
    UserProfile user1;
    @Setter
    UserProfile user2;
    UserProfileRepository userProfileRepository;
    @Getter
    String battleLog;
    public CountDownLatch battleComplete = new CountDownLatch(1);

    public Battle(UserProfile user1, UserProfileRepository userProfileRepository) {
        this.user1 = user1;
        this.userProfileRepository = userProfileRepository;
    }

    public void startBattle() {
        user1.setElo(user1.getElo() + 1);
        user2.setElo(user2.getElo() + 1);
        battleLog = "Battle started between " + user1.getUsername() + " and " + user2.getUsername() + "!";


        //update users to database
        getUserProfileRepository().update(getUser1());
        getUserProfileRepository().update(getUser2());

        battleComplete.countDown();
    }
}
