package ua.com.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;

import ua.com.foxminded.university.exception.DAOException;

public interface Dao<T> {

    void add(T t);

    Optional<T> getById(int id) throws DAOException;

    List<T> getAll();

    void update(T t);

    void delete(T t);
}
