package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
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
public class RoomDto extends GenericDto {

    @Size(max = 100)
    @NotBlank(message = "{room.building.not.blank}")
    String building;

    @Size(max = 20)
    @NotBlank(message = "{room.number.not.blank}")
    String number;

    String buildingAndRoom;

    @Builder
    public RoomDto(Integer id, String building, String number, String buildingAndRoom) {
        super(id);
        this.building = building;
        this.number = number;
        this.buildingAndRoom = buildingAndRoom;
    }
}
