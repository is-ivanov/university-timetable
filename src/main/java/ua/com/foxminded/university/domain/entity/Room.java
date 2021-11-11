package ua.com.foxminded.university.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_building", columnList = "building"),
    @Index(name = "idx_room_room_number", columnList = "room_number")
})
public class Room {

    @Id
    @GeneratedValue
    @Column(name = "room_id")
    private Integer id;

    @Column(name = "building", nullable = false)
    private String building;

    @Column(name = "room_number", nullable = false)
    private String number;

    public String getBuildingAndRoom () {
        return this.building + " - " + this.number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Room room = (Room) o;
        return id != null && Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
