package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.mapper.FacultyMapper;

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
//        Integer deanId = null;
//        if (faculty.getDean() != null) {
//            deanId = faculty.getDean().getId();
//        }
//        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
//                faculty.getName(), deanId);
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                faculty.getName(), faculty.getDean().getId());
    }

    @Override
    public Optional<Faculty> getById(int id) {
        Faculty result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new FacultyMapper(), id);
        } catch (DataAccessException e) {
            System.out.println("Faculty not found: " + id);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Faculty> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new FacultyMapper());
    }

    @Override
    public void update(Faculty faculty) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                faculty.getName(), faculty.getDean().getId(), faculty.getId());
    }

    @Override
    public void delete(Faculty faculty) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                faculty.getId());
    }

}
