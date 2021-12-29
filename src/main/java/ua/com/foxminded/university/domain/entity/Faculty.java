package ua.com.foxminded.university.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "faculties", indexes = {
    @Index(name = "idx_faculty_faculty_name", columnList = "faculty_name", unique = true)
})
public class Faculty {

    @Id
    @GeneratedValue
    @Column(name = "faculty_id")
    private Integer id;

    @NotBlank(message = "{faculty.name.not.blank}")
    @CapitalLetter
    @Column(name = "faculty_name", nullable = false, unique = true)
    private String name;

    public Faculty(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Faculty faculty = (Faculty) o;
        return id != null && Objects.equals(id, faculty.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
