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
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "group", collectionRelation = "groups")
public class GroupDto extends AbstractDto<GroupDto> {

    Integer id;

    @Size(max = 15)
    @NotBlank(message = "{group.name.not.blank}")
    String name;

    @NotNull
    boolean active;

    @NotNull
    Integer facultyId;
    String facultyName;

}
