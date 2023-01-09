package app.daos;

import app.testModels.*;
import app.models.*;
import app.testModels.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DaosTest {

    static Connection connection;
    CardDao cardDao;
    UserDao userDao;
    TradeDao tradeDao;
    DeckDao deckDao;
    StackDao stackDao;
    PackDao packDao;
    ShopDao shopDao;

    @BeforeAll
    static void createTestDb() throws SQLException, IOException {

        //create test database
        Connection connection2 = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/",
                "swe1user",
                "swe1pw"
        );

        try {
            String sql = "DROP DATABASE test;";
            PreparedStatement stmt = connection2.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException ignored) {}

        String sql = "CREATE DATABASE test;";
        PreparedStatement stmt = connection2.prepareStatement(sql);
        stmt.executeUpdate();

        stmt.close();
        connection2.close();

        //create test tables
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/test",
                "swe1user",
                "swe1pw"
        );

        // Load sql script
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("database.sql"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        sql = sb.toString();

        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();

        stmt.close();
    }

    @BeforeEach
    void setUp(){
        cardDao = new CardDao(connection);
        userDao = new UserDao(connection);
        tradeDao = new TradeDao(connection);
        deckDao = new DeckDao(connection);
        stackDao = new StackDao(connection);
        packDao = new PackDao(connection);
        shopDao = new ShopDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {

        //clear database after each test
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader("database_clear.sql"));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        String sql = sb.toString();

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        stmt.close();
    }

    @Test
    @DisplayName("Create And Read")
    void testDAOs_createAndRead() throws SQLException, IOException {

        //---Create---

        //create Users
        userDao.create(TestUser.getTestObject());

        //create Cards
        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();
        Card card4 = TestCard.getTestObject();
        Card card5 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");
        card3.setId("3");
        card4.setId("4");
        card5.setId("5");

        cardDao.create(card1);
        cardDao.create(card2);
        cardDao.create(card3);
        cardDao.create(card4);
        cardDao.create(card5);

        //create Stack
        stackDao.create(TestStack.getTestObject());

        //create Deck
        deckDao.create(TestDeck.getTestObject());

        //create Pack
        packDao.create(TestPack.getTestObject());

        //create Trade
        tradeDao.create(TestTrade.getTestObject());

        //create Shop Card
        shopDao.create(TestShopCard.getTestObject());


        //---Read---

        //read Users
        assertEquals(1, userDao.read().size());

        //read Cards
        assertEquals(5, cardDao.read().size());

        //read Stack
        assertEquals(1, stackDao.read().size());

        //read Deck
        assertEquals(1, deckDao.read().size());

        //read Pack
        assertEquals(1, packDao.read().size());

        //read Trade
        assertEquals(1, tradeDao.read().size());

        //read Shop Card
        assertEquals(1, shopDao.read().size());
    }


    @Test
    @DisplayName("Exception on create with duplicate ids")
    void testDAOs_createDuplicateIds() throws SQLException, IOException {

        //---Create twice and assert Exception---

        //create Users
        userDao.create(TestUser.getTestObject());
        assertThrows(SQLException.class, () -> userDao.create(TestUser.getTestObject()));

        //create Cards
        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();
        Card card4 = TestCard.getTestObject();
        Card card5 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");
        card3.setId("3");
        card4.setId("4");
        card5.setId("5");

        cardDao.create(card1);
        cardDao.create(card2);
        cardDao.create(card3);
        cardDao.create(card4);
        cardDao.create(card5);

        assertThrows(SQLException.class, () -> cardDao.create(card1));

        //create Stack
        stackDao.create(TestStack.getTestObject());
        assertThrows(SQLException.class, () -> stackDao.create(TestStack.getTestObject()));

        //create Deck
        deckDao.create(TestDeck.getTestObject());
        assertThrows(SQLException.class, () -> deckDao.create(TestDeck.getTestObject()));

        //create Trade
        tradeDao.create(TestTrade.getTestObject());
        assertThrows(SQLException.class, () -> tradeDao.create(TestTrade.getTestObject()));

        //create Shop Card
        shopDao.create(TestShopCard.getTestObject());
        assertThrows(SQLException.class, () -> shopDao.create(TestShopCard.getTestObject()));

    }

    @Test
    @DisplayName("Create And ReadById")
    void testDAOs_createAndReadById() throws SQLException, IOException {

        //---Create---

        //create Users
        userDao.create(TestUser.getTestObject());

        //create Cards
        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();
        Card card4 = TestCard.getTestObject();
        Card card5 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");
        card3.setId("3");
        card4.setId("4");
        card5.setId("5");

        cardDao.create(card1);
        cardDao.create(card2);
        cardDao.create(card3);
        cardDao.create(card4);
        cardDao.create(card5);

        //create Stack
        stackDao.create(TestStack.getTestObject());

        //create Deck
        deckDao.create(TestDeck.getTestObject());

        //create Pack
        Pack pack = packDao.create(TestPack.getTestObject());

        //create Trade
        tradeDao.create(TestTrade.getTestObject());

        //create Shop Card
        shopDao.create(TestShopCard.getTestObject());

        //---Read by Id---

        //read Users
        User user = userDao.readById(TestUser.getTestObject().getUsername());
        assertEquals(TestUser.getTestObject().getUsername(), user.getUsername());

        //read Cards
        Card card = cardDao.readById(TestCard.getTestObject().getId());
        assertEquals(TestCard.getTestObject().getId(), card.getId());

        //read Stack
        Stack stack = stackDao.readById(TestStack.getTestObject().getUsername());
        assertEquals(TestStack.getTestObject().getUsername(), stack.getUsername());

        //read Deck
        Deck deck = deckDao.readById(TestDeck.getTestObject().getUsername());
        assertEquals(TestDeck.getTestObject().getUsername(), deck.getUsername());

        //read Pack
        Pack readPack = packDao.readById(pack.getId());
        assertEquals(pack.getId(), readPack.getId());

        //read Trade
        Trade trade = tradeDao.readById(TestTrade.getTestObject().getId());
        assertEquals(TestTrade.getTestObject().getId(), trade.getId());

        //read Shop Card
        ShopCard shopCard = shopDao.readById(TestShopCard.getTestObject().getCardId());
        assertEquals(TestShopCard.getTestObject().getCardId(), shopCard.getCardId());
    }

    @Test
    @DisplayName("Read empty db")
    void testDAOs_readEmpty() throws SQLException {

        //read Users
        assertEquals(0, userDao.read().size());

        //read Cards
        assertEquals(0, cardDao.read().size());

        //read Stack
        assertEquals(0, stackDao.read().size());

        //read Deck
        assertEquals(0, deckDao.read().size());

        //read Pack
        assertEquals(0, packDao.read().size());

        //read Trade
        assertEquals(0, tradeDao.read().size());

        //read Shop Card
        assertEquals(0, shopDao.read().size());
    }

    @Test
    @DisplayName("Read nonexistent id")
    void testDAOs_readNonexistentId() throws SQLException {

        //read Users
        assertEquals(0, userDao.read().size());
        assertNull(userDao.readById("testId"));

        //read Cards
        assertEquals(0, cardDao.read().size());
        assertNull(cardDao.readById("testId"));

        //read Stack
        assertEquals(0, stackDao.read().size());
        assertEquals(0, stackDao.readById("testId").getCardIDs().size());

        //read Deck
        assertEquals(0, deckDao.read().size());
        assertEquals(0, deckDao.readById("testId").getCardIDs().size());

        //read Pack
        assertEquals(0, packDao.read().size());
        assertNull(packDao.readById(123));

        //read Trade
        assertEquals(0, tradeDao.read().size());
        assertNull(tradeDao.readById("testId"));

        //read Shop Card
        assertEquals(0, shopDao.read().size());
        assertNull(shopDao.readById("testId"));
    }

    @Test
    @DisplayName("Delete")
    void testDAOs_delete() throws SQLException, IOException {

        //---Create---

        //create Users
        userDao.create(TestUser.getTestObject());

        //create Cards
        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();
        Card card4 = TestCard.getTestObject();
        Card card5 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");
        card3.setId("3");
        card4.setId("4");
        card5.setId("5");

        cardDao.create(card1);
        cardDao.create(card2);
        cardDao.create(card3);
        cardDao.create(card4);
        cardDao.create(card5);

        //create Stack
        stackDao.create(TestStack.getTestObject());

        //create Deck
        deckDao.create(TestDeck.getTestObject());

        //create Pack
        Pack pack = TestPack.getTestObject();
        pack = packDao.create(pack); //id for pack is set by db

        //create Trade
        tradeDao.create(TestTrade.getTestObject());

        //create Shop Card
        shopDao.create(TestShopCard.getTestObject());

        //---Delete---

        //delete Pack
        packDao.delete(pack);

        //delete Trade
        tradeDao.delete(TestTrade.getTestObject());

        //delete Deck
        deckDao.delete(TestDeck.getTestObject());

        //delete Stack
        stackDao.delete(TestStack.getTestObject());

        //delete Shop Card
        shopDao.delete(TestShopCard.getTestObject());

        //delete Cards
        cardDao.delete(card1);
        cardDao.delete(card2);
        cardDao.delete(card3);
        cardDao.delete(card4);
        cardDao.delete(card5);

        //delete Users
        userDao.delete(TestUser.getTestObject());

        ///---Check if deleted---

        //read Users
        assertEquals(0, userDao.read().size());

        //read Cards
        assertEquals(0, cardDao.read().size());

        //read Stack
        assertEquals(0, stackDao.read().size());

        //read Deck
        assertEquals(0, deckDao.read().size());

        //read Pack
        assertEquals(0, packDao.read().size());

        //read Trade
        assertEquals(0, tradeDao.read().size());

        //read Shop Card
        assertEquals(0, shopDao.read().size());
    }

    @Test
    @DisplayName("Update")
    void testDAOs_update() throws SQLException, IOException {

        //---Create---

        //create Users
        userDao.create(TestUser.getTestObject());

        //create Cards
        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();
        Card card4 = TestCard.getTestObject();
        Card card5 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");
        card3.setId("3");
        card4.setId("4");
        card5.setId("5");

        cardDao.create(card1);
        cardDao.create(card2);
        cardDao.create(card3);
        cardDao.create(card4);
        cardDao.create(card5);

        //create Stack
        stackDao.create(TestStack.getTestObject());

        //create Deck
        deckDao.create(TestDeck.getTestObject());

        //create Pack
        Pack pack = TestPack.getTestObject();
        pack = packDao.create(pack); //id for pack is set by db

        //create Trade
        tradeDao.create(TestTrade.getTestObject());

        //create Shop Card
        shopDao.create(TestShopCard.getTestObject());

        //---Update and check---

        //update User
        User user = userDao.readById(TestUser.getTestObject().getUsername());
        user.setName("newName");
        userDao.update(user);
        assertEquals("newName", userDao.readById(user.getUsername()).getName());

        //update Card
        Card card = cardDao.readById(TestCard.getTestObject().getId());
        card.setName("newName");
        cardDao.update(card);
        assertEquals("newName", cardDao.readById(card.getId()).getName());

        //update Stack
        Stack stack = stackDao.readById(TestStack.getTestObject().getUsername());
        stack.getCardIDs().removeAll(TestStack.getTestObject().getCardIDs());
        stackDao.update(stack);
        assertEquals(0, stackDao.readById(stack.getUsername()).getCardIDs().size());

        //update Deck
        Deck deck = deckDao.readById(TestDeck.getTestObject().getUsername());
        deck.getCardIDs().removeAll(TestDeck.getTestObject().getCardIDs());
        deckDao.update(deck);
        assertEquals(0, deckDao.readById(deck.getUsername()).getCardIDs().size());

        //update Pack
        Pack pack1 = packDao.readById(pack.getId());
        pack1.getCardIDs().remove(pack1.getCardIDs().get(0));
        packDao.update(pack1);
        assertEquals(4, packDao.readById(pack1.getId()).getCardIDs().size());

        //update Trade
        Trade trade = tradeDao.readById(TestTrade.getTestObject().getId());
        trade.setMin_damage(100);
        tradeDao.update(trade);
        assertEquals(100, tradeDao.readById(trade.getId()).getMin_damage());

        //update Shop Card
        ShopCard shopCard = shopDao.readById(TestShopCard.getTestObject().getCardId());
        shopCard.setPrice(100);
        shopDao.update(shopCard);
        assertEquals(100, shopDao.readById(shopCard.getCardId()).getPrice());
    }

    @AfterAll
    static void deleteTestDb() throws SQLException {

        connection.close();

        //create test database
        Connection connection2 = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/",
                "swe1user",
                "swe1pw"
        );

        String sql = "DROP DATABASE test";
        PreparedStatement stmt = connection2.prepareStatement(sql);
        stmt.executeUpdate();

        stmt.close();
        connection2.close();
    }

}
