package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.COURSE_ID1;
import static ua.com.foxminded.university.TestObjects.MESSAGE_FIRST_CAPITAL_LETTER;
import static ua.com.foxminded.university.domain.entity.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_COURSE = "FirstCourse";
    public static final String FAIL_NAME_FIRST_COURSE = "firstCourse";
    public static final String NAME_SECOND_COURSE = "SecondCourse";
    public static final String URI_COURSES = "/courses";
    public static final String URI_COURSES_ID = "/courses/{id}";

    private MockMvc mockMvc;

    @Captor
    ArgumentCaptor<Course> courseCaptor;

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
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("test 'showCourses' method")
    class ShowCoursesTest {

        @Test
        @DisplayName("when GET request without parameters then should use " +
            "@PageableDefault values")
        void getRequestWithoutParameters() throws Exception {
            int totalPages = 1;
            int currentPage = 0;
            Pageable pageable = PageRequest.of(currentPage, 10, Sort.by("name"));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, totalPages);
            List<Integer> pages = Collections.singletonList(1);

            when(courseServiceMock.findAll(pageable)).thenReturn(pageCourses);
            when(pageSequenceCreatorMock.createPageSequence(totalPages, currentPage + 1))
                .thenReturn(pages);

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
        void getRequestWithPage2() throws Exception {
            int page = 2;

            Pageable pageable = PageRequest.of(page, 10, Sort.by("name"));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, expectedCourses.size());

            when(courseServiceMock.findAll(pageable)).thenReturn(pageCourses);

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
        void getRequestWithPageSizeAndSort() throws Exception {
            int page = 3;
            int size = 7;
            String sort = "course_id,desc";

            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("course_id")));
            Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
            Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

            List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
            Page<Course> pageCourses = new PageImpl<>(expectedCourses, pageable, 5);

            when(courseServiceMock.findAll(pageable)).thenReturn(pageCourses);

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
        @DisplayName("when POST request with parameter name then should call " +
            "courseService.add once")
        void postRequestCreateCourse() throws Exception {
            mockMvc.perform(post(URI_COURSES)
                    .param("name", NAME_FIRST_COURSE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            verify(courseServiceMock).create(courseCaptor.capture());
            Course expectedCourse = courseCaptor.getValue();
            assertThat(expectedCourse).hasId(null);
            assertThat(expectedCourse).hasName(NAME_FIRST_COURSE);
        }

        @Test
        @DisplayName("when POST request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            mockMvc.perform(post(URI_COURSES)
                    .param("name", FAIL_NAME_FIRST_COURSE))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }

    @Nested
    @DisplayName("test 'getCourse' method")
    class GetCourseTest {

        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should return " +
            "JSON with expected course")
        void getRequestWithId() throws Exception {
            int courseId = anyInt();
            Course expectedCourse = new Course(courseId, NAME_FIRST_COURSE);

            when(courseServiceMock.findById(courseId)).thenReturn(expectedCourse);

            mockMvc.perform(get(URI_COURSES_ID, courseId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().string(containsString(String.valueOf(courseId))),
                    content().string(containsString(NAME_FIRST_COURSE))
                );
        }
    }

    @Nested
    @DisplayName("test 'updateCourse' method")
    class UpdateCourseTest {

        @Test
        @DisplayName("when PUT request with parameters id and name then call " +
            "courseService.update once")
        void putRequestWithIdAndName() throws Exception {
            int courseId = anyInt();
            mockMvc.perform(put(URI_COURSES_ID, courseId)
                .param("name", NAME_FIRST_COURSE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            Course updatedCourse = new Course(courseId, NAME_FIRST_COURSE);
            verify(courseServiceMock).create(updatedCourse);
        }

        @Test
        @DisplayName("when PUT request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPutRequestWithFailParameter() throws Exception {
            mockMvc.perform(put(URI_COURSES_ID, COURSE_ID1)
                    .param("name", FAIL_NAME_FIRST_COURSE))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }

    @Nested
    @DisplayName("test 'deleteCourse' method")
    class DeleteCourseTest {

        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then call " +
            "courseService.delete once and redirect")
        void deleteRequestWithId() throws Exception {
            int courseId = anyInt();
            mockMvc.perform(delete(URI_COURSES_ID, courseId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(courseServiceMock).delete(courseId);
        }
    }


}