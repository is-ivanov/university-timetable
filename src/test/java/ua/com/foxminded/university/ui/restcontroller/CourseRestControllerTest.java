package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.CourseAssert;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_COURSES;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_COURSES_ID;

@WebMvcTest(CourseRestController.class)
@Import(TestMapperConfig.class)
class CourseRestControllerTest {

    public static final String FAIL_COURSE_NAME = "fail course name";
    private static final String URI_COURSES_ID = "/api/courses/{id}";

    @Captor
    ArgumentCaptor<Course> courseCaptor;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseServiceMock;

    @Nested
    @DisplayName("test 'getCourses' method")
    class GetCoursesTest {
        @Test
        @DisplayName("when GET request without parameters and service return list " +
            "courses then method return CollectionModel<CourseDto> with expected links")
        void whenServiceReturnListCourses_MethodReturnCollectionModel() throws Exception {

            List<Course> testCourses = createTestCourses();

            when(courseServiceMock.findAll()).thenReturn(testCourses);

            mockMvc.perform(get(API_COURSES))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.courses", hasSize(2)),
                    jsonPath("$._embedded.courses[0].id", is(COURSE_ID1)),
                    jsonPath("$._embedded.courses[0].name", is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.courses[0]._links.self.href", is(COURSE1_SELF_LINK)),
                    jsonPath("$._embedded.courses[1].id", is(COURSE_ID2)),
                    jsonPath("$._embedded.courses[1].name", is(NAME_SECOND_COURSE)),
                    jsonPath("$._embedded.courses[1]._links.self.href", is(COURSE2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(COURSES_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'getCoursesPaginatedAndSorted' method")
    class GetCoursesPaginatedAndSortedTest {
        @Test
        @DisplayName("when GET request with parameters page, size and sort then " +
            "should use this parameters and return HAL+JSON PagedModel with expected links")
        void whenServiceReturnPageCourseThenMethodReturnPagedModel() throws Exception {
            int page = 3;
            int size = 2;
            String sort = "id,desc";
            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("id")));
            Page<Course> coursePage = createTestPageCourse(pageable);

            when(courseServiceMock.findAll(pageable)).thenReturn(coursePage);
            String parameters = "?page=" + page + "&size=" + size + "&sort=" + sort;
            String sefLink = COURSES_LINK + parameters;

            mockMvc.perform(get(API_COURSES + parameters))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.courses", hasSize(2)),
                    jsonPath("$._embedded.courses[0].id", is(COURSE_ID1)),
                    jsonPath("$._embedded.courses[0].name", is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.courses[0]._links.self.href", is(COURSE1_SELF_LINK)),
                    jsonPath("$._embedded.courses[1].id", is(COURSE_ID2)),
                    jsonPath("$._embedded.courses[1].name", is(NAME_SECOND_COURSE)),
                    jsonPath("$._embedded.courses[1]._links.self.href", is(COURSE2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(sefLink)),
                    jsonPath("$.page.size", is(size)),
                    jsonPath("$.page.number", is(page))
                );
        }
    }

    @Nested
    @DisplayName("test 'getCourse' method")
    class GetCourseTest {

        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should return " +
            "JSON with expected course")
        void getRequestWithId() throws Exception {
            Course expectedCourse = createTestCourse();

            when(courseServiceMock.findById(COURSE_ID1)).thenReturn(expectedCourse);

            mockMvc.perform(get(API_COURSES_ID, COURSE_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(COURSE_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_COURSE)),
                    jsonPath("$._links.self.href", is(COURSE1_SELF_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createCourse' method")
    class CreateCourseTest {

        @Test
        @DisplayName("when POST request with parameter 'name' then should call " +
            "courseService.add once")
        void postRequestCreateCourse() throws Exception {

            when(courseServiceMock.create(any()))
                .thenReturn(new Course(COURSE_ID1, NAME_FIRST_COURSE));

            mockMvc.perform(post(API_COURSES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"" + NAME_FIRST_COURSE + "\"}"))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(COURSE_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_COURSE)),
                    jsonPath("$._links.self.href", is(COURSE1_SELF_LINK))
                );

            verify(courseServiceMock).create(courseCaptor.capture());
            Course expectedCourse = courseCaptor.getValue();
            CourseAssert.assertThat(expectedCourse)
                .hasId(null)
                .hasName(NAME_FIRST_COURSE);
        }

        @Test
        @DisplayName("when POST request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            mockMvc.perform(post(API_COURSES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"" + FAIL_COURSE_NAME + "\"}"))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }


    @Nested
    @DisplayName("test 'updateCourse' method")
    class UpdateCourseTest {

        @Test
        @DisplayName("when PUT request with parameters id and name then call " +
            "courseService.update once")
        void putRequestWithIdAndName() throws Exception {
            String newName = "New Course Name";
            Course existingCourse = new Course(COURSE_ID1, NAME_FIRST_COURSE);
            Course updatedCourse = new Course(COURSE_ID1, newName);

            String jsonBodyRequest = "{\"name\": \"" + newName + "\",   " +
                "\"id\":" + COURSE_ID1 + "}";

            when(courseServiceMock.update(COURSE_ID1, existingCourse))
                .thenReturn(updatedCourse);

            mockMvc.perform(put(URI_COURSES_ID, COURSE_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(COURSE_ID1)),
                    jsonPath("$.name", is(newName)),
                    jsonPath("$._links.self.href", is(COURSE1_SELF_LINK))
                );
        }

        @Test
        @DisplayName("when PUT request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPutRequestWithFailParameter() throws Exception {

            String jsonBodyRequest = "{\"name\": \"" + FAIL_COURSE_NAME + "\",   " +
                "\"id\":" + COURSE_ID1 + "}";

            mockMvc.perform(put(URI_COURSES_ID, COURSE_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER))
                );
        }
    }

    @Nested
    @DisplayName("test 'deleteCourse' method")
    class DeleteCourseTest {
        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then call" +
            " once service.delete(id)")
        void whenDeleteRequestWithPathParameterIdThenCallServiceDelete() throws Exception {

            mockMvc.perform(delete(URI_COURSES_ID, COURSE_ID1))
                .andDo(print())
                .andExpect(status().isNoContent());

            verify(courseServiceMock).delete(COURSE_ID1);
        }

        @Test
        @DisplayName("when DELETE request with @PathParameter id < 1 then return" +
            " status BAD_REQUEST and never call service.delete()")
        void whenDeleteRequestWithPathParameterId1ThenReturnStatusBadRequest()
            throws Exception {
            int illegalId = -5;

            mockMvc.perform(delete(URI_COURSES_ID, illegalId))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message", is("illegal ID"))
                );

            verify(courseServiceMock, never()).delete(COURSE_ID1);
        }
    }

}