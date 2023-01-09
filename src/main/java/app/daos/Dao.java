package app.daos;

import java.sql.SQLException;
import java.util.HashMap;

public interface Dao<T, ID> {
    T create(T t) throws SQLException;
    HashMap<ID, T> read() throws SQLException;
    T readById(ID id) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
}
