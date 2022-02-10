package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder
@NonFinal
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "student", collectionRelation = "students")
public class StudentDto extends AbstractDto<StudentDto> {

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
    Integer groupId;

    String groupName;
}
