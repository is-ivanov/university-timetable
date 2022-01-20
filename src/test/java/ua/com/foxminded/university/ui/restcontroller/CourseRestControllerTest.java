package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.CourseDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.mapper.CourseDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.restcontroller.link.CourseDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.university.TestObjects.createTestCourseDtos;
import static ua.com.foxminded.university.TestObjects.createTestCourses;

@ExtendWith(MockitoExtension.class)
class CourseRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseServiceMock;
    @Mock
    private CourseDtoMapper mapperMock;
    @Mock
    private CourseDtoAssembler assemblerMock;
    @Mock
    private PagedResourcesAssembler<Course> pagedAssemblerMock;

    @InjectMocks
    private CourseRestController courseRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(courseRestController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

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
                CollectionModel.of(createTestCourseDtos());

            when(courseServiceMock.findAll()).thenReturn(testCourses);
            when(assemblerMock.toCollectionModel(testCourses))
                .thenReturn(testCourseDtos);


            mockMvc.perform(MockMvcRequestBuilders.get(MappingConstants.API_COURSES))
                .andDo(print())
                .andExpectAll(
                    status().isOk()
                );
        }
    }
}