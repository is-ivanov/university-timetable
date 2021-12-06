package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.StudentRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class StudentRepositoryJpa implements StudentRepository {

    private static final String QUERY_GET_ALL = "student.getAll";
    private static final String QUERY_GET_ALL_ACTIVE = "student.getAllActive";
    private static final String QUERY_DELETE_BY_ID = "student.deleteById";
    private static final String QUERY_GET_ALL_BY_LESSON = "student.getStudentsByLesson";
    private static final String QUERY_GET_ALL_BY_GROUP = "student.getStudentsByGroup";
    private static final String QUERY_GET_ALL_BY_FACULTY = "student.getStudentsByFaculty";
    private static final String QUERY_GET_FREE_STUDENTS_BY_GROUP = "student.getFreeStudentsFromGroupOnLessonTime";
    private static final String MESSAGE_DELETE_STUDENT_NOT_FOUND = "Can't delete because student id(%d) not found";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Student student) {
        log.debug("Saving student [{} {} {}, active={}, group {}]",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
        entityManager.persist(student);
        log.debug("Student [{} {} {}, active={}, group {}] saved successfully",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
    }

    @Override
    public Optional<Student> getById(int id) {
        log.debug("Getting student by id({})", id);
        Student student = entityManager.find(Student.class, id);
        log.debug("Found {}", student);
        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> getAll() {
        log.debug("Getting all students");
        List<Student> students = entityManager
            .createQuery(env.getProperty(QUERY_GET_ALL), Student.class)
            .getResultList();
        log.debug("Found {} students", students.size());
        return students;
    }

    @Override
    public void update(Student student) {
        entityManager.merge(student);
        log.debug("Update {}", student);
    }

    @Override
    public void delete(Student student) {
        entityManager.remove(student);
        log.debug("Delete {}", student);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete student id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_STUDENT_NOT_FOUND, id));
        } else {
            log.debug("Delete student id({})", id);
        }
    }

    @Override
    public List<Student> getStudentsByLesson(Lesson lesson) {
        log.debug("Getting students by lesson id({})", lesson.getId());
        List<Student> students = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_LESSON), Student.class)
            .setParameter("lessonId", lesson.getId())
            .getResultList();
        log.debug("Found {} students from lesson id({})", students.size(),
            lesson.getId());
        return students;
    }

    @Override
    public List<Student> getStudentsByGroup(Group group) {
        log.debug("Getting students by group id({})", group.getId());
        List<Student> students = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_GROUP), Student.class)
            .setParameter("groupId", group.getId())
            .getResultList();
        log.debug("Found {} students from group id({})", students.size(),
            group.getId());
        return students;
    }

    @Override
    public List<Student> getStudentsByFaculty(Faculty faculty) {
        List<Student> students = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_FACULTY), Student.class)
            .setParameter("facultyId", faculty.getId())
            .getResultList();
        log.debug("Found {} students from ({})", students.size(), faculty);
        return students;
    }

    @Override
    public List<Student> getActiveStudents() {
        log.debug("Getting all active students");
        List<Student> students = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_ACTIVE), Student.class)
            .getResultList();
        log.debug("Found {} students", students.size());
        return students;
    }

    @Override
    public List<Student> getFreeStudentsFromGroup(int groupId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, startTime, endTime);
        List<Student> freeStudents = entityManager.createQuery(
                env.getProperty(QUERY_GET_FREE_STUDENTS_BY_GROUP), Student.class)
            .setParameter("groupId", groupId)
            .setParameter("time_start", startTime)
            .setParameter("time_end", endTime)
            .getResultList();
        log.debug("Found {} free student from group id({})",
            freeStudents.size(), groupId);
        return freeStudents;
    }
}
