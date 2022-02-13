package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ua.com.foxminded.university.domain.dto.CourseDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.mapper.CourseDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.ui.restcontroller.link.CourseDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.domain.entity.Assertions.assertThat;

@WebMvcTest(CourseRestController.class)
class CourseRestControllerTest {

    public static final String APPLICATION_HAL_JSON = "application/hal+json";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseServiceMock;

    @MockBean
    private CourseDtoMapper mapperMock;

    @MockBean
    private CourseDtoAssembler assemblerMock;

    @MockBean
    private PagedResourcesAssembler<Course> pagedAssemblerMock;

//    @Autowired
//    private CourseRestController courseRestController;

    @Captor
    ArgumentCaptor<Course> courseCaptor;

//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders
//            .standaloneSetup(courseRestController)
//            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
//            .setControllerAdvice(new GlobalExceptionHandler())
//            .build();
//    }

    @Nested
    @DisplayName("test 'getCourses' method")
    class GetCoursesTest {
        @Test
        @DisplayName("when service return list faculties then method return " +
            "CollectionModel<FacultyDtos> with expected links")
        void whenServiceReturnListFaculties_thenMethodReturnCollectionModel()
            throws Exception {

            List<Course> testCourses = createTestCourses();
            CollectionModel<CourseDto> testCourseDtos =
                createTestModelCourseDtos();

            when(courseServiceMock.findAll()).thenReturn(testCourses);
//            when(assemblerMock.toCollectionModel(testCourses))
//                .thenReturn(testCourseDtos);

            mockMvc.perform(MockMvcRequestBuilders.get(MappingConstants.API_COURSES))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(APPLICATION_HAL_JSON),
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

//    @Nested
//    @DisplayName("test 'createCourse' method")
//    class CreateCourseTest {
//
//        @Test
//        @DisplayName("when POST request with parameter name then should call " +
//            "courseService.add once")
//        void postRequestCreateCourse() throws Exception {
//            mockMvc.perform(post(URI_COURSES)
//                    .param("name", NAME_FIRST_COURSE))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(courseServiceMock).create(courseCaptor.capture());
//            Course expectedCourse = courseCaptor.getValue();
//            assertThat(expectedCourse).hasId(null);
//            assertThat(expectedCourse).hasName(NAME_FIRST_COURSE);
//        }
//
//        @Test
//        @DisplayName("when POST request with fail parameter (name with first " +
//            "letter lower case) then should return error 400.BAD_REQUEST")
//        void whenPostRequestWithFailParameter() throws Exception {
//            mockMvc.perform(post(URI_COURSES)
//                    .param("name", FAIL_NAME_FIRST_COURSE))
//                .andDo(print())
//                .andExpectAll(
//                    status().isBadRequest(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$.violations[0].field", is("name")),
//                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
//        }
//    }

//    @Nested
//    @DisplayName("test 'getCourse' method")
//    class GetCourseTest {
//
//        @Test
//        @DisplayName("when GET request with @PathParameter 'id' then should return " +
//            "JSON with expected course")
//        void getRequestWithId() throws Exception {
//            int courseId = anyInt();
//            Course expectedCourse = new Course(courseId, NAME_FIRST_COURSE);
//
//            when(courseServiceMock.findById(courseId)).thenReturn(expectedCourse);
//
//            mockMvc.perform(get(URI_COURSES_ID, courseId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isOk(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    content().string(containsString(String.valueOf(courseId))),
//                    content().string(containsString(NAME_FIRST_COURSE))
//                );
//        }
//    }

//    @Nested
//    @DisplayName("test 'updateCourse' method")
//    class UpdateCourseTest {
//
//        @Test
//        @DisplayName("when PUT request with parameters id and name then call " +
//            "courseService.update once")
//        void putRequestWithIdAndName() throws Exception {
//            int courseId = anyInt();
//            mockMvc.perform(put(URI_COURSES_ID, courseId)
//                    .param("name", NAME_FIRST_COURSE))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            Course updatedCourse = new Course(courseId, NAME_FIRST_COURSE);
//            verify(courseServiceMock).create(updatedCourse);
//        }
//
//        @Test
//        @DisplayName("when PUT request with fail parameter (name with first " +
//            "letter lower case) then should return error 400.BAD_REQUEST")
//        void whenPutRequestWithFailParameter() throws Exception {
//            mockMvc.perform(put(URI_COURSES_ID, COURSE_ID1)
//                    .param("name", FAIL_NAME_FIRST_COURSE))
//                .andDo(print())
//                .andExpectAll(
//                    status().isBadRequest(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$.violations[0].field", is("name")),
//                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
//        }
//    }










}