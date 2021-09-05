package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.dao.mapper.TeacherMapper;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.DAOException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class TeacherDaoImpl implements TeacherDao {

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

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Teacher teacher) {
        log.debug("Adding teacher [{} {} {}, active={}, department {}]",
            teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                teacher.getFirstName(), teacher.getLastName(),
                teacher.getPatronymic(), teacher.isActive(),
                teacher.getDepartment().getId());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", teacher, e);
            throw new DAOException(e.getMessage(), e);
        }
        log.info("Teacher [{} {} {}, active={}, department {}] added " +
                "successfully", teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
    }

    @Override
    public Optional<Teacher> getById(int id) {
        log.debug("Getting teacher by id({})", id);
        Teacher result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new TeacherMapper(), id);
        } catch (DataAccessException e) {
            log.error("Teacher id({}) not found", id, e);
            throw new DAOException(String.format(MESSAGE_TEACHER_NOT_FOUND, id),
                e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Teacher> getAll() {
        log.debug("Getting all teachers");
        List<Teacher> teachers = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), new TeacherMapper());
        log.info("Found {} teachers", teachers.size());
        return teachers;
    }

    @Override
    public void update(Teacher teacher) {
        log.debug("Updating teacher [id={}, {} {} {}, active={}]",
            teacher.getId(), teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive());
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            teacher.getFirstName(), teacher.getLastName(),
            teacher.getPatronymic(), teacher.isActive(),
            teacher.getDepartment().getId(), teacher.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update teacher id({})", teacher.getId());
            throw new DAOException(String.format(MESSAGE_UPDATE_TEACHER_NOT_FOUND,
                teacher.getId()));
        } else {
            log.info("Update teacher id({})", teacher.getId());
        }
    }

    @Override
    public void delete(Teacher teacher) {
        log.debug("Deleting teacher [id={}, {} {} {}, active={}]",
            teacher.getId(), teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive());
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), teacher.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete teacher id({})", teacher.getId());
            throw new DAOException(String.format(MESSAGE_DELETE_TEACHER_NOT_FOUND,
                teacher.getId()));
        } else {
            log.info("Delete teacher id({})", teacher.getId());
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting teacher id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete teacher id({})", id);
            throw new DAOException(String.format(MESSAGE_DELETE_TEACHER_NOT_FOUND,
                id));
        } else {
            log.info("Delete teacher id({})", id);
        }
    }

    @Override
    public List<Teacher> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers by department id({})", departmentId);
        List<Teacher> teachers = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_DEPARTMENT),
            new TeacherMapper(), departmentId);
        log.info("Found {} teachers from department id({})", teachers.size(), departmentId);
        return teachers;
    }

    @Override
    public List<Teacher> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers by faculty id({})", facultyId);
        List<Teacher> teachers = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_BY_FACULTY),
            new TeacherMapper(), facultyId);
        log.info("Found {} teachers from faculty id({})", teachers.size(), facultyId);
        return teachers;
    }

    @Override
    public List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        log.debug("Getting active teachers free from {} to {}", startTime, endTime);
        List<Teacher> freeTeachers = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_FREE_TEACHERS), new TeacherMapper(),
            startTime, endTime, startTime, endTime);
        log.info("Found {} active free teachers", freeTeachers.size());
        return freeTeachers;
    }
}
