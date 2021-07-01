package ua.com.foxminded.university.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<Student> unavailableStudents = new ArrayList<>();
        for (Student student : lesson.getStudents()) {
            List<Lesson> lessonsByStudent = getLessonsByStudent(student);
            if (checkTime(lesson, lessonsByStudent)) {
                lessonDao.addStudentToLesson(lessonId, student.getId());
            } else {
                unavailableStudents.add(student);
            }
        }
        if (!unavailableStudents.isEmpty()) {
            throw new ServiceException(
                    String.format("Students %s are not available",
                            unavailableStudents.toString()));
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
    public void update(Lesson updatedLesson) throws ServiceException {
        checkLesson(updatedLesson);
        lessonDao.update(updatedLesson);
        int lessonId = updatedLesson.getId();
        try {
            Lesson existingLesson = lessonDao.getById(lessonId)
                    .orElseThrow(() -> new ServiceException(String.format(
                            "There is not lesson with id=%d in base",
                            lessonId)));
            // пройти по студентам сущ. урока
            existingLesson.getStudents().forEach(student -> {
                boolean isStudentExistingOnUpdatedLesson = updatedLesson
                        .getStudents()
                        .stream().anyMatch(student::equals);
                if (!isStudentExistingOnUpdatedLesson) {
                    lessonDao.deleteStudentFromLesson(lessonId,
                            student.getId());
                }
            });
            List<Student> unavailableStudents = new ArrayList<>();
            // пройти по студентам изменённого урока,
            updatedLesson.getStudents().forEach(updStudent -> {
                List<Lesson> lessonsByStudent = getLessonsByStudent(updStudent);
                if (checkTime(updatedLesson, lessonsByStudent)) {
                    // если проверку прошёл, то проверяем есть ли он в сущ. уроке.
                    boolean isStudentExistingOnExistedLesson = existingLesson
                            .getStudents().stream().anyMatch(updStudent::equals);
                    if (!isStudentExistingOnExistedLesson) {
                        //если нет, то сохраняем в базу
                        lessonDao.addStudentToLesson(lessonId, updStudent.getId());
                    }
                } else {
                    //если проверку не прошёл, то сохраняем в список недоступных студентов
                    unavailableStudents.add(updStudent);
                }
            });
            if (!unavailableStudents.isEmpty()) {
                throw new ServiceException(
                        String.format("Students %s are not available",
                                unavailableStudents.toString()));
            }
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
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

    private boolean checkStudent(Lesson checkedLesson, Student checkedStudent)
            throws ServiceException {
        List<Lesson> lessonsByStudent = getLessonsByStudent(checkedStudent);
        if (!checkTime(checkedLesson, lessonsByStudent)) {
            throw new ServiceException(
                    String.format("Student %c is not available for this lesson",
                            checkedStudent.toString()));
        } else {
            return true;
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
