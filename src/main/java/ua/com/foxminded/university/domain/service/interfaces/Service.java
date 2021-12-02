package ua.com.foxminded.university.domain.service.interfaces;

import java.util.List;

import ua.com.foxminded.university.exception.ServiceException;

public interface Service<T, T1> {

    void add(T t) throws ServiceException;

    T1 getById(int id) throws ServiceException;

    List<T1> getAll();

    void update(T t) throws ServiceException;

    void delete(T t);

    void delete(int id);
}
