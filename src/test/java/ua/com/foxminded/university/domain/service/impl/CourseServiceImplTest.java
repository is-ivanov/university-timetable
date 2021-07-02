package ua.com.foxminded.university.domain.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    private CourseServiceImpl courseService;

    @Mock
    private CourseDao courseDaoMock;

    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call add method then should call courseDao once")
    void testAdd_CallDaoOnce() {
        Course course = new Course();
        courseService.add(course);
        verify(courseDaoMock).add(course);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Course then method should return Course")
        void testReturnExpectedCourse() throws Exception {
            Course expectedCourse = new Course();
            expectedCourse.setId(1);
            expectedCourse.setName("Course name");
            when(courseDaoMock.getById(1))
                    .thenReturn(Optional.of(expectedCourse));
            assertEquals(expectedCourse, courseService.getById(1));
        }
        
        @Test
        @DisplayName("when Dao return empty Optional then method should return empty Course")
        void testReturnEmptyCourse() throws Exception {
            Optional<Course> optional = Optional.empty();
            when(courseDaoMock.getById(1)).thenReturn(optional);
            assertEquals(new Course(), courseService.getById(1));
        }
        
        @Test
        @DisplayName("when Dao return DAOException then method should throw ServiceException")
        void testThrowException() throws Exception {
            when(courseDaoMock.getById(1)).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                    () -> courseService.getById(1));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List courses then method return this List")
    void testGetAll_ReturnListCourses(){
        List<Course> expectedCourses = new ArrayList<>();
        when(courseDaoMock.getAll()).thenReturn(expectedCourses);
        assertEquals(expectedCourses, courseService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call courseDao once")
    void testUpdate_CallDaoOnce(){
        Course course = new Course();
        courseService.update(course);
        verify(courseDaoMock).update(course);
    }

    @Test
    @DisplayName("test 'delete' when call update method then should call courseDao once")
    void testDelete_CallDaoOnce(){
        Course course = new Course();
        courseService.delete(course);
        verify(courseDaoMock).delete(course);
    }

}
