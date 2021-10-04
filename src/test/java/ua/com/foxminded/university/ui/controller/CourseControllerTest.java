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
import java.util.Collections;
import java.util.List;

import static com.sun.deploy.uitoolkit.impl.awt.AWTClientPrintHelper.print;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_COURSE = "firstCourse";
    public static final String NAME_SECOND_COURSE = "secondCourse";
    public static final String URI_COURSES = "/courses";

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
        @DisplayName("when GET request without parameters then should use " +
            "@PageableDefault values")
        void testGetRequestWithoutParameters() throws Exception {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("course_name"));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, expectedCourses.size());
            List<Integer> pages = Collections.singletonList(1);

            when(courseServiceMock.getAllSortedPaginated(pageable)).thenReturn(pageCourses);
            when(pageSequenceCreatorMock.createPageSequence(1, 1)).thenReturn(pages);

            mockMvc.perform(get(URI_COURSES))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("course"),
                    model().attributeExists("courses", "page", "uri",
                        "newCourse", "pages"),
                    model().attribute("courses", expectedCourses),
                    model().attribute("page", pageCourses),
                    model().attribute("uri", URI_COURSES),
                    model().attribute("pages", pages)
                );

        }

        @Test
        @DisplayName("when GET request with parameter page = 2 then should use " +
            "this value and the rest of the parameters by default")
        void testGetRequestWithPage2() throws Exception {
            int page = 2;

            Pageable pageable = PageRequest.of(page, 10, Sort.by("course_name"));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, expectedCourses.size());

            when(courseServiceMock.getAllSortedPaginated(pageable)).thenReturn(pageCourses);

            mockMvc.perform(get(URI_COURSES)
                    .param("page", String.valueOf(page)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("course"),
                    model().attributeExists("courses", "page", "uri",
                        "newCourse", "pages"),
                    model().attribute("courses", expectedCourses),
                    model().attribute("page", pageCourses),
                    model().attribute("uri", URI_COURSES)
                );
        }

        @Test
        @DisplayName("when GET request with parameters page, size and sort then " +
            "should use this parameters")
        void testGetRequestWithPageSizeAndSort() throws Exception {
            int page = 3;
            int size = 7;
            String sort = "id,desc";

            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("id")));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, expectedCourses.size());

            when(courseServiceMock.getAllSortedPaginated(pageable)).thenReturn(pageCourses);

            mockMvc.perform(get(URI_COURSES)
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .param("sort", sort))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("course")
                );
        }
    }

    @Nested
    @DisplayName("test 'createCourse' method")
    class CreateCourseTest {

        @Test
        @DisplayName("when POST request with parameter courseName then should " +
            "call courseService.add once and redirect")
        void testPostRequestCreateCourse() throws Exception {
            mockMvc.perform(post(URI_COURSES)
                    .param("name", NAME_FIRST_COURSE))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            Course course = new Course();
            course.setName(NAME_FIRST_COURSE);

            verify(courseServiceMock).add(course);
        }
    }


}