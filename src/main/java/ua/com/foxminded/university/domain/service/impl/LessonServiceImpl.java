package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.LessonRepository;
import ua.com.foxminded.university.dao.interfaces.StudentRepository;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.exception.ServiceException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LessonServiceImpl implements LessonService {

    public static final String FOUND_LESSONS = "Found {} lessons";
    public static final String MESSAGE_FILTER_NOT_SELECT = "Select at least one filter";
    public static final String MESSAGE_LESSON_NOT_FOUND = "Lesson id(%d) not found";
    public static final String MESSAGE_LESSON_OVERLAP = "Lesson id(%d) overlap with lesson id(%d)";
    public static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher id(%d) is not available";
    public static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student id(%d) is not available";
    public static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room id(%d) is not available";

    private final LessonRepository lessonRepository;
    private final LessonDtoMapper lessonDtoMapper;
    private final StudentRepository studentRepository;

    @Override
    public void add(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before adding", lesson.getId());
        checkLesson(lesson);
        log.debug("Adding lesson id({})", lesson.getId());
        lessonRepository.save(lesson);
        log.debug("Lesson id({}) added successfully", lesson.getId());
    }

    @Override
    public void update(Lesson lesson) throws ServiceException {
        log.debug("Check lesson id({}) before updating", lesson.getId());
        checkLesson(lesson);
        log.debug("Updating lesson id({})", lesson.getId());
        lessonRepository.save(lesson);
        log.debug("Lesson id({}) updated successfully", lesson.getId());
    }

    @Override
    public LessonDto getById(int id) {
        log.debug("Getting lesson by id({})", id);
        Lesson lesson = getLessonById(id);
        log.debug("Found lesson [teacher {}, course {}, room {}]",
            lesson.getTeacher().getFullName(), lesson.getCourse().getName(),
            lesson.getRoom().getNumber());
        return lessonDtoMapper.toLessonDto(lesson);
    }

    @Override
    public List<LessonDto> getAll() {
        log.debug("Getting all lessons");
        List<Lesson> lessons = lessonRepository.findAll();
        log.debug(FOUND_LESSONS, lessons.size());
        return lessonDtoMapper.toLessonDtos(lessons);
    }

    @Override
    public void delete(int id) {
        log.debug("Start deleting lesson id({})", id);
        log.debug("Deleting all students from lesson id({})", id);
        lessonRepository.deleteAllStudentsFromLesson(id);
        log.debug("Deleting lesson id({})", id);
        lessonRepository.deleteById(id);
        log.debug("Lesson id({}) deleted successfully", id);
    }

    @Override
    public void addStudentToLesson(int lessonId, int studentId) {
        log.debug("Getting lessonId({}) and studentId({})", lessonId, studentId);
        Lesson lesson = getLessonById(lessonId);
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Student id(%d) not found", studentId)));
        checkAndSaveStudentToLesson(lesson, student);
    }


    @Override
    public void addStudentsFromGroupToLesson(int groupId, int lessonId) {
        log.debug("Getting lesson by lessonId({})", lessonId);
        Lesson lesson = getLessonById(lessonId);
        log.debug("Getting active students from group id({})", groupId);
        List<Student> studentsFromGroup =
            studentRepository.findFreeStudentsFromGroup(groupId,
                lesson.getTimeStart(), lesson.getTimeEnd());
        for (Student student : studentsFromGroup) {
            checkAndSaveStudentToLesson(lesson, student);
        }
        log.debug("{} students is added successfully", studentsFromGroup.size());
    }

    @Override
    public List<LessonDto> getAllWithFilter(LessonFilter filter) {
        log.debug("Getting all lessons with ({})", filter);
        log.debug("Checking filter conditions");
        if (filter.getFacultyId() != null || filter.getDepartmentId() != null ||
            filter.getTeacherId() != null || filter.getCourseId() != null ||
            filter.getRoomId() != null || filter.getDateFrom() != null ||
            filter.getDateTo() != null) {
            List<Lesson> filteredLessons = lessonRepository.findAllWithFilter(filter);
            return lessonDtoMapper.toLessonDtos(filteredLessons);
        } else {
            log.warn("Filter is empty");
            throw new ServiceException(MESSAGE_FILTER_NOT_SELECT);
        }
    }

    @Override
    public List<LessonDto> getAllForStudentForTimePeriod(int studentId,
                                                         LocalDateTime startTime,
                                                         LocalDateTime endTime) {
        log.debug("Getting lessons for student id({}) from {} to {})", studentId,
            startTime, endTime);
        List<Lesson> lessonsForStudent = lessonRepository
            .findAllForStudentForTimePeriod(studentId, startTime, endTime);
        log.debug(FOUND_LESSONS, lessonsForStudent.size());
        return lessonDtoMapper.toLessonDtos(lessonsForStudent);
    }

    @Override
    public List<LessonDto> getAllForTeacherForTimePeriod(int teacherId,
                                                         LocalDateTime startTime,
                                                         LocalDateTime endTime) {
        log.debug("Getting lessons for teacher id({}) from {} to {})", teacherId,
            startTime, endTime);
        List<Lesson> lessonsForTeacher = lessonRepository
            .findAllForTeacherForTimePeriod(teacherId, startTime, endTime);
        log.debug(FOUND_LESSONS, lessonsForTeacher.size());
        return lessonDtoMapper.toLessonDtos(lessonsForTeacher);
    }

    @Override
    public List<LessonDto> getAllForRoomForTimePeriod(int roomId,
                                                      LocalDateTime startTime,
                                                      LocalDateTime endTime) {
        log.debug("Getting lessons for room id({}) from {} to {})", roomId,
            startTime, endTime);
        List<Lesson> lessonsForRoom = lessonRepository
            .getAllForRoomForTimePeriod(roomId, startTime, endTime);
        log.debug(FOUND_LESSONS, lessonsForRoom.size());
        return lessonDtoMapper.toLessonDtos(lessonsForRoom);
    }

    @Override
    public void removeStudentFromLesson(int lessonId, int studentId) {
        log.debug("Removing student id({}) from lesson id({})", studentId, lessonId);
        lessonRepository.removeStudentFromLesson(lessonId, studentId);
        log.debug("Student id({}) successfully removed from lesson id({})",
            studentId, lessonId);
    }

    @Override
    public void removeStudentsFromLesson(int lessonId, int[] studentIds) {
        log.debug("Removing students id({}) from lesson id({})", studentIds, lessonId);
        for (int studentId : studentIds) {
            lessonRepository.removeStudentFromLesson(lessonId, studentId);
        }
        log.debug("Students id({}) successfully removed from lesson id({})",
            studentIds, lessonId);
    }

    private void checkAndSaveStudentToLesson(Lesson lesson, Student student) {
        if (student.isActive()) {
            checkStudent(student, lesson);
            lesson.getStudents().add(student);
            log.debug("Student id({}) added to lesson({}) successfully", student.getId(),
                lesson.getId());
        } else {
            log.warn("Student id({}) is inactive)", student.getId());
            throw new ServiceException(String.format("Student id(%d) is inactive", student.getId()));
        }
    }

    private void checkLesson(Lesson lesson) {
        log.debug("Start checking the lesson id({})", lesson.getId());
        Teacher teacher = lesson.getTeacher();
        checkTeacher(teacher, lesson);
        log.debug("Teacher id({}) is available", teacher.getId());
        Room room = lesson.getRoom();
        checkRoom(room, lesson);
        log.debug("Room id({}) is available", room.getId());
        log.debug("Lesson id({}) checking passed", lesson.getId());
    }

    private void checkAvailableLesson(Lesson checkedLesson, List<Lesson> lessons) {
        if (checkLessonsIsEmpty(lessons)) {
            return;
        }
        checkLessonTime(checkedLesson, lessons);
    }

    private boolean checkLessonsIsEmpty(List<Lesson> lessons) {
        log.debug("Checking list lessons is empty");
        if (lessons.isEmpty()) {
            log.debug("Checking passed");
            return true;
        }
        return false;
    }

    private void checkLessonTime(Lesson checkedLesson, List<Lesson> lessons) {
        log.debug("Checking the intersection of time lesson id({}) with other lessons",
            checkedLesson.getId());
        LocalDateTime timeStartCheckedLesson = checkedLesson.getTimeStart();
        LocalDateTime timeEndCheckedLesson = checkedLesson.getTimeEnd();
        for (Lesson lesson : lessons) {
            if (!lesson.getId().equals(checkedLesson.getId())) {
                LocalDateTime timeStartLesson = lesson.getTimeStart();
                LocalDateTime timeEndLesson = lesson.getTimeEnd();
                boolean checkTimeStart = timeStartLesson.compareTo(timeEndCheckedLesson) < 1;
                boolean checkTimeEnd = timeEndLesson.compareTo(timeStartCheckedLesson) > -1;
                if (checkTimeStart && checkTimeEnd) {
                    log.warn("Time lesson id({}) intersect with time lesson id({})",
                        checkedLesson.getId(), lesson.getId());
                    throw new ServiceException(
                        String.format(MESSAGE_LESSON_OVERLAP, checkedLesson.getId(), lesson.getId()));
                }
            }
        }
        log.debug("Checking passed");
    }

    private void checkTeacher(Teacher teacher, Lesson lesson) {
        log.debug("Checking the teacher id({}) for lesson id({})", teacher.getId(),
            lesson.getId());
        List<Lesson> lessonsFromThisTeacher = lessonRepository.findAllByTeacherId(teacher.getId());
        try {
            checkAvailableLesson(lesson, lessonsFromThisTeacher);
        } catch (ServiceException e) {
            log.warn("Teacher id({}) is not available for the lesson id({})",
                teacher.getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_TEACHER_NOT_AVAILABLE,
                teacher.getId()), e);
        }
    }

    private void checkStudent(Student student, Lesson lesson) {
        log.debug("Checking the student id({}) for lesson id({})",
            student.getId(), lesson.getId());
        Set<Lesson> lessonsForStudent = student.getLessons();
        if (lessonsForStudent != null) {
            List<Lesson> lessonsFromThisStudent = new ArrayList<>(lessonsForStudent);
            try {
                checkAvailableLesson(lesson, lessonsFromThisStudent);
            } catch (ServiceException e) {
                log.warn("Student id({}) is not available for the lesson id({})",
                    student.getId(), lesson.getId());
                throw new ServiceException(String.format(MESSAGE_STUDENT_NOT_AVAILABLE,
                    student.getId()), e);
            }
        }
    }

    private void checkRoom(Room room, Lesson lesson) {
        log.debug("Checking the room id({}) for lesson id({})", room.getId(),
            lesson.getId());
        List<Lesson> lessonsFromThisRoom = lessonRepository.findAllByRoomId(room.getId());
        try {
            checkAvailableLesson(lesson, lessonsFromThisRoom);
        } catch (ServiceException e) {
            log.warn("Room id({}) is not available for lesson id({})",
                room.getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_ROOM_NOT_AVAILABLE,
                room.getId()), e);
        }
    }

    private Lesson getLessonById(int id) {
        return lessonRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_LESSON_NOT_FOUND, id)));
    }

}