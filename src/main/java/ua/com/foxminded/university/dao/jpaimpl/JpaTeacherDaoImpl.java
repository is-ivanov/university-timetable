package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.entity.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class JpaTeacherDaoImpl implements TeacherDao {

    private static final String QUERY_ADD = "teacher.add";
    private static final String QUERY_GET_ALL = "teacher.getAll";
    private static final String QUERY_GET_BY_ID = "teacher.getById";
    private static final String QUERY_UPDATE = "teacher.update";
    private static final String QUERY_DELETE = "teacher.delete";
    private static final String QUERY_GET_FREE_TEACHERS = "teacher.getFreeTeachersOnLessonTime";
    private static final String QUERY_GET_ALL_BY_DEPARTMENT = "teacher.getTeachersByDepartment";
    private static final String QUERY_GET_ALL_BY_FACULTY = "teacher.getTeachersByFaculty";
    private static final String MESSAGE_TEACHER_NOT_FOUND = "Teacher id(%d) not found";
    private static final String MESSAGE_UPDATE_TEACHER_NOT_FOUND = "Can't update because teacher id(%d) not found";
    private static final String MESSAGE_DELETE_TEACHER_NOT_FOUND = "Can't delete because teacher id(%d) not found";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Teacher teacher) {
        entityManager.persist(teacher);
    }

    @Override
    public Optional<Teacher> getById(int id) {
        Teacher teacher = entityManager.find(Teacher.class, id);
        return Optional.ofNullable(teacher);
    }

    @Override
    public List<Teacher> getAll() {
        List<Teacher> teachers = entityManager.createQuery(
            env.getProperty(QUERY_GET_ALL), Teacher.class)
            .getResultList();
        return teachers;
    }

    @Override
    public void update(Teacher teacher) {

    }

    @Override
    public void delete(Teacher teacher) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Teacher> getAllByDepartment(int departmentId) {
        return null;
    }

    @Override
    public List<Teacher> getAllByFaculty(int facultyId) {
        return null;
    }

    @Override
    public List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }
}
