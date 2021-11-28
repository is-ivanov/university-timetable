package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class JpaLessonDaoImpl implements LessonDao {

    private static final String QUERY_ADD = "lesson.add";
    private static final String QUERY_GET_ALL = "Lesson.getAll";
    private static final String QUERY_GET_BY_ID = "lesson.getById";
    private static final String QUERY_UPDATE = "lesson.update";
    private static final String QUERY_DELETE_BY_ID = "Lesson.deleteById";
    private static final String QUERY_ADD_STUDENT_TO_LESSON =
        "Lesson.addStudentToLesson";
    private static final String QUERY_DELETE_ALL_STUDENTS_FROM_LESSON =
        "Lesson.deleteAllStudentsFromLesson";
    private static final String QUERY_DELETE_STUDENT_FROM_LESSON =
        "lesson.deleteStudentFromLesson";
    private static final String QUERY_GET_ALL_FOR_TEACHER = "Lesson.getAllForTeacher";
    private static final String QUERY_GET_ALL_FOR_ROOM = "Lesson.getAllForRoom";
    private static final String QUERY_GET_ALL_FOR_STUDENT = "Lesson.getAllForStudent";
    private static final String QUERY_GET_ALL_FOR_STUDENT_FOR_TIME_PERIOD =
        "lesson.getAllForStudentForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_TEACHER_FOR_TIME_PERIOD =
        "lesson.getAllForTeacherForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_ROOM_FOR_TIME_PERIOD =
        "lesson.getAllForRoomForTimePeriod";
    private static final String MESSAGE_LESSON_NOT_FOUND = "Lesson id(%d) not found";
    private static final String MESSAGE_UPDATE_LESSON_NOT_FOUND =
        "Can't update because lesson id(%d) not found";
    private static final String MESSAGE_DELETE_LESSON_NOT_FOUND =
        "Can't delete because lesson id(%d) not found";
    private static final String MESSAGE_STUDENT_NOT_FOUND_IN_LESSON =
        "Can't delete because student id(%d) not found in lesson id(%d)";
    private static final String FOUND_LESSONS = "Found {} lessons";
    private static final String WHERE = " WHERE l.lesson_id > 0 ";
    private static final String TEACHER_FILTER = " AND l.teacher_id = ";
    private static final String DEPARTMENT_FILTER = " AND t.department_id = ";
    private static final String FACULTY_FILTER = " AND d.faculty_id = ";
    private static final String COURSE_FILTER = " AND l.course_id = ";
    private static final String ROOM_FILTER = " AND l.room_id = ";
    private static final String TIME_BETWEEN_FILTER = " AND l.time_start BETWEEN '";
    private static final String AND = "' AND '";
    private static final String CLOSING_QUOTATION_MARK = "' ";
    private static final String TIME_AFTER_FILTER = " AND l.time_start >= '";
    private static final String TIME_BEFORE_FILTER = " AND l.time_start <= '";


    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Lesson lesson) {
        entityManager.persist(lesson);
    }

    @Override
    public Optional<Lesson> getById(int id) {
        Lesson lesson = entityManager.find(Lesson.class, id);
        return Optional.ofNullable(lesson);
    }

    @Override
    public List<Lesson> getAll() {
        return entityManager.createQuery(env.getProperty(QUERY_GET_ALL),
            Lesson.class).getResultList();
    }

    @Override
    public void update(Lesson lesson) {
        entityManager.merge(lesson);
    }

    @Override
    public void delete(Lesson lesson) {
        entityManager.remove(lesson);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete lesson id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_LESSON_NOT_FOUND, id));
        } else {
            log.info("Delete lesson id({})", id);
        }
    }

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {
        Lesson lesson = entityManager.find(Lesson.class, lessonId);
        Student student = entityManager.find(Student.class, studentId);
        lesson.addStudent(student);
    }

    @Override
    public void deleteAllStudentsFromLesson(int lessonId) {
        // First variant
        Query query = entityManager.createNativeQuery(
            env.getProperty(QUERY_DELETE_ALL_STUDENTS_FROM_LESSON));
        query.setParameter("id", lessonId);
        int rowsDeleted = query.executeUpdate();

//        //Second variant
//        Lesson lesson = entityManager.find(Lesson.class, lessonId);
//        lesson.getStudents().clear();
    }

    @Override
    public List<Lesson> getAllForTeacher(int teacherId) {
        List<Lesson> lessons = entityManager.createQuery(
            env.getProperty(QUERY_GET_ALL_FOR_TEACHER), Lesson.class)
            .setParameter("teacherId", teacherId)
            .getResultList();
        return lessons;
    }

    @Override
    public List<Lesson> getAllForRoom(int roomId) {
        List<Lesson> lessons = entityManager.createQuery(
            env.getProperty(QUERY_GET_ALL_FOR_ROOM), Lesson.class)
            .setParameter("roomId", roomId)
            .getResultList();
        return lessons;
    }

    @Override
    public List<Lesson> getAllForStudent(int studentId) {
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_STUDENT), Lesson.class)
            .setParameter("studentId", studentId)
            .getResultList();
        return lessons;
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {
        Lesson lesson = entityManager.find(Lesson.class, lessonId);
        Student student = entityManager.find(Student.class, studentId);
        lesson.removeStudent(student);
    }

    @Override
    public List<Lesson> getAllWithFilter(LessonFilter filter) {
        return null;
    }

    @Override
    public List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        return null;
    }

    @Override
    public List<Lesson> getAllForTeacherForTimePeriod(int studentId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        return null;
    }

    @Override
    public List<Lesson> getAllForRoomForTimePeriod(int teacherId,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        return null;
    }
}
