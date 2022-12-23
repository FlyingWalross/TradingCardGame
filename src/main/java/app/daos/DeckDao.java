package app.daos;

import app.models.Deck;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class DeckDao implements Dao<Deck, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public DeckDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Deck create(Deck deck) throws SQLException {

        saveDeckCards(deck);

        return deck;
    }

    @Override
    public HashMap<String, Deck> read() throws SQLException {
        HashMap<String, Deck> decks = new HashMap<>();
        String query = "SELECT * FROM user_cards_deck";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Deck deck = new Deck(
                        result.getString(1), new ArrayList<String>()
                );
                decks.put(deck.getUsername(), deck);
            }
        }

        query = "SELECT card_id FROM user_cards_deck WHERE username = ?";
        for (Deck deck : decks.values()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, deck.getUsername());
                ResultSet result = stmt.executeQuery();
                while (result.next()) {
                    deck.getCardIDs().add(result.getString(1));
                }
            }
        }
        return decks;
    }

    @Override
    public Deck readById(String username) throws SQLException {
        String query = "SELECT card_id FROM user_cards_deck WHERE username=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            Deck deck = new Deck(
                    username, new ArrayList<String>()
            );
            while (result.next()) {
                deck.getCardIDs().add(result.getString(1));
            }
            return deck;
        }
    }

    @Override
    public void update(Deck deck) throws SQLException {
        deleteDeckCards(deck);
        saveDeckCards(deck);
    }

    @Override
    public void delete(Deck deck) throws SQLException {
        deleteDeckCards(deck);
    }

    private void saveDeckCards(Deck deck) throws SQLException {
        String query = "INSERT INTO user_cards_deck (username, card_id) VALUES (?, ?)";

        for (String cardID : deck.getCardIDs()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setString(1, deck.getUsername());
                stmt.setString(2, cardID);
                stmt.executeUpdate();
            }
        }
    }

    private void deleteDeckCards(Deck deck) throws SQLException {
        String query = "DELETE FROM user_cards_deck WHERE username = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, deck.getUsername());
            stmt.executeUpdate();
        }
    }
}
