package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final String MESSAGE_COURSE_NOT_FOUND = "Course id(%d) not found";

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
        Course course = courseDao.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_COURSE_NOT_FOUND, id)));
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

    @Override
    public void delete(int id) {
        log.debug("Deleting course id({})", id);
        courseDao.delete(id);
        log.info("Delete course id({})", id);
    }

    @Override
    public Page<Course> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of courses", pageable.getPageNumber());
        Page<Course> pageCourses = courseDao.getAllSortedPaginated(pageable);
        log.info("Found {} courses on page {}", pageCourses.getContent().size(),
            pageCourses.getNumber());
        return pageCourses;
    }
}
