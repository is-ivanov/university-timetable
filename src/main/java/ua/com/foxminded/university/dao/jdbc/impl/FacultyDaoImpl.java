package ua.com.foxminded.university.dao.jdbc.impl;

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
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.dao.jdbc.mapper.FacultyMapper;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DaoException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class FacultyDaoImpl implements FacultyDao {

    private static final String QUERY_ADD = "faculty.add";
    private static final String QUERY_GET_ALL = "faculty.getAll";
    private static final String QUERY_GET_ALL_SORTED_PAGINATED = "faculty.getAllSortedPaginated";
    private static final String QUERY_GET_ALL_SORTED_NAME_ASC = "faculty.getAllSortedByNameAsc";
    private static final String QUERY_GET_BY_ID = "faculty.getById";
    private static final String QUERY_UPDATE = "faculty.update";
    private static final String QUERY_DELETE = "faculty.delete";
    private static final String QUERY_COUNT_ALL = "faculty.countAll";
    private static final String MESSAGE_FACULTY_NOT_FOUND = "Faculty id(%d) not found";
    private static final String MESSAGE_UPDATE_FACULTY_NOT_FOUND = "Can't update because faculty id(%d) not found";
    private static final String MESSAGE_DELETE_FACULTY_NOT_FOUND = "Can't delete because faculty id(%d) not found";
    public static final String FACULTY_NAME = "faculty_name";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Faculty faculty) {
        log.debug("Adding {}", faculty);
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                faculty.getName());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", faculty, e);
            throw new DaoException(e.getMessage(), e);
        }
        log.info("{} added successfully", faculty);
    }

    @Override
    public Optional<Faculty> getById(int id) {
        log.debug("Getting faculty by id({})", id);
        Faculty result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new FacultyMapper(), id);
        } catch (DataAccessException e) {
            log.error("Faculty id({}) not found", id, e);
            throw new DaoException(String.format(MESSAGE_FACULTY_NOT_FOUND,
                id), e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Faculty> getAll() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), new FacultyMapper());
        log.info("Found {} faculties", faculties.size());
        return faculties;
    }

    @Override
    public void update(Faculty faculty) {
        log.debug("Updating {}", faculty);
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            faculty.getName(), faculty.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", faculty);
            throw new DaoException(String.format(MESSAGE_UPDATE_FACULTY_NOT_FOUND,
                faculty.getId()));
        } else {
            log.info("Update {}", faculty);
        }
    }

    @Override
    public void delete(Faculty faculty) {
        log.debug("Deleting {}", faculty);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), faculty.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", faculty);
            throw new DaoException(String.format(MESSAGE_DELETE_FACULTY_NOT_FOUND,
                faculty.getId()));
        } else {
            log.info("Delete {}", faculty);
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting faculty id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete faculty id({})", id);
            throw new DaoException(String.format(MESSAGE_DELETE_FACULTY_NOT_FOUND,
                id));
        } else {
            log.info("Delete faculty id({})", id);
        }
    }

    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        log.debug("Getting all faculties sorted by name ascending");
        List<Faculty> faculties = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_SORTED_NAME_ASC),
            new FacultyMapper());
        log.info("Found {} sorted faculties", faculties.size());
        return faculties;
    }

    @Override
    public int countAll() {
        log.debug("Count all faculties in database");
        Integer result = jdbcTemplate.queryForObject(
            env.getRequiredProperty(QUERY_COUNT_ALL), Integer.class);
        log.info("{} faculties", result);
        return (result != null ? result : 0);
    }

    @Override
    public Page<Faculty> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of faculties", pageable.getPageNumber());
        Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Order.by(FACULTY_NAME);
        }
        String query = String.format(env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
            order.getProperty(), order.getDirection().name(),
            pageable.getOffset(), pageable.getPageSize());
        List<Faculty> faculties = jdbcTemplate.query(query, new FacultyMapper());
        log.info("Found {} faculties", faculties.size());
        return new PageImpl<>(faculties, pageable, countAll());
    }

}