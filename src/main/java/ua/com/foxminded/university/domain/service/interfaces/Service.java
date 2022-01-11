package ua.com.foxminded.university.domain.service.interfaces;

import java.util.List;

public interface Service<T, T1> {

    T save(T t);

    T1 getById(int id);

    List<T1> getAll();

    void delete(int id);
}
