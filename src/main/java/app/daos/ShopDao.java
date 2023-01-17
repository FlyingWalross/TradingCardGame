package app.daos;

import app.models.ShopCard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;


public class ShopDao implements Dao<ShopCard, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public ShopDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public ShopCard create(ShopCard shopCard) throws SQLException {
        String query = "INSERT INTO shop_cards (card_id, price) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, shopCard.getCardId());
            stmt.setInt(2, shopCard.getPrice());
            stmt.executeUpdate();
        }
        return shopCard;
    }

    @Override
    public HashMap<String, ShopCard> read() throws SQLException {
        HashMap<String, ShopCard> shopCards = new HashMap<>();
        String query = "SELECT * FROM shop_cards";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                ShopCard shopCard = new ShopCard(
                        result.getString(1),
                        result.getInt(2)
                );
                shopCards.put(shopCard.getCardId(), shopCard);
            }
        }

        return shopCards;
    }

    @Override
    public ShopCard readById(String cardId) throws SQLException {
        String query = "SELECT * FROM shop_cards WHERE card_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, cardId);
            ResultSet result = stmt.executeQuery();
            ShopCard shopCard = null;
            if (result.next()) {
                shopCard = new ShopCard(
                        result.getString(1),
                        result.getInt(2)
                );
            }
            return shopCard;
        }
    }

    @Override
    public void update(ShopCard shopCard) throws SQLException {
        String query = "UPDATE shop_cards SET price = ? WHERE card_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, shopCard.getPrice());
            stmt.setString(2, shopCard.getCardId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(ShopCard shopCard) throws SQLException {
        String query = "DELETE FROM shop_cards WHERE card_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, shopCard.getCardId());
            stmt.executeUpdate();
        }
    }
}
