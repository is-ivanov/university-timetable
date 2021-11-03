package ua.com.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    void add(T t);

    Optional<T> getById(int id);

    List<T> getAll();

    void update(T t);

    void delete(T t);

    void delete(int id);
}
