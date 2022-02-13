package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.CourseRepository;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    public static final String COURSE_NAME = "Course name";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepoMock;

    @BeforeEach
    void setUp() {
        courseService = new CourseServiceImpl(courseRepoMock);
    }

    @Test
    @DisplayName("test 'create' when call add method then should call courseDao once")
    void testSave_CallDaoOnce() {
        Course course = new Course();
        courseService.create(course);
        verify(courseRepoMock).save(course);
    }

    @Nested
    @DisplayName("test 'findById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Course then method should return Course")
        void testReturnExpectedCourse() {
            Course expectedCourse = new Course();
            expectedCourse.setId(ID1);
            expectedCourse.setName(COURSE_NAME);
            Optional<Course> optionalCourse = Optional.of(expectedCourse);
            when(courseRepoMock.findById(ID1))
                    .thenReturn(optionalCourse);
            assertThat(courseService.findById(ID1)).isEqualTo(expectedCourse);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw " +
            "new MyEntityNotFoundException")
        void testReturnEmptyCourse() {
            when(courseRepoMock.findById(ID1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> courseService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Course with id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'getAll' when Repository return List courses then method return this List")
    void testGetAll_ReturnListCourses(){
        Course course1 = new Course();
        course1.setId(ID1);
        Course course2 = new Course();
        course2.setId(ID2);
        List<Course> expectedCourses = new ArrayList<>();
        expectedCourses.add(course1);
        expectedCourses.add(course2);
        when(courseRepoMock.findAll()).thenReturn(expectedCourses);
        assertThat(courseService.findAll()).isEqualTo(expectedCourses);
    }

    @Test
    @DisplayName("test 'delete' when call update method then should call courseDao once")
    void testDelete_CallDaoOnce(){
        int courseId = anyInt();
        courseService.delete(courseId);
        verify(courseRepoMock).deleteById(courseId);
    }

}