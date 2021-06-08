package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.mapper.CourseMapper;
import ua.com.foxminded.university.exception.DAOException;

@Component
@PropertySource("classpath:sql_query.properties")
public class CourseDaoImpl implements CourseDao {

    private static final String QUERY_ADD = "course.add";
    private static final String QUERY_GET_ALL = "course.getAll";
    private static final String QUERY_GET_BY_ID = "course.getById";
    private static final String QUERY_UPDATE = "course.update";
    private static final String QUERY_DELETE = "course.delete";
    private static final String MESSAGE_COURSE_NOT_FOUND = "Course not found: ";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private Environment env;

    @Override
    public void add(Course course) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                course.getName());
    }

    @Override
    public Optional<Course> getById(int id) throws DAOException {
        Course result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new CourseMapper(), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_COURSE_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Course> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new CourseMapper());
    }

    @Override
    public void update(Course course) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                course.getName(), course.getId());
    }

    @Override
    public void delete(Course course) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                course.getId());
    }


}
