package app.repositories;

import app.daos.*;
import app.dtos.*;
import app.enums.card_type;
import app.exceptions.AlreadyExistsException;
import app.models.*;
import app.testModels.TestCard;
import app.testModels.TestTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeRepositoryTest {
    TradeRepository tradeRepository;

    TradeDao tradeDao;
    CardDao cardDao;

    @BeforeEach
    void setUp() {
        tradeDao = mock(TradeDao.class);
        cardDao = mock(CardDao.class);

        tradeRepository = new TradeRepository(tradeDao, cardDao);
    }

    @Test
    @DisplayName("get TradeDTO by id")
    void testTradeRepository_getById() throws SQLException {

        //---Set up test data for Dao mocks---
        when(tradeDao.readById("test")).thenReturn(TestTrade.getTestObject());

        Card testCard = TestCard.getTestObject();
        testCard.setId("5");

        when(cardDao.readById("5")).thenReturn(testCard);

        //---Read Trade---
        TradeDTO tradeDTO = tradeRepository.getById("test");

        //---Assert TradeDTO---
        assertEquals("test", tradeDTO.getId());
        assertEquals("test", tradeDTO.getUsername());
        assertEquals("5", tradeDTO.getCard().getId());
        assertEquals(card_type.monster, tradeDTO.getType());
        assertEquals(10, tradeDTO.getMin_damage());
    }

    @Test
    @DisplayName("get TradeDTO with nonexistent id returns null")
    void testTradeRepository_getByIdNonexistent() {

        //---Read nonexistent Trade---
        TradeDTO tradeDTO = tradeRepository.getById("someId");

        //---Assert TradeDTO is null---
        assertNull(tradeDTO);
    }

    @Test
    @DisplayName("create new trade")
    void testTradeRepository_create() throws SQLException, AlreadyExistsException {

        //---Create Trade---
        tradeRepository.create(TestTrade.getTestObject());

        //---Assert Create---

        //Create captor to catch user object passed to userDao.create()
        ArgumentCaptor<Trade> captor = ArgumentCaptor.forClass(Trade.class);

        verify(tradeDao).create(captor.capture());

        Trade trade = captor.getValue();

        //Assert captured objects
        assertEquals("test", trade.getId());
        assertEquals("test", trade.getUsername());
        assertEquals("5", trade.getCardId());
        assertEquals(card_type.monster, trade.getType());
        assertEquals(10, trade.getMin_damage());
    }

    @Test
    @DisplayName("exception on duplicate trade id")
    void testTradeRepository_createDuplicateTradeId() throws SQLException {

        when(tradeDao.create(any())).thenThrow(new SQLException("Duplicate", "23000"));

        //---Assert Exception on duplicate trade id ---
        assertThrows(AlreadyExistsException.class, () -> tradeRepository.create(TestTrade.getTestObject()));
    }

    @Test
    @DisplayName("delete trade")
    void testTradeRepository_delete() throws SQLException {

        //---Set up test data for Dao mocks---
        when(tradeDao.readById("test")).thenReturn(TestTrade.getTestObject());

        Card testCard = TestCard.getTestObject();
        testCard.setId("5");
        when(cardDao.readById("5")).thenReturn(testCard);

        //---Get trade to delete---
        TradeDTO tradeDTO = tradeRepository.getById("test");

        //---Delete trade---
        tradeRepository.delete(tradeDTO);

        //---Assert Delete---

        //Create captor to catch user object passed to userDao.create()
        ArgumentCaptor<Trade> captor = ArgumentCaptor.forClass(Trade.class);

        verify(tradeDao).delete(captor.capture());

        Trade trade = captor.getValue();

        //Assert captured objects
        assertEquals("test", trade.getId());
    }

}