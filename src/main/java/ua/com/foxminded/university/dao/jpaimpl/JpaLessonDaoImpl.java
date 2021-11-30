package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
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
    private static final String WHERE = " WHERE l.id > 0 ";
    private static final String TEACHER_FILTER = " AND l.teacher.id = ";
    private static final String DEPARTMENT_FILTER = " AND l.teacher.department.id = ";
    private static final String FACULTY_FILTER = " AND l.teacher.department.faculty.id = ";
    private static final String COURSE_FILTER = " AND l.course.id = ";
    private static final String ROOM_FILTER = " AND l.room.id = ";
    private static final String TIME_BETWEEN_FILTER = " AND l.timeStart BETWEEN '";
    private static final String AND = "' AND '";
    private static final String CLOSING_QUOTATION_MARK = "' ";
    private static final String TIME_AFTER_FILTER = " AND l.timeStart >= '";
    private static final String TIME_BEFORE_FILTER = " AND l.timeStart <= '";


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
        Query query = entityManager.createNativeQuery(
            env.getProperty(QUERY_DELETE_ALL_STUDENTS_FROM_LESSON));
        query.setParameter("id", lessonId);
        query.executeUpdate();
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
        log.debug("Getting all lessons with ({})", filter);

        String query = createQuery(filter);
        List<Lesson> lessons = entityManager.createQuery(query, Lesson.class)
            .getResultList();
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;

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

    private String createQuery(LessonFilter filter) {
        log.debug("create jpql query with filters");
        StringBuilder query = new StringBuilder(env.getRequiredProperty(QUERY_GET_ALL));
        query.append(WHERE);
        addTeacherDepartmentFacultyFilter(query, filter.getTeacherId(),
            filter.getDepartmentId(), filter.getFacultyId());
        addCourseFilter(query, filter.getCourseId());
        addRoomFilter(query, filter.getRoomId());
        addDateFilter(query, filter.getDateFrom(), filter.getDateTo());
        log.debug("query = {}", query);
        return query.toString();
    }

    private void addTeacherDepartmentFacultyFilter(StringBuilder query,
                                                   Integer teacherId,
                                                   Integer departmentId,
                                                   Integer facultyId) {
        log.debug("check filter by teacher, department and faculty");
        if (teacherId != null && teacherId > 0) {
            log.debug("add filter by teacherId({})", teacherId);
            query.append(TEACHER_FILTER).append(teacherId);
        } else if (departmentId != null && departmentId > 0) {
            log.debug("add filter by departmentId({})", departmentId);
            query.append(DEPARTMENT_FILTER).append(departmentId);
        } else if (facultyId != null && facultyId > 0) {
            log.debug("add filter by facultyId({})", facultyId);
            query.append(FACULTY_FILTER).append(facultyId);
        }
    }

    private void addCourseFilter(StringBuilder query, Integer courseId) {
        log.debug("check filter by course");
        if (courseId != null && courseId > 0) {
            log.debug("add filter by courseId({})", courseId);
            query.append(COURSE_FILTER).append(courseId);
        }
    }

    private void addRoomFilter(StringBuilder query, Integer roomId) {
        log.debug("check filter by room");
        if (roomId != null && roomId > 0) {
            log.debug("add filter by roomId({})", roomId);
            query.append(ROOM_FILTER).append(roomId);
        }
    }

    private void addDateFilter(StringBuilder query, LocalDateTime dateFrom,
                               LocalDateTime dateTo) {
        log.debug("check filter by date");
        if (dateFrom != null && dateTo != null) {
            log.debug("add filter between {} and {}", dateFrom, dateTo);
            query.append(TIME_BETWEEN_FILTER).append(dateFrom)
                .append(AND).append(dateTo).append(CLOSING_QUOTATION_MARK);
        } else if (dateFrom != null) {
            log.debug("add filter time_start >= {}", dateFrom);
            query.append(TIME_AFTER_FILTER).append(dateFrom)
                .append(CLOSING_QUOTATION_MARK);
        } else if (dateTo != null) {
            log.debug("add filter time_start <= {}", dateTo);
            query.append(TIME_BEFORE_FILTER).append(dateTo)
                .append(CLOSING_QUOTATION_MARK);
        }
    }

}
