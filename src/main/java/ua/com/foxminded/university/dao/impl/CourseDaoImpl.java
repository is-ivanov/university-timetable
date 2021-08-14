package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.dao.mapper.CourseMapper;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DAOException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:sql_query.properties")
@Repository
public class CourseDaoImpl implements CourseDao {

    private static final String QUERY_ADD = "course.add";
    private static final String QUERY_GET_ALL = "course.getAll";
    private static final String QUERY_GET_BY_ID = "course.getById";
    private static final String QUERY_UPDATE = "course.update";
    private static final String QUERY_DELETE = "course.delete";
    private static final String MESSAGE_COURSE_NOT_FOUND = "Course id(%s) not found";
    private static final String MESSAGE_UPDATE_COURSE_NOT_FOUND = "Can't update because course id(%s) not found";
    private static final String MESSAGE_DELETE_COURSE_NOT_FOUND = "Can't delete because course id(%s) not found";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Course course) {
        log.debug("Adding {}", course);
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                course.getName());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", course, e);
            throw new DAOException(e.getMessage(), e);
        }
        log.info("{} added successfully", course);
    }

    @Override
    public Optional<Course> getById(int id) {
        log.debug("Getting course by id({})", id);
        Course result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new CourseMapper(), id);
        } catch (DataAccessException e) {
            log.error("Course id({}) not found", id, e);
            throw new DAOException(String.format(MESSAGE_COURSE_NOT_FOUND, id), e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Course> getAll() {
        log.debug("Getting all courses");
        List<Course> courses = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL),
            new CourseMapper());
        log.info("Found {} courses", courses.size());
        return courses;
    }

    @Override
    public void update(Course course) {
        log.debug("Updating {}", course);
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            course.getName(), course.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", course);
            throw new DAOException(String.format(MESSAGE_UPDATE_COURSE_NOT_FOUND,
                course.getId()));
        } else {
            log.info("Update {}", course);
        }
    }

    @Override
    public void delete(Course course) {
        log.debug("Deleting {}", course);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), course.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", course);
            throw new DAOException(String.format(MESSAGE_DELETE_COURSE_NOT_FOUND,
                course.getId()));
        } else {
            log.info("Delete {}", course);
        }
    }

}
