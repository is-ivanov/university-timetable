package ua.com.foxminded.university.domain.service.interfaces;

import java.util.List;

public interface OldService<T> {

    T save(T t);

    T getById(int id);

    List<T> getAll();

    void delete(int id);
}