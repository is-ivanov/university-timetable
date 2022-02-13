package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.GenericEntity;

public interface Service<T extends GenericEntity> extends Operations<T> {
}
