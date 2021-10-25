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
import org.springframework.web.servlet.view.InternalResourceViewResolver;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    public static final String URL_FACULTY_PARAMETER = "/teacher?facultyId=1";
    public static final String URL_FACULTY_ID_0 = "/teacher/faculty?facultyId=0";
    public static final String URL_FACULTY_ID_1 = "/teacher/faculty?facultyId=1";
    public static final String URI_TEACHERS = "/teachers";

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
        void testWithAllParameters() throws Exception {
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
//
//        @Test
//        @DisplayName("with facultyId and without departmentId")
//        void testWithFacultyIdAndWithoutDepartmentId() throws Exception {
//            Teacher teacher1 = new Teacher();
//            teacher1.setId(ID1);
//            teacher1.setFirstName(TEACHER_NAME);
//            List<Teacher> teachers = Collections.singletonList(teacher1);
//
//            when(teacherServiceMock.getAllByFaculty(ID1)).thenReturn(teachers);
//
//            mockMvc.perform(get(URL_FACULTY_PARAMETER))
//                .andDo(print())
//                .andExpect(matchAll(
//                    status().isOk(),
//                    view().name("teacher"),
//                    model().attribute("teachers", teachers)
//                ));
//        }
    }

//    @Nested
//    @DisplayName("test getDepartments")
//    class TestGetDepartments {
//
//        @Test
//        @DisplayName("with parameter facultyId = 0")
//        void withParameterFacultyId0() throws Exception {
//            Department department1 = new Department();
//            department1.setId(ID1);
//            department1.setName("department1");
//            Department department2 = new Department();
//            department2.setId(ID2);
//            department2.setName("department2");
//            List<Department> departments = Arrays.asList(department1, department2);
//
//            when(departmentServiceMock.getAll()).thenReturn(departments);
//
//            mockMvc.perform(get(URL_FACULTY_ID_0))
//                .andDo(print())
//                .andExpect(matchAll(
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    status().isOk(),
//                    jsonPath("$.length()", is(2)),
//                    jsonPath("$.[0].name", is("department1")),
//                    jsonPath("$.[1].name", is("department2"))
//                ));
//        }
//
//        @Test
//        @DisplayName("with parameter facultyId = 1")
//        void withParameterFacultyId1() throws Exception {
//            Department department1 = new Department();
//            department1.setId(ID1);
//            department1.setName("department1");
//            List<Department> departments = Collections.singletonList(department1);
//
//            when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departments);
//
//            mockMvc.perform(get(URL_FACULTY_ID_1))
//                .andDo(print())
//                .andExpect(matchAll(
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    status().isOk(),
//                    jsonPath("$.length()",is(1)),
//                    jsonPath("$.[0].name", is("department1"))
//                ));
//        }
//    }
}
