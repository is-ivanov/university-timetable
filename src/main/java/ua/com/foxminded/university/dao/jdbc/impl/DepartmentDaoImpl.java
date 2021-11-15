package ua.com.foxminded.university.dao.jdbc.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.dao.jdbc.mapper.DepartmentMapper;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.exception.DaoException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class DepartmentDaoImpl implements DepartmentDao {

    private static final String QUERY_ADD = "department.add";
    private static final String QUERY_GET_ALL = "department.getAll";
    private static final String QUERY_GET_ALL_BY_FACULTY = "department.getAllByFacultyId";
    private static final String QUERY_GET_BY_ID = "department.getById";
    private static final String QUERY_UPDATE = "department.update";
    private static final String QUERY_DELETE = "department.delete";
    private static final String MESSAGE_DEPARTMENT_NOT_FOUND = "Department id(%s) not found";
    private static final String MESSAGE_UPDATE_DEPARTMENT_NOT_FOUND = "Can't update because department id(%s) not found";
    private static final String MESSAGE_DELETE_DEPARTMENT_NOT_FOUND = "Can't delete because department id(%s) not found";

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Department department) {
        log.debug("Adding {}", department);
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                department.getName(), department.getFaculty().getId());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", department, e);
            throw new DaoException(e.getMessage(), e);
        }
        log.info("{} added successfully", department);
    }

    @Override
    public Optional<Department> getById(int id) {
        log.debug("Getting department by id({})", id);
        Department result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                new DepartmentMapper(), id);
        } catch (DataAccessException e) {
            log.error("Department id({}) not found", id, e);
            throw new DaoException(String.format(MESSAGE_DEPARTMENT_NOT_FOUND,
                id), e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Department> getAll() {
        log.debug("Getting all departments");
        List<Department> departments = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL),
            new DepartmentMapper());
        log.info("Found {} departments", departments.size());
        return departments;
    }

    @Override
    public void update(Department department) {
        log.debug("Updating {}", department);
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            department.getName(), department.getFaculty().getId(),
            department.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", department);
            throw new DaoException(String.format(MESSAGE_UPDATE_DEPARTMENT_NOT_FOUND,
                department.getId()));
        } else {
            log.info("Update {}", department);
        }
    }

    @Override
    public void delete(Department department) {
        log.debug("Deleting {}", department);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), department.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", department);
            throw new DaoException(String
                .format(MESSAGE_DELETE_DEPARTMENT_NOT_FOUND,
                    department.getId()));
        } else {
            log.info("Delete {}", department);
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting department id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete department id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_DEPARTMENT_NOT_FOUND, id));
        } else {
            log.info("Delete department id({})", id);
        }
    }

    @Override
    public List<Department> getAllByFacultyId(int facultyId) {
        log.debug("Getting all departments by faculty id({})", facultyId);
        List<Department> departments =
            jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL_BY_FACULTY),
                new DepartmentMapper(), facultyId);
        log.info("Found {} departments", departments.size());
        return departments;
    }
}