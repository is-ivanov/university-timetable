package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.common.Operations;
import ua.com.foxminded.university.domain.entity.IEntity;

public interface Service<T extends IEntity> extends Operations<T> {
}
