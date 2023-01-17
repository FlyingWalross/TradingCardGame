package app.repositories;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.StackDao;
import app.daos.UserDao;
import app.dtos.*;
import app.exceptions.AlreadyExistsException;
import app.exceptions.UserDoesNotExistException;
import app.models.Card;
import app.models.Deck;
import app.models.Stack;
import app.models.User;
import app.services.EncryptionService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserProfileRepository {
    UserDao userDao;
    StackDao stackDao;
    DeckDao deckDao;
    CardDao cardDao;

    public UserProfile getById(String username) {
        try {
            User user = getUserDao().readById(username);
            if(user == null){
                return null;
            }
            return new UserProfile(
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getName(), user.getElo(),
                    user.getCoins(),
                    user.getWins(),
                    user.getLosses(),
                    user.getBio(),
                    user.getImage(),
                    this.getUserStack(user.getUsername()),
                    this.getUserDeck(user.getUsername())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserInfo getUserInfo(String username) {
        try {
            User user = getUserDao().read().get(username);
            if(user == null) {
                return null;
            }
            return new UserInfo(user.getName(), user.getBio(), user.getImage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserStats getUserStats(UserProfile userProfile) {
            return new UserStats(
                    userProfile.getName(),
                    userProfile.getElo(),
                    userProfile.getWins(),
                    userProfile.getLosses());
    }

    public ArrayList<UserStats> getScoreboard() {
        try {
            HashMap<String, User> users = getUserDao().read();
            ArrayList<UserStats> scoreboard = new ArrayList<>();

            //sort HashMap by elo and add userStats to scoreboard
            users.values().stream()
                    .sorted((u1, u2) -> u2.getElo() - u1.getElo())
                    .forEach(user -> scoreboard.add(new UserStats(user.getName(), user.getElo(), user.getWins(), user.getLosses())));

            return scoreboard;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserProfile create(UserCredentials userCredentials) throws AlreadyExistsException {
        try {
            String passwordHash = EncryptionService.hashPassword(userCredentials.getPasswordPlain());
            User user = new User(userCredentials.getUsername(), passwordHash);
            getUserDao().create(user);

            return this.getById(userCredentials.getUsername());
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState.equals("23000") || sqlState.equals("23505")) {
                throw new AlreadyExistsException("Username already exists");
            } else {
                // Other SQL exceptions
                throw new RuntimeException(e);
            }
        }
    }

    //only update UserInfo part of user
    public void update(UserInfo userInfo, String username) throws UserDoesNotExistException {
        try {
            User user = getUserDao().readById(username);

            if(user == null){
                throw new UserDoesNotExistException("User does not exist");
            }

            user.setName(userInfo.getName());
            user.setBio(userInfo.getBio());
            user.setImage(userInfo.getImage());

            getUserDao().update(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //update entire UserProfile including deck and stack
    public void update(UserProfile userProfile) {
        try {
            //update User
            User user = new User(
                    userProfile.getUsername(),
                    userProfile.getPasswordHash(),
                    userProfile.getName(),
                    userProfile.getElo(),
                    userProfile.getCoins(),
                    userProfile.getWins(),
                    userProfile.getLosses(),
                    userProfile.getBio(),
                    userProfile.getImage()
            );
            getUserDao().update(user);

            //update Stack
            ArrayList<String> stackCardIds = new ArrayList<>();
            for (Card card : userProfile.getStack()) {
                stackCardIds.add(card.getId());
            }
            getStackDao().update(new Stack(userProfile.getUsername(), stackCardIds));

            //update Deck
            ArrayList<String> deckCardIds = new ArrayList<>();
            for (Card card : userProfile.getDeck()) {
                deckCardIds.add(card.getId());
            }
            getDeckDao().update(new Deck(userProfile.getUsername(), deckCardIds));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Card> getUserDeck(String username) {
        try {
            Deck deck = getDeckDao().readById(username);
            ArrayList<Card> userDeck = new ArrayList<>();
            for (String cardID : deck.getCardIDs()) {
                userDeck.add(getCardDao().readById(cardID));
            }
            return userDeck;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Card> getUserStack(String username) {
        try {
            Stack stack = getStackDao().readById(username);
            ArrayList<Card> userStack = new ArrayList<>();
            for (String cardID : stack.getCardIDs()) {
                userStack.add(getCardDao().readById(cardID));
            }
            return userStack;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
