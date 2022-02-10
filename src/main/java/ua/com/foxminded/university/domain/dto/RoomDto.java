package ua.com.foxminded.university.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = false)
@Relation(itemRelation = "room", collectionRelation = "rooms")
public class RoomDto extends AbstractDto<RoomDto> {

    Integer id;

    @Size(max = 100)
    @NotBlank(message = "{room.building.not.blank}")
    String building;

    @Size(max = 20)
    @NotBlank(message = "{room.number.not.blank}")
    String number;

    String buildingAndRoom;
}
