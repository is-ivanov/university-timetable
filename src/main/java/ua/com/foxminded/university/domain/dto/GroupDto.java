package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@EqualsAndHashCode(callSuper = false)
public class GroupDto extends AbstractDto<GroupDto> {

    Integer id;

    @Size(max = 15)
    @NotBlank(message = "{group.name.not.blank}")
    String name;

    @NotNull
    boolean active;

    Integer facultyId;
    String facultyName;

}
