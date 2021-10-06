package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.sun.deploy.uitoolkit.impl.awt.AWTClientPrintHelper.print;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "Faculty1 name";
    public static final String NAME_SECOND_FACULTY = "Faculty2 name";
    public static final String NAME_FIRST_DEPARTMENT = "First department";
    public static final String NAME_SECOND_DEPARTMENT = "Second department";
    public static final String URI_DEPARTMENTS = "/departments";

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private TeacherDtoMapper teacherDtoMapper;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(departmentController)
            .build();
    }

    private List<Faculty> createExpectedFaculties() {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        return Arrays.asList(faculty1, faculty2);
    }

    @Nested
    @DisplayName("test 'showDepartments' method")
    class ShowDepartments {

        @Test
        @DisplayName("when GET request without parameters")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> expectedFaculties = createExpectedFaculties();
            Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT,
                expectedFaculties.get(0));
            Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT,
                expectedFaculties.get(1));
            List<Department> allDepartments = Arrays.asList(department1,
                department2);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
            when(departmentServiceMock.getAll()).thenReturn(allDepartments);

            mockMvc.perform(get(URI_DEPARTMENTS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("department"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("departments", allDepartments),
                    model().attribute("facultySelected", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with parameter facultyId")
        void getRequestWithParameterFacultyId() throws Exception {
            List<Faculty> expectedFaculties = createExpectedFaculties();
            Faculty faculty1 = expectedFaculties.get(0);
            Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT,
                faculty1);
            List<Department> departmentsFaculty1 = Collections.singletonList(department1);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
            when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departmentsFaculty1);

            mockMvc.perform(get(URI_DEPARTMENTS)
                    .param("facultyId", String.valueOf(ID1)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("department"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("departments", departmentsFaculty1),
                    model().attribute("facultySelected", faculty1)
                );
        }

    }

    @Nested
    @DisplayName("test 'createDepartment' method")
    class CreateDepartmentTest {

        @Test
        @DisplayName("when POST request with parameters name and faculty.id then " +
            "should call departmentService.add once and redirect")
        void postRequestWithParametersNameAndFacultyId() throws Exception {
            String departmentName = NAME_FIRST_DEPARTMENT;
            String facultyId = String.valueOf(ID1);

            mockMvc.perform(post(URI_DEPARTMENTS)
                    .param("name", departmentName)
                    .param("faculty.Id", facultyId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            ArgumentCaptor<Department> requestCaptor =
                ArgumentCaptor.forClass(Department.class);
            verify(departmentServiceMock).add(requestCaptor.capture());

            Department expectedCreatedDepartment = requestCaptor.getValue();
            assertThat(expectedCreatedDepartment.getName(), is(NAME_FIRST_DEPARTMENT));
            assertThat(expectedCreatedDepartment.getFaculty().getId(), is(ID1));
        }
    }

    @Nested
    @DisplayName("test 'getDepartment' method")
    class GetDepartmentTest {

        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should return " +
            "JSON with expected department")
        void getRequestWithIdParameter() throws Exception {
            int departmentId = anyInt();
            Faculty faculty = new Faculty(ID1, NAME_FIRST_FACULTY);
            Department expectedDepartment = new Department(departmentId,
                NAME_FIRST_DEPARTMENT, faculty);

            when(departmentServiceMock.getById(departmentId)).thenReturn(expectedDepartment);

            mockMvc.perform(get("/departments/{id}", departmentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().string(containsString(String.valueOf(departmentId))),
                    content().string(containsString(NAME_FIRST_DEPARTMENT))
                );
        }
    }
}
