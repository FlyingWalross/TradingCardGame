package app.battle;

import app.dtos.UserProfile;
import app.models.Card;
import app.repositories.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static app.enums.card_element.*;
import static app.enums.card_type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BattleFunctionsTest {
    @Mock
    UserProfileRepository userProfileRepository;
    @Mock UserProfile user1;
    Battle battle;

    @BeforeEach
    void setUp() {
//
        user1 = mock(UserProfile.class);
        userProfileRepository = mock(UserProfileRepository.class);

        battle = new Battle(user1, userProfileRepository);
    }

    @Test
    @DisplayName("Effectiveness calculation 2 Monsters")
    void testEffectiveness_Monster() {

        //create Cards
        Card monster1 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "FireGoblin",
                goblin,
                fire,
                10
        );
        Card monster2 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "Dragon",
                dragon,
                normal,
                10
        );

        //usually fire should be effective against normal
        //but if both cards are monsters, effectiveness should always be 1
        assertEquals(1, battle.checkEffectiveness(monster1, monster2));
        assertEquals(1, battle.checkEffectiveness(monster2, monster1));
    }


    @Test
    @DisplayName("Effectiveness calculation Spell vs Monster")
    void testEffectiveness_Spell() {

        //create Cards
        Card spell1 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "WaterSpell",
                spell,
                water,
                10
        );
        Card monster2 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "FireDragon",
                dragon,
                fire,
                10
        );

        //water should be effective against fire, so effectiveness should be 2
        //since dragon is not a spell its effectiveness should be 1
        assertEquals(2, battle.checkEffectiveness(spell1, monster2));
        assertEquals(1, battle.checkEffectiveness(monster2, spell1));
    }


    @Test
    @DisplayName("Speciality with no effect")
    void testSpecialities_noEffect() {

        //create Cards
        Card monster1 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "Ork",
                ork,
                normal,
                10
        );
        Card monster2 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "FireDragon",
                dragon,
                fire,
                10
        );

        //there should be no speciality between an ork and a dragon
        assertEquals(1, battle.checkForSpecialties(monster1, monster2));
        assertEquals(1, battle.checkForSpecialties(monster2, monster1));
    }

    @Test
    @DisplayName("Speciality with effect")
    void testSpecialities_withEffect() {

        //create Cards
        Card monster1 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "Goblin",
                goblin,
                normal,
                10
        );
        Card monster2 = new Card(
                "845f0dc7-37d0-426e-994e-43fc3ac83c08",
                "FireDragon",
                dragon,
                fire,
                10
        );

        //the Goblin always looses against a dragon
        assertEquals(0, battle.checkForSpecialties(monster1, monster2));
        assertEquals(1, battle.checkForSpecialties(monster2, monster1));
    }
}
