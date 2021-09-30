package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.checker.interfaces.LessonChecker;
import ua.com.foxminded.university.domain.checker.interfaces.StudentChecker;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.exception.ServiceException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {

    private static final String FOUND_LESSONS = "Found {} lessons";
    private static final String MESSAGE_FILTER_NOT_SELECT = "Select at least one filter";
    public static final String MESSAGE_LESSON_NOT_FOUND = "Lesson id(%d) not found";

    private final LessonDao lessonDao;
    private final StudentService studentService;
    private final StudentDao studentDao;
    private final LessonChecker lessonChecker;
    private final StudentChecker studentChecker;

    @Transactional
    @Override
    public void add(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before adding", lesson.getId());
        lessonChecker.check(lesson);
        log.debug("Adding lesson id({})", lesson.getId());
        lessonDao.add(lesson);
        log.info("Lesson id({}) added successfully", lesson.getId());
    }

    @Transactional
    @Override
    public void update(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before updating", lesson.getId());
        lessonChecker.check(lesson);
        log.debug("Updating lesson id({})", lesson.getId());
        lessonDao.update(lesson);
        log.info("Lesson id({}) updated successfully", lesson.getId());
    }

    @Override
    public Lesson getById(int id) {
        log.debug("Getting lesson by id({})", id);
        Lesson lesson = lessonDao.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_LESSON_NOT_FOUND, id)));
        log.info("Found lesson [teacher {}, course {}, room {}]",
            lesson.getTeacher().getFullName(), lesson.getCourse().getName(),
            lesson.getRoom().getNumber());
        return lesson;
    }

    @Override
    public List<Lesson> getAll() {
        log.debug("Getting all lessons");
        List<Lesson> lessons = lessonDao.getAll();
        log.info(FOUND_LESSONS, lessons.size());
        return lessons;
    }

    @Override
    public void delete(Lesson lesson) {
        log.info("Start deleting lesson id({})", lesson.getId());
        log.debug("Deleting all students from lesson id({})", lesson.getId());
        lessonDao.deleteAllStudentsFromLesson(lesson.getId());
        log.debug("Deleting lesson id({})", lesson.getId());
        lessonDao.delete(lesson);
        log.info("Lesson id({}) deleted successfully", lesson.getId());
    }

    @Override
    public void delete(int id) {
        log.info("Start deleting lesson id({})", id);
        log.debug("Deleting all students from lesson id({})", id);
        lessonDao.deleteAllStudentsFromLesson(id);
        log.debug("Deleting lesson id({})", id);
        lessonDao.delete(id);
        log.info("Lesson id({}) deleted successfully", id);
    }

    @Override
    public void addStudentToLesson(Lesson lesson, Student student) {
        log.debug("Start adding student id({}) to lesson id({})",
            student.getId(), lesson.getId());
        checkAndSaveStudentToLesson(lesson, student);
    }

    @Override
    @Transactional
    public void addStudentToLesson(int lessonId, int studentId) {
        log.debug("Getting lesson by lessonId({})", lessonId);
        Lesson lesson = lessonDao.getById(lessonId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_LESSON_NOT_FOUND, lessonId)));
        Student student = studentDao.getById(studentId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Student id(%d) not found", studentId)));
        checkAndSaveStudentToLesson(lesson, student);
    }


    @Override
    @Transactional
    public void addStudentsFromGroupToLesson(int groupId, int lessonId) {
        log.debug("Getting lesson by lessonId({})", lessonId);
        Lesson lesson = lessonDao.getById(lessonId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_LESSON_NOT_FOUND, lessonId)
            ));
        log.debug("Getting active students from group id({})", groupId);
        List<Student> studentsFromGroup =
            studentService.getFreeStudentsFromGroup(groupId,
                lesson.getTimeStart(), lesson.getTimeEnd());
        for (Student student : studentsFromGroup) {
            checkAndSaveStudentToLesson(lesson, student);
        }
        log.info("{} students is added successfully", studentsFromGroup.size());
    }

    @Override
    public List<Lesson> getAllWithFilter(LessonFilter filter) {
        log.debug("Getting all lessons with ({})", filter);
        log.debug("Checking filter conditions");
        if (filter.getFacultyId() != null || filter.getDepartmentId() != null ||
            filter.getTeacherId() != null || filter.getCourseId() != null ||
            filter.getRoomId() != null || filter.getDateFrom() != null ||
            filter.getDateTo() != null) {
            return lessonDao.getAllWithFilter(filter);
        } else {
            log.warn("Filter is empty");
            throw new ServiceException(MESSAGE_FILTER_NOT_SELECT);
        }
    }

    @Override
    public List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for student id({}) from {} to {})", studentId,
            startTime, endTime);
        List<Lesson> lessonsForStudent = lessonDao
            .getAllForStudentForTimePeriod(studentId, startTime, endTime);
        log.info(FOUND_LESSONS, lessonsForStudent.size());
        return lessonsForStudent;
    }

    @Override
    public List<Lesson> getAllForTeacherForTimePeriod(int teacherId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for teacher id({}) from {} to {})", teacherId,
            startTime, endTime);
        List<Lesson> lessonsForTeacher = lessonDao
            .getAllForTeacherForTimePeriod(teacherId, startTime, endTime);
        log.info(FOUND_LESSONS, lessonsForTeacher.size());
        return lessonsForTeacher;
    }

    @Override
    public List<Lesson> getAllForRoomForTimePeriod(int roomId,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime) {
        log.debug("Getting lessons for room id({}) from {} to {})", roomId,
            startTime, endTime);
        List<Lesson> lessonsForRoom = lessonDao
            .getAllForRoomForTimePeriod(roomId, startTime, endTime);
        log.info(FOUND_LESSONS, lessonsForRoom.size());
        return lessonsForRoom;
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {
        log.debug("Removing student id({}) from lesson id({})", studentId, lessonId);
        lessonDao.removeStudentFromLesson(lessonId, studentId);
        log.info("Student id({}) successfully removed from lesson id({})",
            studentId, lessonId);
    }

    @Override
    @Transactional
    public void removeStudentsFromLesson(int lessonId, int[] studentIds) {
        log.debug("Removing students id({}) from lesson id({})", studentIds, lessonId);
        for (int studentId : studentIds) {
            lessonDao.removeStudentFromLesson(lessonId, studentId);
        }
        log.info("Students id({}) successfully removed from lesson id({})",
            studentIds, lessonId);
    }

    @Override
    public List<Lesson> getLessonsForStudent(Student student) {
        log.debug("Getting all lessons for student id({})", student.getId());
        return lessonDao.getAllForStudent(student.getId());
    }

    private void checkAndSaveStudentToLesson(Lesson lesson, Student student) {
        if (student.isActive()) {
            studentChecker.check(student, lesson);
            lessonDao.addStudentToLesson(lesson.getId(), student.getId());
            log.info("Student id({}) added to lesson({}) successfully", student.getId(),
                lesson.getId());
        } else {
            log.warn("Student id({}} is inactive)", student.getId());
            throw new ServiceException(String.format("Student id(%d) is inactive", student.getId()));
        }
    }

}