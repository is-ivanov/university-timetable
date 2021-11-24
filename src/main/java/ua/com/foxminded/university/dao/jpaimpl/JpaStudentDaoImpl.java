package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
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
public class JpaStudentDaoImpl implements StudentDao {

    private static final String QUERY_GET_ALL = "Student.getAll";
    private static final String QUERY_GET_ALL_ACTIVE = "student.getAllActive";
    private static final String QUERY_GET_BY_ID = "student.getById";
    private static final String QUERY_UPDATE = "student.update";
    private static final String QUERY_DELETE_BY_ID = "Student.deleteById";
    private static final String QUERY_GET_ALL_BY_LESSON = "Student.getStudentsByLesson";
    private static final String QUERY_GET_ALL_BY_GROUP = "student.getStudentsByGroup";
    private static final String QUERY_GET_ALL_BY_FACULTY = "Student.getStudentsByFaculty";
    private static final String QUERY_GET_FREE_STUDENTS_BY_GROUP = "student.getFreeStudentsFromGroupOnLessonTime";
    private static final String MESSAGE_STUDENT_NOT_FOUND = "Student id(%d) not found";
    private static final String MESSAGE_UPDATE_STUDENT_NOT_FOUND = "Can't update because student id(%d) not found";
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
        log.info("Student [{} {} {}, active={}, group {}] saved successfully",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
    }

    @Override
    public Optional<Student> getById(int id) {
        log.debug("Getting student by id({})", id);
        Student student = entityManager.find(Student.class, id);
        log.info("Found {}", student);
        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = entityManager
            .createQuery(env.getProperty(QUERY_GET_ALL), Student.class)
            .getResultList();
        return students;
    }

    @Override
    public void update(Student student) {
        entityManager.merge(student);
    }

    @Override
    public void delete(Student student) {
        entityManager.remove(student);
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
            log.info("Delete student id({})", id);
        }
    }

    @Override
    public List<Student> getStudentsByLesson(Lesson lesson) {
        List<Student> students = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_LESSON), Student.class)
            .setParameter("lessonId", lesson.getId())
            .getResultList();
        return students;
    }

    @Override
    public List<Student> getStudentsByGroup(Group group) {
        return null;
    }

    @Override
    public List<Student> getStudentsByFaculty(Faculty faculty) {
        return null;
    }

    @Override
    public List<Student> getActiveStudents() {
        return null;
    }

    @Override
    public List<Student> getFreeStudentsFromGroup(int groupId, LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }
}
