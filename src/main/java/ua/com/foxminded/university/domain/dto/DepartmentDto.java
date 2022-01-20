package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@EqualsAndHashCode(callSuper = false)
public class DepartmentDto extends AbstractDto<DepartmentDto> {

    Integer id;

    @NotBlank(message = "{department.name.not.blank}")
    @CapitalLetter
    String name;

    @NotNull
    Integer facultyId;

    String facultyName;

}
