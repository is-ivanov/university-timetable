package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = false)
public abstract class GenericDto extends RepresentationModel<GenericDto>
    implements Serializable {

    Integer id;

}
