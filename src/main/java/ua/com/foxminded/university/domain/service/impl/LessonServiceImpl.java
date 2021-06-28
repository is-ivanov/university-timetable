package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
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
    public void add(Lesson lesson) {
        lessonDao.add(lesson);
        int lessonId = lesson.getId();
        lesson.getStudents().forEach(student -> lessonDao
                .addStudentToLesson(lessonId, student.getId()));
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
    public void update(Lesson lesson) {
        lessonDao.update(lesson);
    }

    @Override
    public void delete(Lesson lesson) {
        lessonDao.deleteAllStudentsFromLesson(lesson.getId());
        lessonDao.delete(lesson);
    }


}
