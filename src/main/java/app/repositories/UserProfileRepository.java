package app.repositories;

import app.daos.CardDao;
import app.daos.DeckDao;
import app.daos.StackDao;
import app.daos.UserDao;
import app.dtos.*;
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
            return new UserStats(userProfile.getName(), userProfile.getElo(), userProfile.getWins(), userProfile.getLosses());
    }

    public UserProfile create(UserCredentials userCredentials) throws SQLException {
        String passwordHash = EncryptionService.hashPassword(userCredentials.getPasswordPlain());
        User user = new User(userCredentials.getUsername(), passwordHash);
        getUserDao().create(user);

        return this.getById(userCredentials.getUsername());
    }

    public UserProfile update(UserInfo userInfo, String username) throws SQLException {
        User user = getUserDao().readById(username);
        user.setName(userInfo.getName());
        user.setBio(userInfo.getBio());
        user.setImage(userInfo.getImage());

        getUserDao().update(user);
        return this.getById(user.getUsername());
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
