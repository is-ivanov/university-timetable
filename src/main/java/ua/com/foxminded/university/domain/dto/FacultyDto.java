package ua.com.foxminded.university.domain.dto;

import lombok.Value;
import ua.com.foxminded.university.domain.validator.CapitalLetter;
import ua.com.foxminded.university.domain.validator.OnCreate;
import ua.com.foxminded.university.domain.validator.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Value
public class FacultyDto {

    @Null(groups = OnCreate.class)
    @NotNull(groups = OnUpdate.class)
    Integer id;

    @NotBlank(message = "{faculty.name.not.blank}")
    @CapitalLetter
    String name;
}
