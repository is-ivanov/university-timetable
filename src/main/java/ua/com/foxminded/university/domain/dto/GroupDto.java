package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = true)
@Relation(itemRelation = "group", collectionRelation = "groups")
public class GroupDto extends GenericDto {

    @Size(max = 15)
    @NotBlank(message = "{group.name.not.blank}")
    String name;

    @NotNull
    boolean active;

    @NotNull
    Integer facultyId;
    String facultyName;

    public GroupDto(Integer id, String name, boolean active, Integer facultyId,
                    String facultyName) {
        super(id);
        this.name = name;
        this.active = active;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }
}
