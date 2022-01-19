package ua.com.foxminded.university.domain.dto;

import org.springframework.hateoas.RepresentationModel;

public abstract class AbstractDto<T extends IDto>
    extends RepresentationModel<AbstractDto<T>> implements IDto {

}
