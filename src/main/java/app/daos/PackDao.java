package app.daos;

import app.models.Pack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class PackDao implements Dao<Pack, Integer> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public PackDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public Pack create(Pack pack) throws SQLException {
        String query = "INSERT INTO packs DEFAULT VALUES RETURNING id";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            result.next();
            pack.setId(result.getInt(1));
        }

        savePackCards(pack);

        return pack;
    }

    @Override
    public HashMap<Integer, Pack> read() throws SQLException {
        HashMap<Integer, Pack> packs = new HashMap<>();
        String query = "SELECT * FROM packs";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Pack pack = new Pack(
                        result.getInt(1), new ArrayList<String>()
                );
                packs.put(pack.getId(), pack);
            }
        }

        query = "SELECT card_id FROM pack_cards WHERE pack_id = ?";
        for (Pack pack : packs.values()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, pack.getId());
                ResultSet result = stmt.executeQuery();
                while (result.next()) {
                    pack.getCardIDs().add(result.getString(1));
                }
            }
        }
        return packs;
    }

    @Override
    public Pack readById(Integer id) throws SQLException {
        String query = "SELECT card_id FROM pack_cards WHERE pack_id=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            Pack pack = new Pack(
                    id, new ArrayList<String>()
            );
            while (result.next()) {
                pack.getCardIDs().add(result.getString(1));
            }
            return pack;
        }
    }

    @Override
    public void update(Pack pack) throws SQLException {
        deletePackCards(pack);
        savePackCards(pack);
    }

    @Override
    public void delete(Pack pack) throws SQLException {
        deletePackCards(pack);

        String query = "DELETE FROM packs WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, pack.getId());
            stmt.executeUpdate();
        }
    }

    private void savePackCards(Pack pack) throws SQLException {
        String query = "INSERT INTO pack_cards (pack_id, card_id) VALUES (?, ?)";

        for (String cardID : pack.getCardIDs()) {
            try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
                stmt.setInt(1, pack.getId());
                stmt.setString(2, cardID);
                stmt.executeUpdate();
            }
        }
    }

    private void deletePackCards(Pack pack) throws SQLException {
        String query = "DELETE FROM pack_cards WHERE pack_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, pack.getId());
            stmt.executeUpdate();
        }
    }
}
