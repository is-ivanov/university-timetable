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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.createTestFaculties;

@ExtendWith(MockitoExtension.class)
class FacultyRestControllerTest {

    public static final String URI_FACULTIES_ID = "/api/faculties/{id}";
    public static final String URI_FACULTIES_ID_GROUPS = "/api/faculties/{id}/groups";
    public static final String URI_FACULTIES_ID_DEPARTMENTS = "/api/faculties/{id}/departments";
    public static final String URI_FACULTIES_ID_TEACHERS = "/api/faculties/{id}/teachers";
    public static final String URI_FACULTIES_ID_GROUPS_FREE = "/api/faculties/{id}/groups/free";
    public static final String FACULTY_NAME = "name";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";


    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyServiceMock;

    @InjectMocks
    private FacultyRestController facultyRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(facultyRestController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("test 'getFaculties' method")
    class GetFacultiesTest {
        @Test
        @DisplayName("when GET request without parameters then should return " +
            "all faculties with status OK")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();

            when(facultyServiceMock.getAll()).thenReturn(faculties);

            mockMvc.perform(get("/api/faculties"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2))
                );
        }
    }
}