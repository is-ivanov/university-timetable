package ua.com.foxminded.university.domain.entity;

import ua.com.foxminded.university.domain.common.IGetId;
import ua.com.foxminded.university.domain.common.ISetId;

import java.io.Serializable;

public interface IEntity extends IGetId, ISetId, Serializable {

}
