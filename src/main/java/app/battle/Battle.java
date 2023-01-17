package app.battle;

import app.dtos.UserProfile;
import app.models.Card;
import app.repositories.UserProfileRepository;
import lombok.Getter;
import lombok.Setter;
import static app.battle.EffectivenessTable.effectiveness;
import static app.enums.card_element.*;
import static app.enums.card_type.*;
import app.Settings;

import java.util.concurrent.CountDownLatch;

@Getter
public class Battle { //class with the actual battle logic
    UserProfile user1;
    @Setter
    UserProfile user2;
    UserProfileRepository userProfileRepository;
    @Getter
    String battleLog;
    CountDownLatch battleComplete = new CountDownLatch(1);

    public Battle(UserProfile user1, UserProfileRepository userProfileRepository) {
        this.user1 = user1; //user1 is set by one thread, user2 is later set by the other thread
        this.userProfileRepository = userProfileRepository;
    }

    public void startBattle() {

        battleLog = "------------- BATTLE LOG -------------\n\n";
        battleLog += "Battle started between " + user1.getUsername() + " and " + user2.getUsername() + "!\n\n";

        battleLog += user1.getUsername() + "'s deck:\n";
        for(Card card : user1.getDeck()) {
            battleLog += "- " + card.getName() + " (" + card.getDamage() + " damage)\n";
        }

        battleLog += "\n" + user2.getUsername() + "'s deck:\n";
        for(Card card : user2.getDeck()) {
            battleLog += "- " + card.getName() + " (" + card.getDamage() + " damage)\n";
        }

        int user1WonRounds = 0;
        int user2WonRounds = 0;

        for(int i = 1; i <= Settings.MAX_BATTLE_ROUNDS && user1.getDeck().size() != 0 && user2.getDeck().size() != 0; i++){ //main loop for battle rounds
            battleLog += "\n---- Round " + i + " ----\n";

            Card user1Card = chooseRandomCard(user1);
            Card user2Card = chooseRandomCard(user2);
            float card1Damage = user1Card.getDamage();
            float card2Damage = user2Card.getDamage();

            addLog(user1.getUsername() + "'s card " + user1Card.getName() + " (" + user1Card.getDamage() + " damage) is battling against " + user2.getUsername() + "'s card " + user2Card.getName() + " (" + user2Card.getDamage() + " damage)!");

            //check for specialties
            card1Damage *= checkForSpecialties(user1Card, user2Card);
            card2Damage *= checkForSpecialties(user2Card, user1Card);

            if(card1Damage != 0 && card2Damage != 0) {
                //if there are no specialties, check for element effectiveness for spells
                card1Damage *= checkEffectiveness(user1Card, user2Card);
                card2Damage *= checkEffectiveness(user2Card, user1Card);

                addLog(card1Damage + " Damage VS " + card2Damage + " Damage");
            }

            if(card1Damage > card2Damage) {
                addLog(user1.getUsername() + "'s " + user1Card.getName() + " wins!");
                addLog(user2.getUsername() + "'s card will be added to " + user1.getUsername() + "'s deck!");
                user2.getDeck().remove(user2Card);
                user1.getDeck().add(user2Card);
                user1WonRounds++;
            } else if(card2Damage > card1Damage) {
                addLog(user2.getUsername() + "'s " + user2Card.getName() + " wins!");
                addLog(user1.getUsername() + "'s card will be added to " + user2.getUsername() + "'s deck!");
                user1.getDeck().remove(user1Card);
                user2.getDeck().add(user1Card);
                user2WonRounds++;
            } else {
                addLog("The round ends in draw!");
            }

            battleLog += "\n" + user1.getUsername() + "'s new deck:\n";
            for(Card card : user1.getDeck()) {
                battleLog += "- " + card.getName() + " (" + card.getDamage() + " damage)\n";
            }

            battleLog += "\n" + user2.getUsername() + "'s new deck:\n";
            for(Card card : user2.getDeck()) {
                battleLog += "- " + card.getName() + " (" + card.getDamage() + " damage)\n";
            }
        }

        if(user1.getDeck().size() == 0) {
            battleLog += "\n" + user1.getUsername() + " has no cards left in their deck!\n";
            handleWin(user2, user1);
        } else if(user2.getDeck().size() == 0) {
            battleLog += "\n" + user2.getUsername() + " has no cards left in their deck!\n";
            handleWin(user1, user2);
        } else if(user1WonRounds > user2WonRounds) {
            battleLog += "\nThe max. number of rounds was exceeded!\n" + user1.getUsername() + " won " + user1WonRounds + " rounds and " + user2.getUsername() + " won " + user2WonRounds + " rounds!\n";
            handleWin(user1, user2);
        } else if(user2WonRounds > user1WonRounds) {
            battleLog += "\nThe max. number of rounds was exceeded!\n" + user1.getUsername() + " won " + user1WonRounds + " rounds and " + user2.getUsername() + " won " + user2WonRounds + " rounds!\n";
            handleWin(user2, user1);
        } else {
            battleLog += "\nThe max. number of rounds was exceeded and the battle ends in a draw!\n";
        }

        //move all cards from deck to stack
        user1.getStack().addAll(user1.getDeck());
        user1.getDeck().clear();
        user2.getStack().addAll(user2.getDeck());
        user2.getDeck().clear();

        //update users to database
        getUserProfileRepository().update(user1);
        getUserProfileRepository().update(user2);

        battleComplete.countDown(); //mark battle as completed for waiting thread
    }

    void handleWin(UserProfile winner, UserProfile looser) {
        battleLog += winner.getUsername() + " wins the battle!\n";
        battleLog += looser.getUsername() + " looses 5 ELO!\n";
        battleLog += winner.getUsername() + " gains 3 ELO!\n";
        looser.setElo(looser.getElo() + Settings.ELO_ON_LOSS);
        winner.setElo(winner.getElo() + Settings.ELO_ON_WIN);
        winner.setWins(winner.getWins() + 1);
        looser.setLosses(looser.getLosses() + 1);
    }

    Card chooseRandomCard (UserProfile user) {
        return user.getDeck().get((int) (Math.random() * user.getDeck().size()));
    }

    float checkEffectiveness(Card attacker, Card defender) {
        //only spell gets effectiveness bonus
        if(attacker.getType() != spell) {
            return 1;
        }

        float multiplier = effectiveness[attacker.getElement().ordinal()][defender.getElement().ordinal()];
        if(multiplier < 1) {
            addLog("The " + attacker.getName() + " is not very effective against the " + defender.getName() + "!");
        }
        if(multiplier > 1) {
            addLog("The " + attacker.getName() + " is very effective against the " + defender.getName() + "!");
        }

        return multiplier;
    }

    float checkForSpecialties(Card attacker, Card defender){
        //attacker loses due to specialty
        if(attacker.getType() == goblin && defender.getType() == dragon){
            addLog("The goblin is too afraid of the dragon to attack!");
            return 0;
        }
        if(attacker.getType() == ork && defender.getType() == wizard){
            addLog("The wizard is able to control the ork so it is not able to damage the wizard!");
            return 0;
        }
        if(attacker.getType() == knight && defender.getType() == spell && defender.getElement() == water){
            addLog("The armor of the knight is so heavy that the waterspell makes them drown instantly!");
            return 0;
        }
        if(attacker.getType() == spell && defender.getType() == kraken){
            addLog("The kraken is immune against spells!");
            return 0;
        }
        if(attacker.getType() == dragon && defender.getType() == elf && defender.getElement() == fire){
            addLog("The fireelves can evade the dragons attacks!");
            return 0;
        }

        return 1; //no specialties
    }

    void addLog(String log){
        battleLog += ">> " + log + "\n";
    }
}
