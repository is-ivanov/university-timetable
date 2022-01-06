package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.Value;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
public class TeacherDto {

    Integer id;

    @Size(max = 100)
    @NotBlank(message = "{person.name.not.blank}")
    @CapitalLetter
    String firstName;

    @Size(max = 100)
    String patronymic;

    @Size(max = 100)
    @NotBlank(message = "{person.name.not.blank}")
    @CapitalLetter
    String lastName;

    @NotNull
    boolean active;

    String fullName;

    @NotNull
    int departmentId;

    String departmentName;
}
