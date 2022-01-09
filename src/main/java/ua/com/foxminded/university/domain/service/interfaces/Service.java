package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

public interface Service<T, T1> {

    T save(T t) throws ServiceException;

    T1 getById(int id) throws ServiceException;

    List<T1> getAll();

    void delete(int id);
}
