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

// The City Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a City
// and our Type (ID) will be an Integer
// See City Dao for details
public class UserDao implements Dao<User, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public UserDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public User create(User user) throws SQLException {
        String query = "insert into users (username, password_hash, name) values (?, ?, ?);";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPasswordHash());
        stmt.setString(3, user.getName());
        stmt.executeUpdate();

        return user;
    }

    @Override
    public HashMap<String, User> read() throws SQLException {
        HashMap<String, User> users = new HashMap<>();
        String query = "SELECT * FROM users";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        ResultSet result = stmt.executeQuery();
        while(result.next()) {
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

    @Override
    public void update() throws SQLException {

    }

    @Override
    public void delete() throws SQLException {

    }
}
