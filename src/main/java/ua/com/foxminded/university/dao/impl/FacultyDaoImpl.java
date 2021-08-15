package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.dao.mapper.FacultyMapper;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DAOException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class FacultyDaoImpl implements FacultyDao {

    private static final String QUERY_ADD = "faculty.add";
    private static final String QUERY_GET_ALL = "faculty.getAll";
    private static final String QUERY_GET_ALL_SORTED_NAME_ASC = "faculty.getAllSortedByNameAsc";
    private static final String QUERY_GET_BY_ID = "faculty.getById";
    private static final String QUERY_UPDATE = "faculty.update";
    private static final String QUERY_DELETE = "faculty.delete";
    private static final String MESSAGE_FACULTY_NOT_FOUND = "Faculty id(%d) not found";
    private static final String MESSAGE_UPDATE_FACULTY_NOT_FOUND = "Can't update because faculty id(%d) not found";
    private static final String MESSAGE_DELETE_FACULTY_NOT_FOUND = "Can't delete because faculty id(%d) not found";

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
            throw new DAOException(e.getMessage(), e);
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
            throw new DAOException(String.format(MESSAGE_FACULTY_NOT_FOUND,
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
            throw new DAOException(String.format(MESSAGE_UPDATE_FACULTY_NOT_FOUND,
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
            throw new DAOException(String.format(MESSAGE_DELETE_FACULTY_NOT_FOUND,
                faculty.getId()));
        } else {
            log.info("Delete {}", faculty);
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

}