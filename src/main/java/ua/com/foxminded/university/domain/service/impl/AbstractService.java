package ua.com.foxminded.university.domain.service.impl;

import com.google.common.base.Preconditions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.domain.entity.IEntity;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
public abstract class AbstractService<T extends IEntity> implements Service<T> {

    @Override
    @Transactional(readOnly = true)
    public T findById(int id) {
        return getRepo().findById(id)
            .orElseThrow(() ->
                new MyEntityNotFoundException(getEntityName(), "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAllSortedAndPaginated(Pageable pageable) {
        return getRepo().findAll(pageable);
    }

    @Override
    public T create(T entity) {
        Preconditions.checkNotNull(entity);
        return getRepo().save(entity);
    }

    @Override
    public T update(int id, T entity) {
        Preconditions.checkNotNull(entity);
        if (!getRepo().existsById(id)) {
            throw new MyEntityNotFoundException(getEntityName(), "id", id);
        }
        return getRepo().save(entity);
    }

    @Override
    public void delete(int id) {
//        if (!getRepo().existsById(id)) {
//            throw new MyEntityNotFoundException(getEntityName(), "id", id);
//        }
        getRepo().deleteById(id);
    }

    protected abstract JpaRepository<T, Integer> getRepo();

    private String getEntityName() {
        return ((Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
    }
}
