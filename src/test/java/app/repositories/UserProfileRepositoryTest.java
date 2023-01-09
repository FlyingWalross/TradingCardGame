package app.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.daos.*;
import app.testModels.TestCard;
import app.testModels.TestDeck;
import app.testModels.TestStack;
import app.testModels.TestUser;
import app.dtos.UserCredentials;
import app.dtos.UserInfo;
import app.dtos.UserProfile;
import app.dtos.UserStats;
import app.exceptions.AlreadyExistsException;
import app.exceptions.UserDoesNotExistException;
import app.models.Card;
import app.models.Deck;
import app.models.Stack;
import app.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

class UserProfileRepositoryTest {
    UserProfileRepository userProfileRepository;

    UserDao userDao;
    StackDao stackDao;
    DeckDao deckDao;
    CardDao cardDao;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        stackDao = mock(StackDao.class);
        deckDao = mock(DeckDao.class);
        cardDao = mock(CardDao.class);

        userProfileRepository = new UserProfileRepository(userDao, stackDao, deckDao, cardDao);
    }

    @Test
    @DisplayName("get UserProfile by id")
    void testUserRepository_getById() throws SQLException {

        //---Set up test data for Dao mocks---
        when(userDao.readById("test")).thenReturn(TestUser.getTestObject());
        when(stackDao.readById("test")).thenReturn(TestStack.getTestObject());
        when(deckDao.readById("test")).thenReturn(TestDeck.getTestObject());

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

        //---Read UserProfile---
        UserProfile userProfile = userProfileRepository.getById("test");

        //---Assert UserProfile---
        assertEquals("test", userProfile.getUsername());
        assertEquals(1, userProfile.getStack().size());
        assertEquals(4, userProfile.getDeck().size());
        assertEquals("5", userProfile.getStack().get(0).getId());
    }

    @Test
    @DisplayName("get UserProfile with nonexistent id returns null")
    void testUserRepository_getByIdNonexistent() throws SQLException {

        //---Set up test data for Dao mocks---
        when(userDao.readById("test")).thenReturn(null);

        //---Read UserProfile---
        UserProfile userProfile = userProfileRepository.getById("test");

        //---Assert UserProfile---
        assertNull(userProfile);
    }

    @Test
    @DisplayName("get UserInfo")
    void testUserRepository_getUserInfo() throws SQLException {

        //---Set up test data for Dao mocks---
        HashMap<String, User> users = new HashMap<>();
        users.put(TestUser.getTestObject().getUsername(), TestUser.getTestObject());
        when(userDao.read()).thenReturn(users);

        //---Read UserInfo---
        UserInfo userInfo = userProfileRepository.getUserInfo(TestUser.getTestObject().getUsername());

        //---Assert UserInfo---
        assertEquals("testName", userInfo.getName());
        assertEquals("testBio", userInfo.getBio());
        assertEquals("testImage", userInfo.getImage());
    }

    @Test
    @DisplayName("Get Scoreboard")
    void testUserRepository_getScoreboard() throws SQLException {

        //---Set up test data for Dao mocks---
        HashMap<String, User> users = new HashMap<>();

        User user1 = TestUser.getTestObject();
        user1.setUsername("test1");
        user1.setName("testName1");

        User user2 = TestUser.getTestObject();
        user2.setUsername("test2");
        user2.setName("testName2");

        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);

        when(userDao.read()).thenReturn(users);

        //---Read Scoreboard---
        ArrayList<UserStats> scoreboard = userProfileRepository.getScoreboard();

        //---Assert Scoreboard---
        assertEquals(2, scoreboard.size());
    }

    @Test
    @DisplayName("Scoreboard ordered by elo")
    void testUserRepository_ScoreboardOrderedByElo() throws SQLException {

        //---Set up test data for Dao mocks---
        HashMap<String, User> users = new HashMap<>();

        User user1 = TestUser.getTestObject();
        user1.setUsername("test1");
        user1.setName("testName1");
        user1.setElo(5);

        User user2 = TestUser.getTestObject();
        user2.setUsername("test2");
        user2.setName("testName2");
        user2.setElo(10);

        User user3 = TestUser.getTestObject();
        user3.setUsername("test3");
        user3.setName("testName3");
        user3.setElo(2);

        User user4 = TestUser.getTestObject();
        user4.setUsername("test4");
        user4.setName("testName4");
        user4.setElo(15);

        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);
        users.put(user3.getUsername(), user3);
        users.put(user4.getUsername(), user4);

        when(userDao.read()).thenReturn(users);

        //---Read Scoreboard---
        ArrayList<UserStats> scoreboard = userProfileRepository.getScoreboard();

        //---Assert Scoreboard---
        assertEquals(4, scoreboard.size());
        assertEquals("testName4", scoreboard.get(0).getName());
        assertEquals("testName2", scoreboard.get(1).getName());
        assertEquals("testName1", scoreboard.get(2).getName());
        assertEquals("testName3", scoreboard.get(3).getName());
    }

    @Test
    @DisplayName("create new user")
    void testUserRepository_create() throws SQLException, AlreadyExistsException {

        //---Set up test data---
        UserCredentials userCredentials = new UserCredentials(
                "testUsername",
                "testPassword"
        );

        //---Create User---
        userProfileRepository.create(userCredentials);

        //---Assert Create---

        //Create captor to catch user object passed to userDao.create()
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userDao).create(captor.capture());

        User createdUser = captor.getValue();

        //Assert captured objects

        assertEquals("testUsername", createdUser.getUsername());
        assertNotEquals("testPassword", createdUser.getPasswordHash()); //Password should be hashed
    }

    @Test
    @DisplayName("exception on duplicate username")
    void testUserRepository_createDuplicateUser() throws SQLException {

        //---Set up test data---
        UserCredentials userCredentials = new UserCredentials(
                "testUsername",
                "testPassword"
        );

        when(userDao.create(any())).thenThrow(new SQLException("Duplicate", "23000"));

        //---Assert Exception on duplicate username---
        assertThrows(AlreadyExistsException.class, () -> userProfileRepository.create(userCredentials));

    }

    @Test
    @DisplayName("update User")
    void testUserRepository_update() throws SQLException, UserDoesNotExistException {

        //---Set up test data for Dao mocks---
        when(userDao.readById("test")).thenReturn(TestUser.getTestObject());
        when(stackDao.readById("test")).thenReturn(TestStack.getTestObject());
        when(deckDao.readById("test")).thenReturn(TestDeck.getTestObject());

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

        //Set up update data
        UserInfo userInfo = new UserInfo();
        userInfo.setName("updatedName");
        userInfo.setBio("updatedBio");
        userInfo.setImage("updatedImage");

        //---Update User---
        userProfileRepository.update( userInfo, TestUser.getTestObject().getUsername());

        //---Assert Update---

        //Create captor to catch user object passed to userDao.update()
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userDao).update(captor.capture());

        User updatedUser = captor.getValue();

        //Assert captured objects

        assertEquals("updatedName", updatedUser.getName());
        assertEquals("updatedBio", updatedUser.getBio());
        assertEquals("updatedImage", updatedUser.getImage());
    }

    @Test
    @DisplayName("updating nonexistent User throws exception")
    void testUserRepository_updateNonexistentUser() {

        //Set up update data
        UserInfo userInfo = new UserInfo();
        userInfo.setName("updatedName");
        userInfo.setBio("updatedBio");
        userInfo.setImage("updatedImage");

        //---Assert Exception thrown because user is not found---
        assertThrows(UserDoesNotExistException.class, () -> userProfileRepository.update(userInfo, "nonExistingUser"));

    }

    @Test
    @DisplayName("update UserProfile with cards")
    void testUserRepository_updateUserProfileWithCards() throws SQLException {

        //---Set up test data---

        ArrayList<Card> stack = new ArrayList<>();
        ArrayList<Card> deck = new ArrayList<>();

        Card card1 = TestCard.getTestObject();
        Card card2 = TestCard.getTestObject();
        Card card3 = TestCard.getTestObject();

        card1.setId("1");
        card2.setId("2");

        stack.add(card1);
        deck.add(card2);

        UserProfile user = new UserProfile(
                "test",
                "testPasswordHash",
                "updateTestName",
                200,
                50,
                1,
                1,
                "UpdateTestBio",
                "UpdateTestImage",
                stack,
                deck
        );

        //---Update UserProfile with cards---
        userProfileRepository.update(user);

        //---Assert Update---

        // Create captor to catch user object passed to userDao.update()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Stack> stackCaptor = ArgumentCaptor.forClass(Stack.class);
        ArgumentCaptor<Deck> deckCaptor = ArgumentCaptor.forClass(Deck.class);

        verify(userDao).update(userCaptor.capture());
        verify(stackDao).update(stackCaptor.capture());
        verify(deckDao).update(deckCaptor.capture());

        User updatedUser = userCaptor.getValue();
        Stack updatedStack = stackCaptor.getValue();
        Deck updatedDeck = deckCaptor.getValue();

        //Assert captured objects

        assertEquals("updateTestName", updatedUser.getName());
        assertEquals("UpdateTestBio", updatedUser.getBio());
        assertEquals("UpdateTestImage", updatedUser.getImage());
        assertEquals(200, updatedUser.getElo());
        assertEquals(50, updatedUser.getCoins());
        assertEquals(1, updatedUser.getWins());
        assertEquals(1, updatedUser.getLosses());

        assertEquals(1, updatedStack.getCardIDs().size());
        assertEquals("1", updatedStack.getCardIDs().get(0));

        assertEquals(1, updatedDeck.getCardIDs().size());
        assertEquals("2", updatedDeck.getCardIDs().get(0));
    }
}