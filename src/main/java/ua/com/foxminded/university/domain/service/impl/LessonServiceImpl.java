package ua.com.foxminded.university.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

public class LessonServiceImpl implements LessonService {

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
        for (Student student : lesson.getStudents()) {
            if (!checkStudent(lesson, student)) {
                throw new ServiceException(String.format(
                        "Student %c is not available", student.toString()));
            }
            lessonDao.addStudentToLesson(lessonId, student.getId());

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
    public void update(Lesson lesson) throws ServiceException {
        checkLesson(lesson);
        lessonDao.update(lesson);
        // TODO update students in lesson
    }

    @Override
    public void delete(Lesson lesson) {
        lessonDao.deleteAllStudentsFromLesson(lesson.getId());
        lessonDao.delete(lesson);
    }

    private void checkLesson(Lesson lesson) throws ServiceException {
        if (!checkTeacher(lesson)) {
            throw new ServiceException(
                    String.format("Teacher %s is not available",
                            lesson.getTeacher().toString()));
        }
        if (!checkRoom(lesson)) {
            throw new ServiceException(String.format("Room %s is not available",
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

    private boolean checkStudent(Lesson checkedLesson, Student checkedStudent) {
        List<Lesson> lessonsByStudent = lessonDao
                .getAllByStudent(checkedStudent.getId());
        return checkTime(checkedLesson, lessonsByStudent);
    }

    private boolean checkTime(Lesson checkedLesson, List<Lesson> lessons) {
        LocalDateTime timeStartNewLesson = checkedLesson.getTimeStart();
        LocalDateTime timeEndNewLesson = checkedLesson.getTimeEnd();
        for (Lesson lesson : lessons) {
            LocalDateTime timeStartLesson = lesson.getTimeStart();
            LocalDateTime timeEndLesson = lesson.getTimeEnd();
            if (timeStartNewLesson.isAfter(timeStartLesson)
                    || timeStartNewLesson.isBefore(timeEndLesson)
                    || timeEndNewLesson.isAfter(timeStartLesson)
                    || timeEndNewLesson.isBefore(timeEndLesson)) {
                return false;
            }
        }
        return true;
    }


}
