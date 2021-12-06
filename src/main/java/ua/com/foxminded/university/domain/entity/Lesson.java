package ua.com.foxminded.university.domain.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_course"))
    private Course course;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_teacher"))
    private Teacher teacher;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_room"))
    private Room room;

    @Column(name = "time_start")
    private LocalDateTime timeStart;

    @Column(name = "time_end")
    private LocalDateTime timeEnd;

    @ManyToMany()
    @JoinTable(name = "students_lessons",
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id"),
        foreignKey = @ForeignKey(name = "fk_lesson"),
        inverseForeignKey = @ForeignKey(name = "fk_student"))
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    public Lesson(Course course, Teacher teacher, Room room,
                  LocalDateTime timeStart, LocalDateTime timeEnd) {
        this.course = course;
        this.teacher = teacher;
        this.room = room;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public void addStudent(Student student) {
        this.students.add(student);
        student.getLessons().add(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getLessons().remove(this);
    }

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
