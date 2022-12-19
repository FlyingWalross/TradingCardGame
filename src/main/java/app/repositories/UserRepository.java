package app.repositories;

import app.daos.UserDao;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserRepository implements Repository<User, String> {
    UserDao userDao;

    public UserRepository(UserDao userDao) {
        setUserDao(userDao);
    }

    @Override
    public ArrayList<User> getAll() {
        try {
            return new ArrayList(getUserDao().read().values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getById(String username) {
        try {
            return getUserDao().read().get(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User create(User user) throws SQLException {
            return getUserDao().create(user);
    }
}
