package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    @Override
    public void add(Course course) {
        log.debug("Adding {}", course);
        courseDao.add(course);
        log.info("{} added successfully", course);
    }

    @Override
    public Course getById(int id) {
        log.debug("Getting course by id({})", id);
        Course course = courseDao.getById(id).orElse(new Course());
        log.info("Found {}", course);
        return course;
    }

    @Override
    public List<Course> getAll() {
        log.debug("Getting all courses");
        List<Course> courses = courseDao.getAll();
        log.info("Found {} courses", courses.size());
        return courses;
    }

    @Override
    public void update(Course course) {
        log.debug("Updating {}", course);
        courseDao.update(course);
        log.info("Update {}", course);
    }

    @Override
    public void delete(Course course) {
        log.debug("Deleting {}", course);
        courseDao.delete(course);
        log.info("Delete {}", course);
    }

}
