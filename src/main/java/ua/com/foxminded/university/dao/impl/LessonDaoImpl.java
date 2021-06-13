package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.mapper.LessonMapper;
import ua.com.foxminded.university.exception.DAOException;

@Component
@PropertySource("classpath:sql_query.properties")
public class LessonDaoImpl implements LessonDao {

    private static final String QUERY_ADD = "lesson.add";
    private static final String QUERY_GET_ALL = "lesson.getAll";
    private static final String QUERY_GET_BY_ID = "lesson.getById";
    private static final String QUERY_UPDATE = "lesson.update";
    private static final String QUERY_DELETE = "lesson.delete";
    private static final String MESSAGE_LESSON_NOT_FOUND = "Lesson not found: ";

    private JdbcTemplate jdbcTemplate;
    private StudentDao studentDao;

    @Autowired
    private Environment env;

    @Autowired
    public LessonDaoImpl(JdbcTemplate jdbcTemplate, StudentDao studentDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentDao = studentDao;
    }

    @Override
    public void add(Lesson lesson) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                lesson.getTeacher().getId(), lesson.getCourse().getId(),
                lesson.getRoom().getId(), lesson.getTimeStart(),
                lesson.getTimeEnd());
    }

    @Override
    public Optional<Lesson> getById(int id) throws DAOException {
        Lesson result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    new LessonMapper(studentDao), id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_LESSON_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Lesson> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new LessonMapper(studentDao));
    }

    @Override
    public void update(Lesson lesson) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                lesson.getTeacher().getId(), lesson.getCourse().getId(),
                lesson.getRoom().getId(), lesson.getTimeStart(),
                lesson.getTimeEnd(), lesson.getId());
    }

    @Override
    public void delete(Lesson lesson) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                lesson.getId());
    }

}
