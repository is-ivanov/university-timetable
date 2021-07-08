package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.entity.mapper.TeacherMapper;
import ua.com.foxminded.university.exception.DAOException;

@Repository
@PropertySource("classpath:sql_query.properties")
public class TeacherDaoImpl implements TeacherDao {

    private static final String QUERY_ADD = "teacher.add";
    private static final String QUERY_GET_ALL = "teacher.getAll";
    private static final String QUERY_GET_BY_ID = "teacher.getById";
    private static final String QUERY_UPDATE = "teacher.update";
    private static final String QUERY_DELETE = "teacher.delete";
    private static final String MESSAGE_TEACHER_NOT_FOUND = "Teacher not found: ";

    private JdbcTemplate jdbcTemplate;
    private Environment env;

    @Autowired
    public TeacherDaoImpl(JdbcTemplate jdbcTemplate, Environment env) {
        this.jdbcTemplate = jdbcTemplate;
        this.env = env;
    }

    @Override
    public void add(Teacher teacher) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                teacher.getFirstName(), teacher.getLastName(),
                teacher.getPatronymic(), teacher.isActive(),
                teacher.getDepartment().getId());
    }

    @Override
    public Optional<Teacher> getById(int id) throws DAOException {
        Teacher result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new TeacherMapper(), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_TEACHER_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Teacher> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new TeacherMapper());
    }

    @Override
    public void update(Teacher teacher) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                teacher.getFirstName(), teacher.getLastName(),
                teacher.getPatronymic(), teacher.isActive(),
                teacher.getDepartment().getId(), teacher.getId());
    }

    @Override
    public void delete(Teacher teacher) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                teacher.getId());
    }

}
