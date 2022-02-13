package ua.com.foxminded.university.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_building", columnList = "building"),
    @Index(name = "idx_room_room_number", columnList = "room_number")
})
public class Room extends GenericEntity {

    @Size(max = 100)
    @NotBlank(message = "{room.building.not.blank}")
    @Column(name = "building", nullable = false, length = 100)
    private String building;

    @Size(max = 20)
    @NotBlank(message = "{room.number.not.blank}")
    @Column(name = "room_number", nullable = false, length = 20)
    private String number;

    public Room(String building, String number) {
        this.building = building;
        this.number = number;
    }

    public Room(Integer id, String building, String number) {
        super(id);
        this.building = building;
        this.number = number;
    }

    public String getBuildingAndRoom () {
        return this.building + " - " + this.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Room room = (Room) o;
        return getId() != null && Objects.equals(getId(), room.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
