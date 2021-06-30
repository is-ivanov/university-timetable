package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.mapper.LessonExtractor;
import ua.com.foxminded.university.exception.DAOException;

@Repository
@PropertySource("classpath:sql_query.properties")
public class LessonDaoImpl implements LessonDao {

    private static final String QUERY_ADD = "lesson.add";
    private static final String QUERY_GET_ALL = "lesson.getAll";
    private static final String QUERY_GET_BY_ID = "lesson.getById";
    private static final String QUERY_UPDATE = "lesson.update";
    private static final String QUERY_DELETE = "lesson.delete";
    private static final String QUERY_ADD_STUDENT_TO_LESSON = "lesson.addStudentToLesson";
    private static final String QUERY_DELETE_ALL_STUDENTS_FROM_LESSON = "lesson.deleteAllStudentsFromLesson";
    private static final String QUERY_GET_ALL_BY_TEACHER = "lesson.getAllByTeacher";
    private static final String QUERY_GET_ALL_BY_ROOM = "lesson.getAllByRoom";
    private static final String QUERY_GET_ALL_BY_STUDENT = "lesson.getAllByStudent";
    private static final String MESSAGE_LESSON_NOT_FOUND = "Lesson not found: ";

    private JdbcTemplate jdbcTemplate;
    private LessonExtractor lessonExtractor;
    private Environment env;

    @Autowired
    public LessonDaoImpl(JdbcTemplate jdbcTemplate,
            LessonExtractor lessonExtractor, Environment env) {
        this.jdbcTemplate = jdbcTemplate;
        this.lessonExtractor = lessonExtractor;
        this.env = env;
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
        List<Lesson> lessons = null;
        Lesson result = null;
        try {
            lessons = jdbcTemplate.query(
                    env.getRequiredProperty(QUERY_GET_BY_ID),
                    lessonExtractor, id);
            if (lessons != null) {
                result = lessons.get(0);
            }
        } catch (DataAccessException | IndexOutOfBoundsException e) {
            throw new DAOException(MESSAGE_LESSON_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Lesson> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                lessonExtractor);
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

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {
        jdbcTemplate.update(
                env.getRequiredProperty(QUERY_ADD_STUDENT_TO_LESSON),
                lessonId, studentId);
    }

    @Override
    public void deleteAllStudentsFromLesson(int lessonId) {
        jdbcTemplate.update(
                env.getRequiredProperty(QUERY_DELETE_ALL_STUDENTS_FROM_LESSON),
                lessonId);
    }

    // TODO method
    @Override
    public void deleteStudentFromLesson(int lessonId, int studentId) {

    }

    @Override
    // TODO unit tests
    public List<Lesson> getAllByTeacher(int teacherId) {
        return jdbcTemplate.query(
                env.getRequiredProperty(QUERY_GET_ALL_BY_TEACHER),
                lessonExtractor, teacherId);
    }

    @Override
    // TODO unit tests
    public List<Lesson> getAllByRoom(int roomId) {
        return jdbcTemplate.query(
                env.getRequiredProperty(QUERY_GET_ALL_BY_ROOM),
                lessonExtractor, roomId);
    }

    @Override
    // TODO unit tests
    public List<Lesson> getAllByStudent(int studentId) {
        return jdbcTemplate.query(
                env.getRequiredProperty(QUERY_GET_ALL_BY_STUDENT),
                lessonExtractor, studentId);
    }

}
