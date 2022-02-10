package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.CourseRepository;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CourseServiceImpl extends AbstractService<Course> implements CourseService {

    private final CourseRepository courseRepo;

    @Override
    protected JpaRepository<Course, Integer> getRepo() {
        return courseRepo;
    }

    @Override
    protected String getEntityName() {
        return Course.class.getSimpleName();
    }

}
