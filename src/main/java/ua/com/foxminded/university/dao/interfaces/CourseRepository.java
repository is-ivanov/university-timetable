package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.entity.Course;

public interface CourseRepository extends Repository<Course> {

    int countAll();

    Page<Course> getAllSortedPaginated (Pageable pageable);
}
