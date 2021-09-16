package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.dao.mapper.LessonExtractor;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.exception.DAOException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
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
    private static final String QUERY_DELETE_STUDENT_FROM_LESSON = "lesson.deleteStudentFromLesson";
    private static final String QUERY_GET_ALL_FOR_TEACHER = "lesson.getAllForTeacher";
    private static final String QUERY_GET_ALL_FOR_ROOM = "lesson.getAllForRoom";
    private static final String QUERY_GET_ALL_FOR_STUDENT = "lesson.getAllForStudent";
    private static final String QUERY_GET_ALL_FOR_STUDENT_FOR_TIME_PERIOD = "lesson.getAllForStudentForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_TEACHER_FOR_TIME_PERIOD = "lesson.getAllForTeacherForTimePeriod";
    private static final String QUERY_GET_ALL_FOR_ROOM_FOR_TIME_PERIOD = "lesson.getAllForRoomForTimePeriod";
    private static final String MESSAGE_LESSON_NOT_FOUND = "Lesson id(%d) not found";
    private static final String MESSAGE_UPDATE_LESSON_NOT_FOUND = "Can't update because lesson id(%d) not found";
    private static final String MESSAGE_DELETE_LESSON_NOT_FOUND = "Can't delete because lesson id(%d) not found";
    private static final String MESSAGE_STUDENT_NOT_FOUND_IN_LESSON = "Can't delete because student id(%d) not found in lesson id(%d)";
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

    private final JdbcTemplate jdbcTemplate;
    private final LessonExtractor lessonExtractor;
    private final Environment env;

    @Override
    public void add(Lesson lesson) {
        log.debug("Adding lesson id({})", lesson.getId());
        try {
            LocalDateTime timeStart = lesson.getTimeStart();
            LocalDateTime timeEnd = lesson.getTimeEnd();
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                lesson.getTeacher().getId(), lesson.getCourse().getId(),
                lesson.getRoom().getId(), timeStart,
                timeEnd);
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", lesson, e);
            throw new DAOException(e.getMessage(), e);
        }
        log.info("Lesson id({}) added successfully", lesson.getId());
    }

    @Override
    public Optional<Lesson> getById(int id) {
        log.debug("Getting lesson by id({})", id);
        List<Lesson> lessons;
        Lesson result = null;
        try {
            lessons = jdbcTemplate.query(
                env.getRequiredProperty(QUERY_GET_BY_ID),
                lessonExtractor, id);
            if (lessons != null) {
                result = lessons.get(0);
            }
        } catch (DataAccessException | IndexOutOfBoundsException e) {
            log.error("Lesson id({}) not found", id, e);
            throw new DAOException(String.format(MESSAGE_LESSON_NOT_FOUND, id),
                e);
        }
        log.info("Found lesson: course [{}], room[{}], teacher[{}]",
            result.getCourse().getName(), result.getRoom().getNumber(),
            result.getTeacher().getFullName());
        return Optional.ofNullable(result);
    }

    @Override
    public List<Lesson> getAll() {
        log.debug("Getting all lessons");
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), lessonExtractor);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public void update(Lesson lesson) {
        log.debug("Updating lesson id({})", lesson.getId());
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            lesson.getTeacher().getId(), lesson.getCourse().getId(),
            lesson.getRoom().getId(), lesson.getTimeStart(),
            lesson.getTimeEnd(), lesson.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update lesson id({})", lesson.getId());
            throw new DAOException(String.format(MESSAGE_UPDATE_LESSON_NOT_FOUND,
                lesson.getId()));
        } else {
            log.info("Lesson id({}) updated successfully", lesson.getId());
        }
    }

    @Override
    public void delete(Lesson lesson) {
        log.debug("Deleting lesson id({})", lesson.getId());
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), lesson.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete lesson id({})", lesson.getId());
            throw new DAOException(String.format(MESSAGE_DELETE_LESSON_NOT_FOUND,
                lesson.getId()));
        } else {
            log.info("Delete lesson id({})", lesson.getId());
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting lesson id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete lesson id({})", id);
            throw new DAOException(String.format(MESSAGE_DELETE_LESSON_NOT_FOUND,
                id));
        } else {
            log.info("Delete lesson id({})", id);
        }
    }

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {
        log.debug("Adding student id({}) to lesson id({})", studentId, lessonId);
        try {
            jdbcTemplate.update(
                env.getRequiredProperty(QUERY_ADD_STUDENT_TO_LESSON),
                lessonId, studentId);
        } catch (DataAccessException e) {
            log.error("An error occurred while adding student id({}) to " +
                "lesson id({})", studentId, lessonId);
            throw new DAOException(e.getMessage(), e);
        }
        log.info("Student id({}) added to lesson({}) successfully", studentId,
            lessonId);
    }

    @Override
    public void deleteAllStudentsFromLesson(int lessonId) {
        log.debug("Deleting all students from lesson id({})", lessonId);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE_ALL_STUDENTS_FROM_LESSON),
            lessonId);
        log.info("Delete {} students from lesson id({})", numberDeletedRows,
            lessonId);
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {
        log.debug("Removing student id({}) from lesson id({})", studentId, lessonId);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE_STUDENT_FROM_LESSON),
            lessonId, studentId);
        if (numberDeletedRows == 0) {
            log.warn("Can't remove student id({}) from lesson id({})",
                studentId, lessonId);
            throw new DAOException(String.format(
                MESSAGE_STUDENT_NOT_FOUND_IN_LESSON, studentId, lessonId));
        } else {
            log.info("Student id({}) successfully removed from lesson id({})",
                studentId, lessonId);
        }
    }

    @Override
    public List<Lesson> getAllForTeacher(int teacherId) {
        log.debug("Getting all lessons for teacher id({})", teacherId);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_TEACHER),
            lessonExtractor, teacherId);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForRoom(int roomId) {
        log.debug("Getting all lessons for room id({})", roomId);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_ROOM),
            lessonExtractor, roomId);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForStudent(int studentId) {
        log.debug("Getting all lessons for student id({})", studentId);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_STUDENT),
            lessonExtractor, studentId);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllWithFilter(LessonFilter filter) {
        log.debug("Getting all lessons with ({})", filter);
        List<Lesson> lessons = jdbcTemplate.query(createQuery(filter), lessonExtractor);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for student id({}) from {} to {}", studentId,
            startTime, endTime);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_STUDENT_FOR_TIME_PERIOD),
            lessonExtractor, studentId, startTime, endTime);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForTeacherForTimePeriod(int teacherId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for teacher id({}) from {} to {}", teacherId,
            startTime, endTime);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_TEACHER_FOR_TIME_PERIOD),
            lessonExtractor, teacherId, startTime, endTime);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public List<Lesson> getAllForRoomForTimePeriod(int roomId,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        log.debug("Getting lessons for room id({}) from {} to {}", roomId,
            startTime, endTime);
        List<Lesson> lessons = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL_FOR_ROOM_FOR_TIME_PERIOD),
            lessonExtractor, roomId, startTime, endTime);
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    private String createQuery(LessonFilter filter) {
        log.debug("create sql query with filters");
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
