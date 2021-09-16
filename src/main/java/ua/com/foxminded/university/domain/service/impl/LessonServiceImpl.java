package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student %s is not available";
    private static final String FOUND_LESSONS = "Found {} lessons";
    private static final String MESSAGE_FILTER_NOT_SELECT = "Select at least one filter";

    private final LessonDao lessonDao;
    private final StudentService studentService;
    private final StudentDao studentDao;

    @Override
    public void add(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before adding", lesson.getId());
        checkLesson(lesson);
        log.debug("Adding lesson id({})", lesson.getId());
        lessonDao.add(lesson);
        log.info("Lesson id({}) added successfully", lesson.getId());
    }

    @Override
    public void update(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before updating", lesson.getId());
        checkLesson(lesson);
        log.debug("Updating lesson id({})", lesson.getId());
        lessonDao.update(lesson);
        log.info("Lesson id({}) updated successfully", lesson.getId());
    }

    @Override
    public Lesson getById(int id) {
        log.debug("Getting lesson by id({})", id);
        Lesson lesson = lessonDao.getById(id).orElse(new Lesson());
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
    public void addStudentToLesson(int lessonId, int studentId) {
        log.debug("Getting lesson by lessonId({})", lessonId);
        Lesson lesson = lessonDao.getById(lessonId)
            .orElseThrow(() -> new ServiceException(
                String.format("Lesson id(%d) not found", lessonId)));
        Student student = studentDao.getById(studentId)
            .orElseThrow(() -> new ServiceException(
                String.format("Student id(%d) not found", studentId)));
        checkAndSaveStudentToLesson(lesson, student);
    }


    @Override
    @Transactional
    public void addStudentsFromGroupToLesson(int groupId, int lessonId) {
        log.debug("Getting lesson by lessonId({})", lessonId);
        Lesson lesson = lessonDao.getById(lessonId)
            .orElseThrow(() -> new ServiceException(
                String.format("Lesson id(%d) not found", lessonId)
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
                                                      LocalDateTime endTime){
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
                                                      LocalDateTime endTime){
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
                                                   LocalDateTime endTime){
        log.debug("Getting lessons for room id({}) from {} to {})", roomId,
            startTime, endTime);
        List<Lesson> lessonsForRoom = lessonDao
            .getAllForRoomForTimePeriod(roomId, startTime, endTime);
        log.info(FOUND_LESSONS, lessonsForRoom.size());
        return lessonsForRoom;
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId){
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

    private void checkAndSaveStudentToLesson(Lesson lesson, Student student) {
        if (student.isActive()) {
            if (checkAvailableStudentForLesson(lesson, student)) {
                lessonDao.addStudentToLesson(lesson.getId(), student.getId());
            } else {
                log.warn("Student id({}) is not available to add to the lesson id" +
                    "({})", student.getId(), lesson.getId());
                throw new ServiceException(String.format(MESSAGE_STUDENT_NOT_AVAILABLE, student));
            }
            log.info("Student id({}) added to lesson({}) successfully", student.getId(),
                lesson.getId());
        } else {
            log.warn("Student id({}} is inactive)", student.getId());
            throw new ServiceException(String.format("Student id(%d) is inactive", student.getId()));
        }
    }

    private void checkLesson(Lesson lesson) throws ServiceException {
        log.debug("Start checking the lesson id({})", lesson.getId());
        if (!checkTeacher(lesson)) {
            log.warn("Teacher id({}) is not available for the lesson id({})",
                lesson.getTeacher().getId(), lesson.getId());
            throw new ServiceException(
                String.format(MESSAGE_TEACHER_NOT_AVAILABLE,
                    lesson.getTeacher().toString()));
        }
        log.info("Teacher id({}) is available", lesson.getTeacher().getId());
        if (!checkRoom(lesson)) {
            log.warn("Room id({}) is not available for the lesson id({})",
                lesson.getRoom().getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_ROOM_NOT_AVAILABLE,
                lesson.getRoom().toString()));
        }
        log.info("Room id({}) is available", lesson.getRoom().getId());
        log.info("Lesson id({}) checking passed", lesson.getId());
    }

    private boolean checkTeacher(Lesson checkedLesson) {
        log.debug("Checking the teacher id({})", checkedLesson.getTeacher().getId());
        Teacher teacher = checkedLesson.getTeacher();
        List<Lesson> lessonsByTeacher = lessonDao
            .getAllForTeacher(teacher.getId());
        if (lessonsByTeacher.isEmpty()) {
            return true;
        }
        return checkTime(checkedLesson, lessonsByTeacher);
    }

    private boolean checkRoom(Lesson checkedLesson) {
        log.debug("Checking the room id({})", checkedLesson.getRoom().getId());
        Room room = checkedLesson.getRoom();
        List<Lesson> lessonsByRoom = lessonDao.getAllForRoom(room.getId());
        if (lessonsByRoom.isEmpty()) {
            return true;
        }
        return checkTime(checkedLesson, lessonsByRoom);
    }

    private boolean checkAvailableStudentForLesson(Lesson checkedLesson,
                                                   Student checkedStudent) {
        log.debug("Checking student id({}) availability",
            checkedStudent.getId());
        List<Lesson> lessonsByStudent = getLessonsForStudent(checkedStudent);
        return checkTime(checkedLesson, lessonsByStudent);
    }

    private List<Lesson> getLessonsForStudent(Student student) {
        log.debug("Getting all lessons for student id({})", student.getId());
        return lessonDao.getAllForStudent(student.getId());
    }

    private boolean checkTime(Lesson checkedLesson, List<Lesson> lessons) {
        log.debug("Checking the intersection of time lesson id({}) with other lessons",
            checkedLesson.getId());
        LocalDateTime timeStartCheckedLesson = checkedLesson.getTimeStart();
        LocalDateTime timeEndCheckedLesson = checkedLesson.getTimeEnd();
        for (Lesson lesson : lessons) {
            LocalDateTime timeStartLesson = lesson.getTimeStart();
            LocalDateTime timeEndLesson = lesson.getTimeEnd();
            if ((timeStartCheckedLesson.isAfter(timeStartLesson)
                && timeStartCheckedLesson.isBefore(timeEndLesson))
                || (timeEndCheckedLesson.isAfter(timeStartLesson)
                && timeEndCheckedLesson.isBefore(timeEndLesson))) {
                log.warn("Time lesson id({}) intersect with time lesson id({})",
                    checkedLesson.getId(), lesson.getId());
                return false;
            }
        }
        log.info("Checking passed");
        return true;
    }

}