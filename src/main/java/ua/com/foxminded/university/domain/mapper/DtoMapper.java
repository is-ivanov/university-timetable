package ua.com.foxminded.university.domain.mapper;

import ua.com.foxminded.university.domain.dto.GenericDto;
import ua.com.foxminded.university.domain.entity.GenericEntity;

import java.util.List;

public interface DtoMapper<T extends GenericEntity, D extends GenericDto> {

    D toDto(T entity);

    T toEntity(D dto);

    List<D> toDtos(Iterable<T> entities);

    List<T> toEntities(Iterable<D> dtos);
}
