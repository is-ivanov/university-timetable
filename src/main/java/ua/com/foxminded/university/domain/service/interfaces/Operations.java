package ua.com.foxminded.university.domain.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface Operations<T extends Serializable> {

    T findById(int id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    T create(T entity);

    T update(int id, T entity);

    void delete(int id);

}
