package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;


@Value
@NonFinal
@EqualsAndHashCode(callSuper = true)
@Relation(itemRelation = "faculty", collectionRelation = "faculties")
public class FacultyDto extends GenericDto {

    @NotBlank(message = "{faculty.name.not.blank}")
    @CapitalLetter
    String name;

    @Builder
    public FacultyDto(Integer id, String name) {
        super(id);
        this.name = name;
    }
}
