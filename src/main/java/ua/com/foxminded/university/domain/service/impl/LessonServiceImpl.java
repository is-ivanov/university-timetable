package ua.com.foxminded.university.domain.service.impl;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student %s is not available";

    private final LessonDao lessonDao;

    @Autowired
    public LessonServiceImpl(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    @Override
    public void add(Lesson lesson) throws ServiceException {
        checkLesson(lesson);
        lessonDao.add(lesson);
    }

    @Override
    public void update(Lesson updatedLesson) throws ServiceException {
        checkLesson(updatedLesson);
        lessonDao.update(updatedLesson);
    }

    @Override
    public Lesson getById(int id) throws ServiceException {
        Lesson lesson;
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

    @Override
    public void addStudentToLesson(Lesson lesson, Student student) throws ServiceException {
        if (checkAvailableStudentForLesson(lesson, student)) {
            lessonDao.addStudentToLesson(lesson.getId(), student.getId());
        } else {
            throw new ServiceException(String.format(MESSAGE_STUDENT_NOT_AVAILABLE, student));
        }
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
        if (lessonsByTeacher.isEmpty()) {
            return true;
        }
        return checkTime(checkedLesson, lessonsByTeacher);
    }

    private boolean checkRoom(Lesson checkedLesson) {
        Room room = checkedLesson.getRoom();
        List<Lesson> lessonsByRoom = lessonDao.getAllByRoom(room.getId());
        if (lessonsByRoom.isEmpty()){
            return true;
        }
        return checkTime(checkedLesson, lessonsByRoom);
    }

    private boolean checkAvailableStudentForLesson(Lesson checkedLesson,
                                                   Student checkedStudent) {
        List<Lesson> lessonsByStudent = getLessonsByStudent(checkedStudent);
        return checkTime(checkedLesson, lessonsByStudent);
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
            if ((timeStartCheckedLesson.isAfter(timeStartLesson)
                && timeStartCheckedLesson.isBefore(timeEndLesson))
                || (timeEndCheckedLesson.isAfter(timeStartLesson)
                && timeEndCheckedLesson.isBefore(timeEndLesson))) {
                return false;
            }
        }
        return true;
    }

}