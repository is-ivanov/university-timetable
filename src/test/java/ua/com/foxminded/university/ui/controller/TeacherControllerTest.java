package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
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

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    private static final int ID1 = 1;
    private static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "faculty1";
    public static final String NAME_SECOND_FACULTY = "faculty2";
    public static final String NAME_FIRST_DEPARTMENT = "dep1";
    public static final String NAME_SECOND_DEPARTMENT = "dep2";
    public static final String TEACHER_NAME = "teacher name";
    public static final String URL_ALL_PARAMETERS = "/teacher?facultyId=1&departmentId=2&isShowInactiveTeachers=on";
    public static final String URL_FACULTY_PARAMETER = "/teacher?facultyId=1";
    public static final String URL_FACULTY_ID_0 = "/teacher/faculty?facultyId=0";
    public static final String URL_FACULTY_ID_1 = "/teacher/faculty?facultyId=1";

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Nested
    @DisplayName("test showTeachers")
    class TestShowTeachers {

        @Test
        @DisplayName("without parameters")
        void testWithoutParameters() throws Exception {
            Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
            Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
            List<Faculty> faculties = Arrays.asList(faculty1, faculty2);

            Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT,
                faculty1);
            Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT,
                faculty2);
            List<Department> departments = Arrays.asList(department1, department2);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.getAll()).thenReturn(departments);

            mockMvc.perform(get("/teacher"))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "teachers"),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("facultyIdSelect", is(nullValue())),
                    model().attribute("groupIdSelect", is(nullValue()))
                ));
        }

        @Test
        @DisplayName("with all parameters")
        void testWithAllParameters() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            teacher1.setFirstName(TEACHER_NAME);
            List<Teacher> teachers = Collections.singletonList(teacher1);
            Department department1 = new Department();
            department1.setId(ID2);
            List<Department> departments = Collections.singletonList(department1);

            when(teacherServiceMock.getAllByDepartment(ID2)).thenReturn(teachers);
            when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departments);

            mockMvc.perform(get(URL_ALL_PARAMETERS))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("teachers", teachers),
                    model().attribute("departments", departments)
                ));
        }

        @Test
        @DisplayName("with facultyId and without departmentId")
        void testWithFacultyIdAndWithoutDepartmentId() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            teacher1.setFirstName(TEACHER_NAME);
            List<Teacher> teachers = Collections.singletonList(teacher1);

            when(teacherServiceMock.getAllByFaculty(ID1)).thenReturn(teachers);

            mockMvc.perform(get(URL_FACULTY_PARAMETER))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("teachers", teachers)
                ));
        }
    }

    @Nested
    @DisplayName("test getDepartments")
    class TestGetDepartments {

        @Test
        @DisplayName("with parameter facultyId = 0")
        void withParameterFacultyId0() throws Exception {
            Department department1 = new Department();
            department1.setId(ID1);
            department1.setName("department1");
            Department department2 = new Department();
            department2.setId(ID2);
            department2.setName("department2");
            List<Department> departments = Arrays.asList(department1, department2);

            when(departmentServiceMock.getAll()).thenReturn(departments);

            mockMvc.perform(get(URL_FACULTY_ID_0))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()", is(2)),
                    jsonPath("$.[0].name", is("department1")),
                    jsonPath("$.[1].name", is("department2"))
                ));
        }

        @Test
        @DisplayName("with parameter facultyId = 1")
        void withParameterFacultyId1() throws Exception {
            Department department1 = new Department();
            department1.setId(ID1);
            department1.setName("department1");
            List<Department> departments = Collections.singletonList(department1);

            when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departments);

            mockMvc.perform(get(URL_FACULTY_ID_1))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()",is(1)),
                    jsonPath("$.[0].name", is("department1"))
                ));
        }
    }
}
