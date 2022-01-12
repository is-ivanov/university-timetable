package ua.com.foxminded.university.domain.dto;

import lombok.Value;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;

@Value
public class FacultyDto {

//    @Null(groups = OnCreate.class)
//    @NotNull(groups = OnUpdate.class)
    Integer id;

    @NotBlank(message = "{faculty.name.not.blank}")
    @CapitalLetter
    String name;
}
