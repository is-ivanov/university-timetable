package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.server.core.Relation;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.validation.constraints.NotBlank;

@Value
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "faculty", collectionRelation = "faculties")
public class FacultyDto extends AbstractDto<FacultyDto> {

    Integer id;

    @NotBlank(message = "{faculty.name.not.blank}")
    @CapitalLetter
    String name;

}
