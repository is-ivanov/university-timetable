package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;

@Value
@EqualsAndHashCode(callSuper = false)
public class CourseDto extends AbstractDto<CourseDto> {

    Integer id;

    @NotBlank(message = "{course.name.not.blank}")
    @CapitalLetter
    String name;

}
