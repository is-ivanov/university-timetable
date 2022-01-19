package ua.com.foxminded.university.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "groups", indexes = {
    @Index(name = "idx_group_group_name", columnList = "group_name", unique = true),
    @Index(name = "idx_group_group_active", columnList = "group_active"),
    @Index(name = "idx_group_faculty_id", columnList = "faculty_id")
})
public class Group implements IEntity {

    @Value("${application.validation.group.max-number-students}")
    private static final int MAX_NUMBER_STUDENTS = 10;

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private Integer id;

    @Size(max = 15)
    @NotBlank(message = "{group.name.not.blank}")
    @Column(name = "group_name", nullable = false, unique = true, length = 15)
    private String name;

    @NotNull
    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_faculty"))
    private Faculty faculty;

    @NotNull
    @Column(name = "group_active", nullable = false)
    private boolean active;

    @Size(max = 25, message = "{group.students.size}")
    @OneToMany(mappedBy = "group", orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private Set<Student> students = new HashSet<>();

    public Group(String name, Faculty faculty, boolean active) {
        this.name = name;
        this.faculty = faculty;
        this.active = active;
    }

    public Group(Integer id, String name, Faculty faculty, boolean active) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
        this.active = active;
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setGroup(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setGroup(null);
    }

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
