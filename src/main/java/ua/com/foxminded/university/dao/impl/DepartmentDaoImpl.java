package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
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

    @Autowired
    public DepartmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private Environment env;

    @Override
    public void add(Department department) {
        Integer headId = null;
        if (department.getHead() != null) {
            headId = department.getHead().getId();
        }
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                department.getName(), headId, department.getFaculty().getId());
    }

    @Override
    public Optional<Department> getById(int id) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Department> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(Department t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Department t) {
        // TODO Auto-generated method stub

    }

}
