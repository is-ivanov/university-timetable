package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.mapper.DepartmentMapper;
import ua.com.foxminded.university.exception.DAOException;

@Component
@PropertySource("classpath:sql_query.properties")
public class DepartmentDaoImpl implements DepartmentDao {

    private static final String QUERY_ADD = "department.add";
    private static final String QUERY_GET_ALL = "department.getAll";
    private static final String QUERY_GET_BY_ID = "department.getById";
    private static final String QUERY_UPDATE = "department.update";
    private static final String QUERY_DELETE = "department.delete";
    private static final String MESSAGE_DEPARTMENT_NOT_FOUND = "Department not found: ";

    private JdbcTemplate jdbcTemplate;
    private Environment env;

    @Autowired
    public DepartmentDaoImpl(JdbcTemplate jdbcTemplate, Environment env) {
        this.jdbcTemplate = jdbcTemplate;
        this.env = env;
    }

    @Override
    public void add(Department department) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                department.getName(), department.getFaculty().getId());
    }

    @Override
    public Optional<Department> getById(int id) throws DAOException {
        Department result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new DepartmentMapper(), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_DEPARTMENT_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Department> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new DepartmentMapper());
    }

    @Override
    public void update(Department department) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                department.getName(), department.getFaculty().getId(),
                department.getId());
    }

    @Override
    public void delete(Department department) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                department.getId());
    }

}
