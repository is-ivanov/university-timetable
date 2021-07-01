package ua.com.foxminded.university.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class LessonServiceImpl implements LessonService {

    private static final String MESSAGE_LESSON_NOT_FOUND = "There is not lesson with id=%d in base";
    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Students %s are not available";
    private LessonDao lessonDao;

    @Autowired
    public LessonServiceImpl(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    @Override
    public void add(Lesson lesson) throws ServiceException {
        checkLesson(lesson);
        lessonDao.add(lesson);
        int lessonId = lesson.getId();
        List<Student> unavailableStudents = new ArrayList<>();
        for (Student student : lesson.getStudents()) {
            if (checkAvailableStudentInLesson(lesson, student)) {
                lessonDao.addStudentToLesson(lessonId, student.getId());
            } else {
                unavailableStudents.add(student);
            }
        }
        checkUnavailableStudents(unavailableStudents);
    }

    @Override
    public void update(Lesson updatedLesson) throws ServiceException {
        checkLesson(updatedLesson);
        lessonDao.update(updatedLesson);
        int lessonId = updatedLesson.getId();
        try {
            Lesson originalLesson = lessonDao.getById(lessonId)
                    .orElseThrow(() -> new ServiceException(String.format(
                            MESSAGE_LESSON_NOT_FOUND,
                            lessonId)));
            originalLesson.getStudents().forEach(student -> {
                if (!checkExistsStudentInLesson(student, updatedLesson)) {
                    lessonDao.deleteStudentFromLesson(lessonId,
                            student.getId());
                }
            });
            List<Student> unavailableStudents = new ArrayList<>();
            updatedLesson.getStudents().forEach(updStudent -> {
                List<Lesson> lessonsByStudent = getLessonsByStudent(updStudent);
                if (checkTime(updatedLesson, lessonsByStudent)) {
                    if (!checkExistsStudentInLesson(updStudent, originalLesson)) {
                        lessonDao.addStudentToLesson(lessonId, updStudent.getId());
                    }
                } else {
                    unavailableStudents.add(updStudent);
                }
            });
            checkUnavailableStudents(unavailableStudents);
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Lesson getById(int id) throws ServiceException {
        Lesson lesson = null;
        try {
            lesson = lessonDao.getById(id).orElse(new Lesson());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return lesson;
    }

    @Override
    public List<Lesson> getAll() {
        return lessonDao.getAll();
    }

    @Override
    public void delete(Lesson lesson) {
        lessonDao.deleteAllStudentsFromLesson(lesson.getId());
        lessonDao.delete(lesson);
    }

    private void checkLesson(Lesson lesson) throws ServiceException {
        if (!checkTeacher(lesson)) {
            throw new ServiceException(
                    String.format(MESSAGE_TEACHER_NOT_AVAILABLE,
                            lesson.getTeacher().toString()));
        }
        if (!checkRoom(lesson)) {
            throw new ServiceException(String.format(MESSAGE_ROOM_NOT_AVAILABLE,
                    lesson.getRoom().toString()));
        }
    }

    private boolean checkTeacher(Lesson checkedLesson) {
        Teacher teacher = checkedLesson.getTeacher();
        List<Lesson> lessonsByTeacher = lessonDao
                .getAllByTeacher(teacher.getId());
        return checkTime(checkedLesson, lessonsByTeacher);
    }

    private boolean checkRoom(Lesson checkedLesson) {
        Room room = checkedLesson.getRoom();
        List<Lesson> lessonsByRoom = lessonDao.getAllByRoom(room.getId());
        return checkTime(checkedLesson, lessonsByRoom);
    }

    private boolean checkAvailableStudentInLesson(Lesson checkedLesson,
            Student checkedStudent) {
        List<Lesson> lessonsByStudent = getLessonsByStudent(checkedStudent);
        return checkTime(checkedLesson, lessonsByStudent);
    }

    private boolean checkExistsStudentInLesson(Student checkedStudent,
            Lesson lesson) {
        return lesson.getStudents().stream().anyMatch(checkedStudent::equals);
    }

    private void checkUnavailableStudents(List<Student> unavailableStudents)
            throws ServiceException {
        if (!unavailableStudents.isEmpty()) {
            throw new ServiceException(
                    String.format(MESSAGE_STUDENT_NOT_AVAILABLE,
                            unavailableStudents.toString()));
        }
    }

    private List<Lesson> getLessonsByStudent(Student student) {
        return lessonDao.getAllByStudent(student.getId());
    }

    private boolean checkTime(Lesson checkedLesson, List<Lesson> lessons) {
        LocalDateTime timeStartCheckedLesson = checkedLesson.getTimeStart();
        LocalDateTime timeEndCheckedLesson = checkedLesson.getTimeEnd();
        for (Lesson lesson : lessons) {
            LocalDateTime timeStartLesson = lesson.getTimeStart();
            LocalDateTime timeEndLesson = lesson.getTimeEnd();
            if (timeStartCheckedLesson.isAfter(timeStartLesson)
                    || timeStartCheckedLesson.isBefore(timeEndLesson)
                    || timeEndCheckedLesson.isAfter(timeStartLesson)
                    || timeEndCheckedLesson.isBefore(timeEndLesson)) {
                return false;
            }
        }
        return true;
    }


}
