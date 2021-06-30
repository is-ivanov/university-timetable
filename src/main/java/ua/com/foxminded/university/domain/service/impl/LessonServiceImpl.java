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
        saveAllStudentsToLesson(lesson);
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
    public void update(Lesson updatedLesson) throws ServiceException {
        checkLesson(updatedLesson);
        lessonDao.update(updatedLesson);
        int lessonId = updatedLesson.getId();
        // получить этот же урок из базы
        try {
            Lesson existingLesson = lessonDao.getById(lessonId).get();
            // пройти по его студентам, если этого студента в изменённом уроке
            // нет, то удаляем
            existingLesson.getStudents().forEach(student -> {
                boolean isStudentExistingOnUpdatedLesson = updatedLesson
                        .getStudents()
                        .stream().anyMatch(student::equals);
                if (!isStudentExistingOnUpdatedLesson) {
                    lessonDao.deleteStudentFromLesson(lessonId,
                            student.getId());
                }
            });
            // пройти по студентам изменённого урока,
            for (Student student : updatedLesson.getStudents()) {
                // делаем проверку, если непрошел то эксепшн
                if (checkStudent(updatedLesson, student)) {

                } else {
                    throw new ServiceException();
                }
                // если проверку прошёл, то проверяем есть ли он в сущ. уроке.
                // Если
                // есть, то ничего
                // если нет, то добавляем

            }

        } catch (DAOException e) {
            throw new ServiceException(e);
        };
    }

    @Override
    public void delete(Lesson lesson) {
        lessonDao.deleteAllStudentsFromLesson(lesson.getId());
        lessonDao.delete(lesson);
    }

    private void saveAllStudentsToLesson(Lesson lesson)
            throws ServiceException {
        int lessonId = lesson.getId();
        for (Student student : lesson.getStudents()) {
            if (!checkStudent(lesson, student)) {
                throw new ServiceException(String.format(
                        "Student %c is not available for this lesson",
                        student.toString()));
            }
            lessonDao.addStudentToLesson(lessonId, student.getId());
        }
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

    private List<Lesson> getLessonsByStudent(Student student) {
        return lessonDao.getAllByStudent(student.getId());
    }

    private boolean checkStudent(Lesson checkedLesson, Student checkedStudent) {
        List<Lesson> lessonsByStudent = getLessonsByStudent(checkedStudent);
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
