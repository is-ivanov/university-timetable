package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_COURSE = "firstCourse";
    public static final String NAME_SECOND_COURSE = "secondCourse";

    private MockMvc mockMvc;

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private PageSequenceCreator pageSequenceCreatorMock;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(courseController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Nested
    @DisplayName("test 'showCourses' method")
    class ShowCoursesTest {

        @Test
        @DisplayName("when ")
        void test() throws Exception {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("course_name"));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, expectedCourses.size());

            when(courseServiceMock.getAllSortedPaginated(pageable)).thenReturn(pageCourses);


            mockMvc.perform(get("/courses"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("course")
                );

        }
    }


//
//    @Test
//    @DisplayName("Test showCourses")
//    void testShowCourses() throws Exception {
//        Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
//        Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);
//
//        List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
//        when(courseServiceMock.getAll()).thenReturn(expectedCourses);
//
//        mockMvc.perform(get("/courses"))
//            .andDo(print())
//            .andExpectAll(
//                status().isOk(),
//                view().name("course"),
//                model().attributeExists("courses"),
//                model().attribute("courses", expectedCourses)
//            );
//    }

}