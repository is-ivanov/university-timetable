package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = true)
@Relation(itemRelation = "department", collectionRelation = "departments")
public class DepartmentDto extends GenericDto {

    @NotBlank(message = "{department.name.not.blank}")
    @CapitalLetter
    String name;

    @NotNull
    Integer facultyId;
    String facultyName;

    @Builder
    public DepartmentDto(Integer id, String name, Integer facultyId, String facultyName) {
        super(id);
        this.name = name;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }
}
