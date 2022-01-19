package ua.com.foxminded.university.domain.mapper;

import ua.com.foxminded.university.domain.dto.AbstractDto;
import ua.com.foxminded.university.domain.entity.IEntity;

public interface DtoMapper<T extends IEntity, D extends AbstractDto<D>> {

    D toDto(T entity);

    T toEntity(D dto);
}
