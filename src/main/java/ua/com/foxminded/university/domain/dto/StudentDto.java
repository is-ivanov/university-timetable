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
@NonFinal
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "student", collectionRelation = "students")
public class StudentDto extends GenericDto {

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

    @Builder
    public StudentDto(Integer id, String firstName, String patronymic,
                      String lastName, boolean active, String fullName,
                      Integer groupId, String groupName) {
        super(id);
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.lastName = lastName;
        this.active = active;
        this.fullName = fullName;
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
