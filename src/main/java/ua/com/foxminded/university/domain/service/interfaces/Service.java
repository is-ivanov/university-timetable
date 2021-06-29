package ua.com.foxminded.university.domain.service.interfaces;

import java.util.List;

import ua.com.foxminded.university.exception.ServiceException;

public interface Service<T> {

    void add(T t) throws ServiceException;

    T getById(int id) throws ServiceException;

    List<T> getAll();

    void update(T t) throws ServiceException;

    void delete(T t);
}
