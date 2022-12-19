package app.repositories;

import java.sql.SQLException;
import java.util.ArrayList;

public interface Repository<T, ID> {
    ArrayList<T> getAll();
    T getById(ID id);

    T create(T t) throws SQLException;
}
