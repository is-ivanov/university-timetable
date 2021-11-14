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
@Table(name = "departments", indexes = {
    @Index(name = "idx_department", columnList = "department_name"),
    @Index(name = "idx_department_faculty_id", columnList = "faculty_id")
})
public class Department {

    @Id
    @GeneratedValue
    @Column(name = "department_id")
    private Integer id;

    @Column(name = "department_name", nullable = false)
    private String name;

    public Department(String name, Faculty faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Department that = (Department) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
