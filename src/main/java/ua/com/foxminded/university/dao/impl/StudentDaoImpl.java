package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.mapper.StudentMapper;
import ua.com.foxminded.university.exception.DAOException;

@Component
@PropertySource("classpath:sql_query.properties")
public class StudentDaoImpl implements StudentDao {

    private static final String QUERY_ADD = "student.add";
    private static final String QUERY_GET_ALL = "student.getAll";
    private static final String QUERY_GET_BY_ID = "student.getById";
    private static final String QUERY_UPDATE = "student.update";
    private static final String QUERY_DELETE = "student.delete";
    private static final String QUERY_GET_ALL_FOR_LESSON = "student.getAllForLesson";
    private static final String MESSAGE_STUDENT_NOT_FOUND = "Student not found: ";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment env;

    @Autowired
    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Student student) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                student.getFirstName(), student.getLastName(),
                student.getPatronymic(), student.getGroup().getId());
    }

    @Override
    public Optional<Student> getById(int id) throws DAOException {
        Student result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new StudentMapper(), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_STUDENT_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Student> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new StudentMapper());
    }

    @Override
    public void update(Student student) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                student.getFirstName(), student.getLastName(),
                student.getPatronymic(), student.getGroup().getId(),
                student.getId());
    }

    @Override
    public void delete(Student student) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                student.getId());
    }

    @Override
    public List<Student> getAllForLesson(Lesson lesson) {
        return jdbcTemplate.query(
                env.getRequiredProperty(QUERY_GET_ALL_FOR_LESSON),
                new StudentMapper(), lesson.getId());
    }

}
