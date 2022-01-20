package ua.com.foxminded.university.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

public interface Operations<T extends Serializable> {

    T findById(final int id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    T create(final T entity);

    T update(final int id, final T entity);

    void delete(final int id);


}
