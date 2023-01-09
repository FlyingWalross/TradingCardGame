package app.battle;

import static app.enums.card_element.*;
import static app.enums.card_type.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.Settings;
import app.dtos.UserProfile;
import app.models.Card;
import app.repositories.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class BattleTest {
    @Mock UserProfileRepository userProfileRepository;
    @Mock UserProfile user1;
    @Mock UserProfile user2;
    Battle battle;

    @BeforeEach
    void setUp() {

        ArrayList<Card> user1Deck = new ArrayList<>();
        ArrayList<Card> user2Deck = new ArrayList<>();

        user1Deck.add(new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "WaterGoblin",
                goblin,
                water,
                10
        ));
        user1Deck.add(new Card(
                "99f8f8dc-e25e-4a95-aa2c-782823f36e2a",
                "Dragon",
                dragon,
                normal,
                50
        ));
        user1Deck.add(new Card(
                "e85e3976-7c86-4d06-9a80-641c2019a79f",
                "WaterSpell",
                spell,
                water,
                20
        ));
        user1Deck.add(new Card(
                "1cb6ab86-bdb2-47e5-b6e4-68c5ab389334",
                "Ork",
                ork,
                normal,
                45
        ));

        user2Deck.add(new Card(
                "dfdd758f-649c-40f9-ba3a-8657f4b3439f",
                "FireSpell",
                spell,
                fire,
                25
        ));
        user2Deck.add(new Card(
                "644808c2-f87a-4600-b313-122b02322fd5",
                "WaterGoblin",
                goblin,
                water,
                9
        ));
        user2Deck.add(new Card(
                "4a2757d6-b1c3-47ac-b9a3-91deab093531",
                "Dragon",
                dragon,
                normal,
                55
        ));
        user2Deck.add(new Card(
                "91a6471b-1426-43f6-ad65-6fc473e16f9f",
                "WaterSpell",
                spell,
                water,
                21
        ));

        user1 = mock(UserProfile.class);
        when(user1.getDeck()).thenReturn(user1Deck);
        when(user1.getStack()).thenReturn(new ArrayList<Card>());
        when(user1.getUsername()).thenReturn("user1");

        user2 = mock(UserProfile.class);
        when(user2.getDeck()).thenReturn(user2Deck);
        when(user2.getStack()).thenReturn(new ArrayList<Card>());
        when(user2.getUsername()).thenReturn("user2");

        userProfileRepository = mock(UserProfileRepository.class);

        battle = new Battle(user1, userProfileRepository);
        battle.setUser2(user2);
    }

    @Test
    @DisplayName("Battle randomness")
    void testBattle_random() {

        //Start battle 1
        battle.startBattle();

        //Save battle log of battle 1
        String battleLog1 = battle.getBattleLog();

        //reset users and battle
        setUp();

        //Start battle 2
        battle.startBattle();

        //Save battle log of battle 2
        String battleLog2 = battle.getBattleLog();

        //Check if battle logs are different
        assertNotEquals(battleLog1, battleLog2);
    }

    @Test
    @DisplayName("All cards moved to stack after battle")
    void testBattle_cardsMovedToStack(){
        battle.startBattle();
        assertEquals(0, user1.getDeck().size());
        assertEquals(0, user2.getDeck().size());
    }

    @Test
    @DisplayName("No cards lost in battle")
    void testBattle_noCardsLost(){
        int totalCards = Settings.STANDARD_DECK_SIZE * 2;
        battle.startBattle();
        assertEquals(totalCards, user1.getStack().size() + user2.getStack().size());
    }

    @Test
    @DisplayName("Battle is marked complete")
    void testBattle_markedComplete(){
        battle.startBattle();
        //check that CountDownLatch is 0 after battle
        assertEquals(0, battle.getBattleComplete().getCount());
    }

    @Test
    @DisplayName("Elo calculation")
    void testBattle_checkEloCalculation(){
        //start battle
        battle.startBattle();

        UserProfile winner;
        UserProfile looser;

        //see which user won the battle
        if(user1.getStack().size() > user2.getStack().size()){
            winner = user1;
            looser = user2;
        } else if (user1.getStack().size() < user2.getStack().size()){
            winner = user2;
            looser = user1;
        } else {
            //rerun test if battle ends in a draw
            setUp();
            testBattle_checkEloCalculation();
            return;
        }

        //check that winner gained elo
        verify(winner).setElo(Settings.ELO_ON_WIN);

        //check that looser lost elo
        verify(looser).setElo(Settings.ELO_ON_LOSS);
    }

    @Test
    @DisplayName("Add win/loss to user")
    void testBattle_checkAddWinLoss(){
        //start battle
        battle.startBattle();

        UserProfile winner;
        UserProfile looser;

        //see which user won the battle
        if(user1.getStack().size() > user2.getStack().size()){
            winner = user1;
            looser = user2;
        } else if (user1.getStack().size() < user2.getStack().size()){
            winner = user2;
            looser = user1;
        } else {
            //rerun test if battle ends in a draw
            setUp();
            testBattle_checkAddWinLoss();
            return;
        }

        //check that winner gainer win
        verify(winner).setWins(1);

        //check that looser lost 5 elo
        verify(looser).setLosses(1);
    }

    @Test
    @DisplayName("Users are updated in UserProfileRepository")
    void testBattle_checkUserUpdate(){
        battle.startBattle();
        //check that users are updated
        verify(userProfileRepository).update(user1);
        verify(userProfileRepository).update(user2);
    }

    @Test
    @DisplayName("User with stronger deck wins")
    void testBattle_verifyStrongerUserWinds(){
        //make deck of user 1 way stronger
        user1.getDeck().get(0).setDamage(1000);
        user1.getDeck().get(1).setDamage(1000);
        user1.getDeck().get(2).setDamage(1000);
        user1.getDeck().get(3).setDamage(1000);

        battle.startBattle();

        //check user1 won the battle
        verify(user1).setWins(1);
    }
}
