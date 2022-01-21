package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

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

    @Autowired
    private CourseRestController courseRestController;

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
            when(assemblerMock.toCollectionModel(testCourses))
                .thenReturn(testCourseDtos);

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
}