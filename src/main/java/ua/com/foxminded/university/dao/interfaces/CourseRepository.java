package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
}
