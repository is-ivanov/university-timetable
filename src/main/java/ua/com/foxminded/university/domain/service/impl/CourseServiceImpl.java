package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.CourseRepository;
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

    private final CourseRepository courseRepo;

    @Override
    public void save(Course course) {
        log.debug("Saving {}", course);
        courseRepo.save(course);
        log.debug("{} saved successfully", course);
    }

    @Override
    public Course getById(int id) {
        log.debug("Getting course by id({})", id);
        Course course = courseRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
            String.format(MESSAGE_COURSE_NOT_FOUND, id)));
        log.debug("Found {}", course);
        return course;
    }

    @Override
    public List<Course> getAll() {
        log.debug("Getting all courses");
        List<Course> courses = courseRepo.findAll();
        log.debug("Found {} courses", courses.size());
        return courses;
    }

//    @Override
//    public void update(Course course) {
//        log.debug("Updating {}", course);
//        courseRepository.save(course);
//        log.debug("Update {}", course);
//    }

    @Override
    public void delete(int id) {
        log.debug("Deleting course id({})", id);
        courseRepo.deleteById(id);
        log.debug("Delete course id({})", id);
    }

    @Override
    public Page<Course> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of courses", pageable.getPageNumber());
        Page<Course> pageCourses = courseRepo.findAll(pageable);
        log.debug("Found {} courses on page {}", pageCourses.getContent().size(),
            pageCourses.getNumber());
        return pageCourses;
    }
}
