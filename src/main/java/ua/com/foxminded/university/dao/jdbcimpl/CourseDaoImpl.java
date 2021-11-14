package ua.com.foxminded.university.dao.jdbcimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.dao.mapper.CourseMapper;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DaoException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:sql_query.properties")
@Repository
public class CourseDaoImpl implements CourseDao {

    private static final String QUERY_ADD = "course.add";
    private static final String QUERY_GET_ALL = "course.getAll";
    private static final String QUERY_GET_ALL_SORTED_PAGINATED = "course.getAllSortedPaginated";
    private static final String QUERY_GET_BY_ID = "course.getById";
    private static final String QUERY_UPDATE = "course.update";
    private static final String QUERY_DELETE = "course.delete";
    private static final String QUERY_COUNT_ALL = "course.countAll";
    private static final String MESSAGE_COURSE_NOT_FOUND = "Course id(%s) not found";
    private static final String MESSAGE_UPDATE_COURSE_NOT_FOUND = "Can't update because course id(%s) not found";
    private static final String MESSAGE_DELETE_COURSE_NOT_FOUND = "Can't delete because course id(%s) not found";
    private static final String COURSE_NAME = "course_name";

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
            throw new DaoException(e.getMessage(), e);
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
            throw new DaoException(String.format(MESSAGE_COURSE_NOT_FOUND, id), e);
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
            throw new DaoException(String.format(MESSAGE_UPDATE_COURSE_NOT_FOUND,
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
            throw new DaoException(String.format(MESSAGE_DELETE_COURSE_NOT_FOUND,
                course.getId()));
        } else {
            log.info("Delete {}", course);
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting course id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete course id({})", id);
            throw new DaoException(String.format(MESSAGE_DELETE_COURSE_NOT_FOUND,
                id));
        } else {
            log.info("Delete course id({})", id);
        }
    }

    @Override
    public int countAll() {
        log.debug("Count all courses in database");
        Integer result = jdbcTemplate.queryForObject(
            env.getRequiredProperty(QUERY_COUNT_ALL), Integer.class);
        log.info("{} courses", result);
        return (result != null ? result : 0);
    }

    @Override
    public Page<Course> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of courses", pageable.getPageNumber());
        Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Order.by(COURSE_NAME);
        }
        String query = String.format(env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
            order.getProperty(), order.getDirection().name(),
            pageable.getOffset(), pageable.getPageSize());
        List<Course> courses = jdbcTemplate.query(query, new CourseMapper());
        log.info("Found {} courses", courses.size());
        return new PageImpl<>(courses, pageable, countAll());
    }
}
