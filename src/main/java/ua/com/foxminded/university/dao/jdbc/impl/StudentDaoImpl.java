package ua.com.foxminded.university.dao.jdbc.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.dao.jdbc.mapper.StudentMapper;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.exception.DaoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class StudentDaoImpl implements StudentDao {

    private static final String QUERY_ADD = "student.add";
    private static final String QUERY_GET_ALL = "student.getAll";
    private static final String QUERY_GET_ALL_ACTIVE = "student.getAllActive";
    private static final String QUERY_GET_BY_ID = "student.getById";
    private static final String QUERY_UPDATE = "student.update";
    private static final String QUERY_DELETE = "student.delete";
    private static final String QUERY_GET_ALL_BY_LESSON = "student.getStudentsByLesson";
    private static final String QUERY_GET_ALL_BY_GROUP = "student.getStudentsByGroup";
    private static final String QUERY_GET_ALL_BY_FACULTY = "student.getStudentsByFaculty";
    private static final String QUERY_GET_FREE_STUDENTS_BY_GROUP = "student.getFreeStudentsFromGroupOnLessonTime";
    private static final String MESSAGE_STUDENT_NOT_FOUND = "Student id(%d) not found";
    private static final String MESSAGE_UPDATE_STUDENT_NOT_FOUND = "Can't update because student id(%d) not found";
    private static final String MESSAGE_DELETE_STUDENT_NOT_FOUND = "Can't delete because student id(%d) not found";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Student student) {
        log.debug("Adding student [{} {} {}, active={}, group {}]",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                student.getFirstName(), student.getLastName(),
                student.getPatronymic(), student.isActive(),
                student.getGroup().getId());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", student, e);
            throw new DaoException(e.getMessage(), e);
        }
        log.info("Student [{} {} {}, active={}, group {}] added successfully",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
    }

    @Override
    public Optional<Student> getById(int id) {
        log.debug("Getting student by id({})", id);
        Student result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new StudentMapper(), id);
        } catch (DataAccessException e) {
            log.error("Student id({}) not found", id, e);
            throw new DaoException(String.format(MESSAGE_STUDENT_NOT_FOUND, id),
                e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Student> getAll() {
        log.debug("Getting all students");
        List<Student> students = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), new StudentMapper());
        log.info("Found {} students", students.size());
        return students;
    }

    @Override
    public void update(Student student) {
        log.debug("Updating student [id={}, {} {} {}, active={}]",
            student.getId(), student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive());
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            student.getFirstName(), student.getLastName(),
            student.getPatronymic(), student.isActive(),
            student.getGroup().getId(),
            student.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update student id({})", student.getId());
            throw new DaoException(String.format(MESSAGE_UPDATE_STUDENT_NOT_FOUND,
                student.getId()));
        } else {
            log.info("Update student id({})", student.getId());
        }
    }

    @Override
    public void delete(Student student) {
        log.debug("Deleting student [id={}, {} {} {}, active={}]",
            student.getId(), student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive());
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), student.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete student id({})", student.getId());
            throw new DaoException(String.format(MESSAGE_DELETE_STUDENT_NOT_FOUND,
                student.getId()));
        } else {
            log.info("Delete student id({})", student.getId());
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting student id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete student id({})", id);
            throw new DaoException(String.format(MESSAGE_DELETE_STUDENT_NOT_FOUND,
                id));
        } else {
            log.info("Delete student id({})", id);
        }
    }

    @Override
    public List<Student> getStudentsByLesson(Lesson lesson) {
        log.debug("Getting students by lesson id({})", lesson.getId());
        List<Student> students = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_LESSON),
            new StudentMapper(), lesson.getId());
        log.info("Found {} students from lesson id({})", students.size(),
            lesson.getId());
        return students;
    }

    @Override
    public List<Student> getStudentsByGroup(Group group) {
        log.debug("Getting students by group id({})", group.getId());
        List<Student> students = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_GROUP),
            new StudentMapper(), group.getId());
        log.info("Found {} students from group id({})", students.size(),
            group.getId());
        return students;
    }

    @Override
    public List<Student> getActiveStudents() {
        log.debug("Getting all active students");
        List<Student> students = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_ACTIVE), new StudentMapper());
        log.info("Found {} students", students.size());
        return students;
    }

    @Override
    public List<Student> getStudentsByFaculty(Faculty faculty) {
        log.debug("Getting students from ({})", faculty);
        List<Student> students = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_FACULTY),
            new StudentMapper(), faculty.getId());
        log.info("Found {} students from ({})", students.size(), faculty);
        return students;
    }

    @Override
    public List<Student> getFreeStudentsFromGroup(int groupId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, startTime, endTime);
        List<Student> freeStudents = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_FREE_STUDENTS_BY_GROUP),
            new StudentMapper(), groupId, startTime, endTime);
        log.info("Found {} free student from group id({})", freeStudents.size(), groupId);
        return freeStudents;
    }
}