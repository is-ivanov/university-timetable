package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.mapper.CourseMapper;
import ua.com.foxminded.university.exception.DAOException;

@Slf4j
@Repository
@PropertySource("classpath:sql_query.properties")
public class CourseDaoImpl implements CourseDao {

    private static final String QUERY_ADD = "course.add";
    private static final String QUERY_GET_ALL = "course.getAll";
    private static final String QUERY_GET_BY_ID = "course.getById";
    private static final String QUERY_UPDATE = "course.update";
    private static final String QUERY_DELETE = "course.delete";
    private static final String MESSAGE_COURSE_NOT_FOUND = "Course not found: ";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Autowired
    public CourseDaoImpl(JdbcTemplate jdbcTemplate, Environment env) {
        this.jdbcTemplate = jdbcTemplate;
        this.env = env;
    }

    @Override
    public void add(Course course) {
        log.debug("Start adding {}", course);
        try {
            int numberAddedRows = jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                course.getName());

            if (numberAddedRows == 0) {
                log.warn("{} not added", course);
            }else {
                log.info("{} added successfully", course);
            }
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", course, e);
            throw new DAOException(e.getMessage(), e);
        }
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
            throw new DAOException(MESSAGE_COURSE_NOT_FOUND + id, e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Course> getAll() {
        log.debug("Getting all courses");
        List<Course> courses = jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
            new CourseMapper());
        log.info("Found {} courses", courses.size());
        return courses;
    }

    @Override
    public void update(Course course) {
        log.debug("Updating {}", course);
        int numberUpdatedRows = jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
            course.getName(), course.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", course);
        } else {
            log.info("Update {} course. {}", numberUpdatedRows, course);
        }
    }

    @Override
    public void delete(Course course) {
        log.debug("Deleting {}", course);
        int numberDeletedRows = jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
            course.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", course);
        } else {
            log.info("Delete {} course. {}", numberDeletedRows, course);
        }
    }

}
