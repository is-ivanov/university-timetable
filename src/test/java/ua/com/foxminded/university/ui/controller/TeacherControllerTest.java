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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    public static final String URI_TEACHERS = "/teachers";
    public static final String URL_FACULTY_PARAMETER = "/teacher?facultyId=1";
    public static final String URL_FACULTY_ID_0 = "/teacher/faculty?facultyId=0";
    public static final String URL_FACULTY_ID_1 = "/teacher/faculty?facultyId=1";
    public static final String URI_TEACHERS_FREE = "/teachers/free";

    @Captor
    ArgumentCaptor<TeacherDto> teacherDtoCaptor;

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private TeacherDtoMapper teacherMapperMock;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private LessonDtoMapper lessonMapperMock;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(teacherController)
            .build();
    }

    @Nested
    @DisplayName("test 'showTeachers' method")
    class ShowTeachersTest {
        @Test
        @DisplayName("when GET request without parameters then should call expected " +
            "services and not load teachers in attribute of model")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();
            List<Department> departments = createTestDepartments();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.getAll()).thenReturn(departments);

            mockMvc.perform(get(URI_TEACHERS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "teachers"),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("facultyIdSelect", is(nullValue())),
                    model().attribute("groupIdSelect", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with all parameters")
        void getRequestWithAllParameters() throws Exception {
            int facultyId = 15;
            int departmentId = 47;

            List<Teacher> testTeachers = createTestTeachers(facultyId);
            List<Department> departments = createTestDepartments(facultyId);
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByDepartment(departmentId))
                .thenReturn(testTeachers);
            when(teacherMapperMock.teachersToTeacherDtos(testTeachers))
                .thenReturn(testTeacherDtos);
            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(departments);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("departmentId", String.valueOf(departmentId))
                    .param("isShowInactiveTeachers", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("teachers", testTeacherDtos),
                    model().attribute("departments", departments)
                );
        }

        @Test
        @DisplayName("when GET request with parameter facultyId and without departmentId")
        void getRequestWithFacultyIdAndWithoutDepartmentId() throws Exception {
            int facultyId = 34;

            List<Teacher> testTeachers = createTestTeachers(facultyId);
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testTeachers);
            when(teacherMapperMock.teachersToTeacherDtos(testTeachers))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("teachers", testTeacherDtos)
                );
            verify(departmentServiceMock, times(1))
                .getAllByFaculty(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'getFreeTeachers' method")
    class GetFreeTeachersTest {
        @Test
        @DisplayName("when GET request with parameters time_start and time_end " +
            "then should return JSON with list teacherDtos in body")
        void getRequestWithParametersShouldReturnJsonListTeacherDtos() throws Exception {
            List<Teacher> testTeachers = createTestTeachers(FACULTY_ID1);
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(FACULTY_ID1);

            when(teacherServiceMock.getFreeTeachersOnLessonTime(DATE_FROM, DATE_TO))
                .thenReturn(testTeachers);
            when(teacherMapperMock.teachersToTeacherDtos(testTeachers))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_TEACHERS_FREE)
                .param("time_start", TEXT_DATE_FROM)
                .param("time_end", TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2)),
                    jsonPath("$[0].id", is(TEACHER_ID1)),
                    jsonPath("$[0].firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$[0].patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$[0].departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$[0].departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[0].active", is(true)),
                    jsonPath("$[1].id", is(TEACHER_ID2)),
                    jsonPath("$[1].firstName", is(NAME_SECOND_TEACHER)),
                    jsonPath("$[1].patronymic", is(PATRONYMIC_SECOND_TEACHER)),
                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$[1].departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$[1].departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[1].active", is(true))
                );
        }
    }
}
