package app.repositories;

import app.daos.CardDao;
import app.daos.ShopDao;
import app.dtos.NewShopCard;
import app.exceptions.AlreadyExistsException;
import app.models.ShopCard;
import app.testModels.TestCard;
import app.testModels.TestShopCard;
import app.dtos.ShopCardDTO;
import app.models.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static app.enums.card_element.*;
import static app.enums.card_type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ShopRepositoryTest {
    ShopRepository shopRepository;

    ShopDao shopDao;
    CardDao cardDao;

    @BeforeEach
    void setUp() {
        shopDao = mock(ShopDao.class);
        cardDao = mock(CardDao.class);

        shopRepository = new ShopRepository(shopDao, cardDao);
    }

    @Test
    @DisplayName("get shop card by id")
    void testShopRepository_getById() throws SQLException {

        when(shopDao.readById("1")).thenReturn(TestShopCard.getTestObject());
        when(cardDao.readById("1")).thenReturn(TestCard.getTestObject());

        //---Read Pack---
        ShopCardDTO shopCardDTO = shopRepository.getById("1");

        //---Assert Pack---
        assertEquals("1", shopCardDTO.getCard().getId());
        assertEquals(2, shopCardDTO.getPrice());
    }

    @Test
    @DisplayName("get all shop cards")
    void testShopRepository_getAll() throws SQLException {

        //---Set up test data for Dao mocks---

        HashMap<String, ShopCard> shopCards = new HashMap<>();
        ShopCard demoShopCard = new ShopCard(
                "1",
                3
        );
        ShopCard demoShopCard2 = new ShopCard(
                "2",
                3
        );
        shopCards.put(demoShopCard.getCardId(), demoShopCard);
        shopCards.put(demoShopCard2.getCardId(), demoShopCard2);

        when(shopDao.read()).thenReturn(shopCards);

        Card testCard1 = TestCard.getTestObject();
        Card testCard2 = TestCard.getTestObject();

        testCard1.setId("1");
        testCard2.setId("2");

        when(cardDao.readById("1")).thenReturn(testCard1);
        when(cardDao.readById("2")).thenReturn(testCard2);

        //---Get ShopCards---
        ArrayList<ShopCardDTO> shopCardDTOs = shopRepository.getAll();

        //---Assert ShopCards---
        assertEquals(2, shopCardDTOs.size());
        assertEquals(3, shopCardDTOs.get(0).getPrice());
        assertEquals(3, shopCardDTOs.get(1).getPrice());
    }


    @Test
    @DisplayName("Create shop card with new card")
    void testShopRepository_createNewCard() throws SQLException, AlreadyExistsException {

        NewShopCard newShopCard = new NewShopCard(
                "1",
                "Goblin",
                10,
                1
        );

        //---Create ShopCard---
        shopRepository.createWithNewCard(newShopCard);

        //---Assert ShopCard and Card creation---

        //Create captor to catch pack object
        ArgumentCaptor<ShopCard> captor = ArgumentCaptor.forClass(ShopCard.class);
        verify(shopDao).create(captor.capture());

        //assert created shopCard
        ShopCard shopCard = captor.getValue();
        assertEquals("1", shopCard.getCardId());
        assertEquals(1, shopCard.getPrice());

        //Create captor to catch card object
        ArgumentCaptor<Card> captor2 = ArgumentCaptor.forClass(Card.class);
        verify(cardDao).create(captor2.capture());

        //assert created cards
        Card card = captor2.getValue();
        assertEquals(goblin, card.getType());
        assertEquals(normal, card.getElement());
    }

    @Test
    @DisplayName("Create shop card with existing card")
    void testShopRepository_createExistingCard() throws SQLException, AlreadyExistsException {

        //---Create ShopCard---
        shopRepository.createWithExistingCard(TestCard.getTestObject(), 1);

        //---Assert ShopCard creation---

        //Create captor to catch ShopCard object
        ArgumentCaptor<ShopCard> captor = ArgumentCaptor.forClass(ShopCard.class);
        verify(shopDao).create(captor.capture());

        //assert created shopCard
        ShopCard shopCard = captor.getValue();
        assertEquals("1", shopCard.getCardId());
        assertEquals(1, shopCard.getPrice());

    }

    @Test
    @DisplayName("Exception on duplicate shop card")
    void testShopRepository_ExceptionDuplicateShopCard() throws SQLException {

        NewShopCard newShopCard = new NewShopCard(
                "1",
                "Goblin",
                10,
                1
        );

        when(shopDao.create(any())).thenThrow(new SQLException("Duplicate", "23000"));

        //---Assert Exception on duplicate ShopCard---
        assertThrows(AlreadyExistsException.class, () -> shopRepository.createWithNewCard(newShopCard));

    }

    @Test
    @DisplayName("Delete shop card")
    void testShopRepository_delete() throws SQLException {

        ShopCardDTO shopCardDTO = new ShopCardDTO(
                TestCard.getTestObject(),
                1
        );

        //---Delete ShopCard---
        shopRepository.delete(shopCardDTO);

        //---Assert ShopCard deletion---

        //Create captor to catch ShopCard object
        ArgumentCaptor<ShopCard> captor = ArgumentCaptor.forClass(ShopCard.class);
        verify(shopDao).delete(captor.capture());

        //assert created shopCard
        ShopCard shopCard = captor.getValue();
        assertEquals("1", shopCard.getCardId());
        assertEquals(1, shopCard.getPrice());

    }
}