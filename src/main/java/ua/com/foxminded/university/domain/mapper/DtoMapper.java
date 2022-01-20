package ua.com.foxminded.university.domain.mapper;

import ua.com.foxminded.university.domain.dto.AbstractDto;
import ua.com.foxminded.university.domain.entity.IEntity;

import java.util.List;

public interface DtoMapper<T extends IEntity, D extends AbstractDto<D>> {

    D toDto(T entity);

    T toEntity(D dto);

    List<D> toDtos(Iterable<T> entities);

    List<T> toEntities(Iterable<D> dtos);
}
