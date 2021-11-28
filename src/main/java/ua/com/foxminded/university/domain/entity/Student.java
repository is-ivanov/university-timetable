package ua.com.foxminded.university.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_active", columnList = "active")
})
public class Student extends Person {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @ToString.Exclude
    private Group group;

    @ToString.Exclude
    @ManyToMany(mappedBy = "students")
    private Set<Lesson> lessons = new HashSet<>();

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
        lesson.getStudents().add(this);
    }

    public void removeLesson(Lesson lesson) {
        this.lessons.remove(lesson);
        lesson.getStudents().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Student student = (Student) o;
        return getId() != null && Objects.equals(getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
