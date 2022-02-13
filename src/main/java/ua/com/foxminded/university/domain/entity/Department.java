package ua.com.foxminded.university.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "departments", indexes = {
    @Index(name = "idx_department", columnList = "department_name"),
    @Index(name = "idx_department_faculty_id", columnList = "faculty_id")
})
public class Department extends GenericEntity {

    @NotBlank(message = "{department.name.not.blank}")
    @CapitalLetter
    @Column(name = "department_name", nullable = false)
    private String name;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_faculty"))
    @ToString.Exclude
    private Faculty faculty;

    public Department(Integer id, LocalDateTime createdTimestamp,
                      LocalDateTime updatedTimestamp,
                      String name, Faculty faculty) {
        super(id, createdTimestamp, updatedTimestamp);
        this.name = name;
        this.faculty = faculty;
    }

    public Department(String name, Faculty faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    public Department(Integer id, String name, Faculty faculty) {
        super(id);
        this.name = name;
        this.faculty = faculty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Department department = (Department) o;
        return getId() != null && Objects.equals(getId(), department.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
