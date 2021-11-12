package ua.com.foxminded.university.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lessons", indexes = {
    @Index(name = "idx_lesson_course_id", columnList = "course_id"),
    @Index(name = "idx_lesson_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_lesson_room_id", columnList = "room_id"),
    @Index(name = "idx_lesson_time_start", columnList = "time_start"),
    @Index(name = "idx_lesson_time_end", columnList = "time_end")
})
public class Lesson {

    @Id
    @GeneratedValue
    @Column(name = "lesson_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "time_start")
    private LocalDateTime timeStart;

    @Column(name = "time_end")
    private LocalDateTime timeEnd;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_lessons",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @ToString.Exclude
    private List<Student> students;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Lesson lesson = (Lesson) o;
        return id != null && Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
