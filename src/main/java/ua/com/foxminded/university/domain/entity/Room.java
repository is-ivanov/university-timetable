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
@Table(name = "rooms")
public class Room {

    @Id
    @SequenceGenerator(
        name = "room-id-seq",
        sequenceName = "rooms_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "room-id-seq"
    )
    @Column(name = "room_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
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
