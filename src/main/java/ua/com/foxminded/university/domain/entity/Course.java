package ua.com.foxminded.university.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course extends GenericEntity {

    @NotBlank(message = "{course.name.not.blank}")
    @CapitalLetter
    @Column(name = "course_name", nullable = false, unique = true)
    private String name;

    public Course(String name) {
        this.name = name;
    }

    public Course(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public Course(Integer id, LocalDateTime createdTimestamp,
                  LocalDateTime updatedTimestamp, String name) {
        super(id, createdTimestamp, updatedTimestamp);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Course course = (Course) o;
        return getId() != null && Objects.equals(getId(), course.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
