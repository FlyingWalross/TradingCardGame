package app.daos;

import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class UserDao implements Dao<User, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public User create(User user) throws SQLException {
        String query = "insert into users (username, password_hash, name, elo, coins, wins, losses, bio, image) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getName());
            stmt.setInt(4, user.getElo());
            stmt.setInt(5, user.getCoins());
            stmt.setInt(6, user.getWins());
            stmt.setInt(7, user.getLosses());
            stmt.setString(8, user.getBio());
            stmt.setString(9, user.getImage());
            stmt.executeUpdate();

            return user;
        }
    }

    @Override
    public HashMap<String, User> read() throws SQLException {
        HashMap<String, User> users = new HashMap<>();
        String query = "SELECT * FROM users";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                User user = new User(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        result.getInt(7),
                        result.getString(8),
                        result.getString(9)
                );

                users.put(user.getUsername(), user);
            }
            return users;
        }
    }

    @Override
    public User readById(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";

        try(PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return new User(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getInt(5),
                        result.getInt(6),
                        result.getInt(7),
                        result.getString(8),
                        result.getString(9)
                );
            }
            return null;
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE users SET password_hash = ?, name = ?, elo = ?, coins = ?, wins = ?, losses = ?, bio = ?, image = ? WHERE username = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getName());
            stmt.setInt(3, user.getElo());
            stmt.setInt(4, user.getCoins());
            stmt.setInt(5, user.getWins());
            stmt.setInt(6, user.getLosses());
            stmt.setString(7, user.getBio());
            stmt.setString(8, user.getImage());
            stmt.setString(9, user.getUsername());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM users WHERE username = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.executeUpdate();
        }
    }
}
