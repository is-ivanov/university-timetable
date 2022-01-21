package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "course", collectionRelation = "courses")
public class CourseDto extends AbstractDto<CourseDto> {

    Integer id;

    @NotBlank(message = "{course.name.not.blank}")
    @CapitalLetter
    String name;

}
