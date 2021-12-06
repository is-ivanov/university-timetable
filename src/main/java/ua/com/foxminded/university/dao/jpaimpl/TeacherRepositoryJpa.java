package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.TeacherRepository;
import ua.com.foxminded.university.domain.entity.Teacher;
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
public class TeacherRepositoryJpa implements TeacherRepository {

    private static final String QUERY_GET_ALL = "teacher.getAll";
    private static final String QUERY_DELETE_BY_ID = "teacher.deleteById";
    private static final String QUERY_GET_FREE_TEACHERS = "teacher.getFreeTeachersOnLessonTime";
    private static final String QUERY_GET_ALL_BY_DEPARTMENT = "teacher.getTeachersByDepartment";
    private static final String QUERY_GET_ALL_BY_FACULTY = "teacher.getTeachersByFaculty";
    private static final String MESSAGE_DELETE_TEACHER_NOT_FOUND = "Can't delete because teacher id(%d) not found";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Teacher teacher) {
        log.debug("Saving {}", teacher);
        entityManager.persist(teacher);
        log.debug("{} saved successfully", teacher);
    }

    @Override
    public Optional<Teacher> getById(int id) {
        log.debug("Getting teacher by id({})", id);
        Teacher teacher = entityManager.find(Teacher.class, id);
        log.debug("Found {}", teacher);
        return Optional.ofNullable(teacher);
    }

    @Override
    public List<Teacher> getAll() {
        log.debug("Getting all teachers");
        List<Teacher> teachers = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL), Teacher.class)
            .getResultList();
        log.debug("Found {} teachers", teachers.size());
        return teachers;
    }

    @Override
    public void update(Teacher teacher) {
        entityManager.merge(teacher);
        log.debug("Update {}", teacher);
    }

    @Override
    public void delete(Teacher teacher) {
        entityManager.remove(teacher);
        log.debug("Delete {}", teacher);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete teacher id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_TEACHER_NOT_FOUND, id));
        } else {
            log.debug("Delete teacher id({})", id);
        }
    }

    @Override
    public List<Teacher> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers by department id({})", departmentId);
        List<Teacher> teachers = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_DEPARTMENT),
                Teacher.class)
            .setParameter("departmentId", departmentId)
            .getResultList();
        log.debug("Found {} teachers from department id({})", teachers.size(),
            departmentId);
        return teachers;
    }

    @Override
    public List<Teacher> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers by faculty id({})", facultyId);
        List<Teacher> teachers = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_BY_FACULTY),
                Teacher.class)
            .setParameter("facultyId", facultyId)
            .getResultList();
        log.debug("Found {} teachers from faculty id({})", teachers.size(),
            facultyId);
        return teachers;
    }

    @Override
    public List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        log.debug("Getting active teachers free from {} to {}", startTime, endTime);
        List<Teacher> freeTeachers = entityManager.createQuery(
                env.getProperty(QUERY_GET_FREE_TEACHERS),
                Teacher.class)
            .setParameter("time_start", startTime)
            .setParameter("time_end", endTime)
            .getResultList();
        log.debug("Found {} active free teachers", freeTeachers.size());
        return freeTeachers;
    }
}
