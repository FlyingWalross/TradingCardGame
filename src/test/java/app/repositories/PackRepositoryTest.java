package app.repositories;

import app.daos.CardDao;
import app.daos.PackDao;
import app.testModels.TestCard;
import app.testModels.TestPack;
import app.dtos.NewCard;
import app.dtos.PackDTO;
import app.exceptions.AlreadyExistsException;
import app.exceptions.NoPacksAvailableException;
import app.models.Card;
import app.models.Pack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static app.enums.card_element.*;
import static app.enums.card_type.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackRepositoryTest {
    PackRepository packRepository;

    PackDao packDao;
    CardDao cardDao;

    @BeforeEach
    void setUp() {
        packDao = mock(PackDao.class);
        cardDao = mock(CardDao.class);

        packRepository = new PackRepository(packDao, cardDao);
    }

    @Test
    @DisplayName("get pack by id")
    void testPackRepository_getById() throws SQLException {

        //---Set up test data for Dao mocks---
        Pack demoPack = TestPack.getTestObject();
        demoPack.setId(1);

        when(packDao.readById(1)).thenReturn(demoPack);

        //---Read Pack---
        Pack Pack = packRepository.getById(1);

        //---Assert Pack---
        assertEquals(1, Pack.getId());
        assertEquals(5, Pack.getCardIDs().size());
    }

    @Test
    @DisplayName("get packDTO")
    void testPackRepository_getPackDTO() throws SQLException, NoPacksAvailableException {

        //---Set up test data for Dao mocks---

        HashMap<Integer, Pack> packs = new HashMap<>();
        Pack demoPack = TestPack.getTestObject();
        demoPack.setId(1);
        packs.put(demoPack.getId(), demoPack);
        when(packDao.read()).thenReturn(packs);

        Card testCard1 = TestCard.getTestObject();
        Card testCard2 = TestCard.getTestObject();
        Card testCard3 = TestCard.getTestObject();
        Card testCard4 = TestCard.getTestObject();
        Card testCard5 = TestCard.getTestObject();

        testCard1.setId("1");
        testCard2.setId("2");
        testCard3.setId("3");
        testCard4.setId("4");
        testCard5.setId("5");

        when(cardDao.readById("1")).thenReturn(testCard1);
        when(cardDao.readById("2")).thenReturn(testCard2);
        when(cardDao.readById("3")).thenReturn(testCard3);
        when(cardDao.readById("4")).thenReturn(testCard4);
        when(cardDao.readById("5")).thenReturn(testCard5);


        //---Get PackDTO---
        PackDTO Pack = packRepository.getPack();

        //---Assert Pack---
        assertEquals(1, Pack.getId());
        assertEquals(5, Pack.getCards().size());
        assertEquals("WaterSpell", Pack.getCards().get(0).getName());
    }

    @Test
    @DisplayName("Exception no packs available")
    void testPackRepository_noPacksAvailable() throws SQLException {

        //---Set up test data for Dao mocks---

        //set up empty hashmap with no packs
        HashMap<Integer, Pack> packs = new HashMap<>();
        when(packDao.read()).thenReturn(packs);

        //---Get pack---
        assertThrows(NoPacksAvailableException.class, () -> packRepository.getPack());
    }

    @Test
    @DisplayName("Create pack")
    void testPackRepository_create() throws SQLException, AlreadyExistsException {

        //---Set up test data for Dao mocks---

        ArrayList<NewCard> newCards = new ArrayList<>();

        newCards.add (new NewCard(
                "1",
                "Goblin",
                10
        ));

        newCards.add (new NewCard(
                "2",
                "FireSpell",
                10
        ));


        newCards.add (new NewCard(
                "3",
                "WaterDragon",
                10
        ));


        newCards.add (new NewCard(
                "4",
                "Wizard",
                10
        ));

        newCards.add (new NewCard(
                "5",
                "Ork",
                10
        ));

        //---Create Pack---
        packRepository.create(newCards);

        //---Assert Pack and Card creation---

        //Create captor to catch pack object
        ArgumentCaptor<Pack> captor = ArgumentCaptor.forClass(Pack.class);
        verify(packDao).create(captor.capture());

        //assert created pack
        Pack pack = captor.getValue();
        assertEquals(5, pack.getCardIDs().size());

        //Create captor to catch card object
        ArgumentCaptor<Card> captor2 = ArgumentCaptor.forClass(Card.class);
        verify(cardDao, times(5)).create(captor2.capture());

        //assert created cards
        ArrayList<Card> cards = (ArrayList<Card>) captor2.getAllValues();
        assertEquals(5, cards.size());
        assertEquals(goblin, cards.get(0).getType());
        assertEquals(normal, cards.get(0).getElement());
        assertEquals(spell, cards.get(1).getType());
        assertEquals(fire, cards.get(1).getElement());
        assertEquals(dragon, cards.get(2).getType());
        assertEquals(water, cards.get(2).getElement());
        assertEquals(wizard, cards.get(3).getType());
        assertEquals(normal, cards.get(3).getElement());
        assertEquals(ork, cards.get(4).getType());
        assertEquals(normal, cards.get(4).getElement());
    }

    @Test
    @DisplayName("Delete Pack")
    void testPackRepository_delete() throws SQLException {

        PackDTO deletePackDTO = new PackDTO(1, new ArrayList<>());
        Pack deletePack = new Pack(1, new ArrayList<>());

        when(packDao.readById(1)).thenReturn(deletePack);

        packRepository.delete(deletePackDTO);

        verify(packDao).delete(deletePack);
    }
}