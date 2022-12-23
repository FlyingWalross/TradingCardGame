package app.daos;

import app.models.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CardDao implements Dao<Card, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public CardDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Card create(Card card) throws SQLException {
        String query = "INSERT INTO cards (id, name, type, element, damage) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setInt(3, card.getType().ordinal());
            stmt.setInt(4, card.getElement().ordinal());
            stmt.setFloat(5, card.getDamage());
            stmt.executeUpdate();
        }
        return card;
    }

    @Override
    public HashMap<String, Card> read() throws SQLException {
        HashMap<String, Card> cards = new HashMap<>();
        String query = "SELECT * FROM cards";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Card card = new Card(
                        result.getString(1),
                        result.getString(2),
                        app.enums.card_type.values()[result.getInt(3)],
                        app.enums.card_element.values()[result.getInt(4)],
                        result.getFloat(5)
                );

                cards.put(card.getId(), card);
            }
            return cards;
        }
    }

    @Override
    public Card readById(String id) throws SQLException {
        String query = "SELECT * FROM cards WHERE id = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet result = stmt.executeQuery();
            Card card = new Card(
                    result.getString(1),
                    result.getString(2),
                    app.enums.card_type.values()[result.getInt(3)],
                    app.enums.card_element.values()[result.getInt(4)],
                    result.getFloat(5)
            );
            return card;
        }
    }

    @Override
    public void update(Card card) throws SQLException {
        String query = "UPDATE cards SET name = ?, type = ?, element = ?, damage = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, card.getName());
            stmt.setInt(2, card.getType().ordinal());
            stmt.setInt(3, card.getElement().ordinal());
            stmt.setFloat(4, card.getDamage());
            stmt.setString(5, card.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Card card) throws SQLException {
        String query = "DELETE FROM cards WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, card.getId());
            stmt.executeUpdate();
        }
    }
}
