package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    @Autowired
    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public void add(Course course) {
        courseDao.add(course);
    }

    @Override
    public Course getById(int id) throws ServiceException {
        Course course;
        try {
            course = courseDao.getById(id).orElse(new Course());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return course;
    }

    @Override
    public List<Course> getAll() {
        return courseDao.getAll();
    }

    @Override
    public void update(Course course) {
        courseDao.update(course);
    }

    @Override
    public void delete(Course course) {
        courseDao.delete(course);
    }

}
