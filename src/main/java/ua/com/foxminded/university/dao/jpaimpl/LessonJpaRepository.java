package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.LessonRepository;
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
public class LessonJpaRepository implements LessonRepository {

    private static final String QUERY_GET_ALL = "lesson.getAll";
    private static final String QUERY_DELETE_BY_ID = "lesson.deleteById";
    private static final String QUERY_DELETE_ALL_STUDENTS_FROM_LESSON =
        "lesson.deleteAllStudentsFromLesson";
    private static final String QUERY_GET_ALL_FOR_TEACHER = "lesson.getAllForTeacher";
    private static final String QUERY_GET_ALL_FOR_ROOM = "lesson.getAllForRoom";
    private static final String QUERY_GET_ALL_FOR_STUDENT = "lesson.getAllForStudent";
    private static final String QUERY_GET_ALL_FOR_STUDENT_FOR_TIME_PERIOD =
        "lesson.getAllForStudentForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_TEACHER_FOR_TIME_PERIOD =
        "lesson.getAllForTeacherForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_ROOM_FOR_TIME_PERIOD =
        "lesson.getAllForRoomForTimePeriod";
    private static final String MESSAGE_DELETE_LESSON_NOT_FOUND =
        "Can't delete because lesson id(%d) not found";
    private static final String FOUND_LESSONS = "Found {} lessons";
    private static final String WHERE = " WHERE l.id > 0 ";
    private static final String TEACHER_FILTER = " AND t.id = ";
    private static final String DEPARTMENT_FILTER = " AND t.department.id = ";
    private static final String FACULTY_FILTER = " AND t.department.faculty.id = ";
    private static final String COURSE_FILTER = " AND c.id = ";
    private static final String ROOM_FILTER = " AND r.id = ";
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
        log.debug("Saving lesson with course id({}), teacher id({}), room id({}) " +
                "time_start({}), time_end({})", lesson.getCourse().getId(),
            lesson.getTeacher().getId(), lesson.getRoom().getId(),
            lesson.getTimeStart(), lesson.getTimeEnd());
        entityManager.persist(lesson);
        log.debug("Lesson with course id({}), teacher id({}), room id({}) " +
                "time_start({}), time_end({}) saved successfully", lesson.getCourse().getId(),
            lesson.getTeacher().getId(), lesson.getRoom().getId(),
            lesson.getTimeStart(), lesson.getTimeEnd());
    }

    @Override
    public Optional<Lesson> getById(int id) {
        log.debug("Getting lesson by id({})", id);
        Lesson lesson = entityManager.find(Lesson.class, id);
        log.debug("Found lesson {}", lesson);
        return Optional.ofNullable(lesson);
    }

    @Override
    public List<Lesson> getAll() {
        log.debug("Getting all lessons");
        List<Lesson> lessons = entityManager.createQuery(env.getProperty(QUERY_GET_ALL),
            Lesson.class).getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public void update(Lesson lesson) {
        log.debug("Updating lesson id({})", lesson.getId());
        entityManager.merge(lesson);
        log.debug("Lesson id({}) updated successfully", lesson.getId());
    }

    @Override
    public void delete(Lesson lesson) {
        log.debug("Deleting lesson id({})", lesson.getId());
        entityManager.remove(lesson);
        log.debug("Delete lesson id({})", lesson.getId());
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting lesson id({})", id);
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete lesson id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_LESSON_NOT_FOUND, id));
        } else {
            log.debug("Delete lesson id({})", id);
        }
    }

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {
        log.debug("Adding student id({}) to lesson id({})", studentId, lessonId);
        Lesson lesson = entityManager.find(Lesson.class, lessonId);
        Student student = entityManager.find(Student.class, studentId);
        lesson.addStudent(student);
        log.info("Student id({}) added to lesson({}) successfully", studentId,
            lessonId);
    }

    @Override
    public void deleteAllStudentsFromLesson(int lessonId) {
        log.debug("Deleting all students from lesson id({})", lessonId);
        Query query = entityManager.createNativeQuery(
            env.getProperty(QUERY_DELETE_ALL_STUDENTS_FROM_LESSON));
        query.setParameter("id", lessonId);
        int numberDeletedRows = query.executeUpdate();
        log.info("Delete {} students from lesson id({})", numberDeletedRows,
            lessonId);
    }

    @Override
    public List<Lesson> getAllForTeacher(int teacherId) {
        log.debug("Getting all lessons for teacher id({})", teacherId);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_TEACHER), Lesson.class)
            .setParameter("teacherId", teacherId)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForRoom(int roomId) {
        log.debug("Getting all lessons for room id({})", roomId);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_ROOM), Lesson.class)
            .setParameter("roomId", roomId)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForStudent(int studentId) {
        log.debug("Getting all lessons for student id({})", studentId);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_STUDENT), Lesson.class)
            .setParameter("studentId", studentId)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {
        log.debug("Removing student id({}) from lesson id({})", studentId, lessonId);
        Lesson lesson = entityManager.find(Lesson.class, lessonId);
        Student student = entityManager.find(Student.class, studentId);
        lesson.removeStudent(student);
        log.debug("Student id({}) successfully removed from lesson id({})",
            studentId, lessonId);
    }

    @Override
    public List<Lesson> getAllWithFilter(LessonFilter filter) {
        log.debug("Getting all lessons with ({})", filter);
        String query = createQuery(filter);
        List<Lesson> lessons = entityManager.createQuery(query, Lesson.class)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;

    }

    @Override
    public List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for student id({}) from {} to {}", studentId,
            startTime, endTime);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_STUDENT_FOR_TIME_PERIOD),
                Lesson.class)
            .setParameter("studentId", studentId)
            .setParameter("timeStart", startTime)
            .setParameter("timeEnd", endTime)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForTeacherForTimePeriod(int teacherId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for teacher id({}) from {} to {}", teacherId,
            startTime, endTime);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_TEACHER_FOR_TIME_PERIOD),
                Lesson.class)
            .setParameter("teacherId", teacherId)
            .setParameter("timeStart", startTime)
            .setParameter("timeEnd", endTime)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForRoomForTimePeriod(int roomId,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        log.debug("Getting lessons for room id({}) from {} to {}", roomId,
            startTime, endTime);
        List<Lesson> lessons = entityManager.createQuery(
                env.getProperty(QUERY_GET_ALL_FOR_ROOM_FOR_TIME_PERIOD),
                Lesson.class)
            .setParameter("roomId", roomId)
            .setParameter("timeStart", startTime)
            .setParameter("timeEnd", endTime)
            .getResultList();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessons;
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
