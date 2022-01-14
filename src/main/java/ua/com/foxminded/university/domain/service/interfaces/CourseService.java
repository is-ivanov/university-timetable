package ua.com.foxminded.university.domain.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.entity.Course;

public interface CourseService extends Service<Course> {

    Page<Course> getAllSortedPaginated(Pageable pageable);
}
