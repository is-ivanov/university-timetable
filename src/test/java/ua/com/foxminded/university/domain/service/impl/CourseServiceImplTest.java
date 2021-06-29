package ua.com.foxminded.university.domain.service.impl;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    private CourseServiceImpl courseService;

    @Mock
    private CourseDao courseDaoMock;

    @BeforeEach
    void setUp() throws Exception {
        courseService = new CourseServiceImpl(courseDaoMock);
    }

    @Test
    @DisplayName("when call add method then shoud call courseDao once")
    void testAdd() {
        Course course = new Course();
        courseService.add(course);

        verify(courseDaoMock).add(course);
    }

}
