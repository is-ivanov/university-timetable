package ua.com.foxminded.university.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "faculties", indexes = {
    @Index(name = "idx_faculty_faculty_name", columnList = "faculty_name", unique = true)
})
public class Faculty extends GenericEntity {

    @Column(name = "faculty_name", nullable = false, unique = true)
    private String name;

    public Faculty(String name) {
        this.name = name;
    }

    public Faculty(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Faculty faculty = (Faculty) o;
        return getId() != null && Objects.equals(getId(), faculty.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
