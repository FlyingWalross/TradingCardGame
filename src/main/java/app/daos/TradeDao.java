package app.daos;

import app.models.Trade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TradeDao implements Dao<Trade, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public TradeDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Trade create(Trade trade) throws SQLException {
        String query = "INSERT INTO trade_offers (id, username, card_id, type, min_damage) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, trade.getId());
            stmt.setString(2, trade.getUsername());
            stmt.setString(3, trade.getCardId());
            stmt.setInt(4, trade.getType().ordinal());
            stmt.setFloat(5, trade.getMin_damage());
            stmt.executeUpdate();
        }
        return trade;
    }

    @Override
    public HashMap<String, Trade> read() throws SQLException {
        HashMap<String, Trade> trades = new HashMap<>();
        String query = "SELECT * FROM trade_offers";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Trade trade = new Trade(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        app.enums.card_type.values()[result.getInt(4)],
                        result.getFloat(5)
                );

                trades.put(trade.getId(), trade);
            }
            return trades;
        }
    }

    @Override
    public Trade readById(String id) throws SQLException {
        String query = "SELECT * FROM trade_offers WHERE id = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet result = stmt.executeQuery();
            if(result.next()) {
                return new Trade(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        app.enums.card_type.values()[result.getInt(4)],
                        result.getFloat(5)
                );
            }
            return null;
        }
    }

    @Override
    public void update(Trade trade) throws SQLException {
        String query = "UPDATE trade_offers SET username = ?, card_id = ?, type = ?, min_damage = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, trade.getUsername());
            stmt.setString(2, trade.getCardId());
            stmt.setInt(3, trade.getType().ordinal());
            stmt.setFloat(4, trade.getMin_damage());
            stmt.setString(5, trade.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Trade trade) throws SQLException {
        String query = "DELETE FROM trade_offers WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, trade.getId());
            stmt.executeUpdate();
        }
    }
}
