package ua.com.foxminded.university.dao.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.domain.entity.Faculty;

@Component
@PropertySource("classpath:sql_query.properties")
public class FacultyDaoImpl implements FacultyDao {

    private static final String QUERY_ADD = "faculty.add";
    private static final String QUERY_GET_ALL = "faculty.getAll";
    private static final String QUERY_GET_BY_ID = "faculty.getById";
    private static final String QUERY_UPDATE = "faculty.update";
    private static final String QUERY_DELETE = "faculty.delete";

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public FacultyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private Environment env;

    @Override
    public void add(Faculty faculty) {
        // TODO Auto-generated method stub

    }

    @Override
    public Optional<Faculty> getById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Faculty> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(Faculty faculty) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Faculty faculty) {
        // TODO Auto-generated method stub

    }

}
