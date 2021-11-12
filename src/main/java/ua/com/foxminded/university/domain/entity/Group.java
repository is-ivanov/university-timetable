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
@Table(name = "groups", indexes = {
    @Index(name = "idx_group_group_name", columnList = "group_name", unique = true),
    @Index(name = "idx_group_group_active", columnList = "group_active"),
    @Index(name = "idx_group_faculty_id", columnList = "faculty_id")
})
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private Integer id;

    @Column(name = "group_name", nullable = false, unique = true, length = 15)
    private String name;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "group_active", nullable = false)
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Group group = (Group) o;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
