package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

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
class DepartmentControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "Faculty1 name";
    public static final String NAME_SECOND_FACULTY = "Faculty2 name";
    public static final String NAME_FIRST_DEPARTMENT = "First department";
    public static final String NAME_SECOND_DEPARTMENT = "Second department";

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test showDepartments without parameters")
    void testShowDepartmentsWithoutParameters() throws Exception {
        List<Faculty> expectedFaculties = createExpectedFaculties();
        Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT,
            expectedFaculties.get(0));
        Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT,
            expectedFaculties.get(1));
        List<Department> allDepartments = Arrays.asList(department1,
            department2);

        when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
        when(departmentServiceMock.getAll()).thenReturn(allDepartments);

        mockMvc.perform(get("/department"))
            .andDo(print())
            .andExpect(matchAll(
                status().isOk(),
                view().name("department"),
                model().attribute("faculties", expectedFaculties),
                model().attribute("departments", allDepartments),
                model().attribute("facultySelected", is(nullValue()))
            ));
    }

    @Test
    @DisplayName("Test showDepartments with parameter")
    void testShowDepartmentsWithParameter() throws Exception {
        List<Faculty> expectedFaculties = createExpectedFaculties();
        Faculty faculty1 = expectedFaculties.get(0);
        Department department1 = new Department(ID1, "First department name",
            faculty1);
        List<Department> departmentsFaculty1 = Collections.singletonList(department1);

        when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
        when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departmentsFaculty1);

        mockMvc.perform(get("/department?facultyId=1"))
            .andDo(print())
            .andExpect(matchAll(
                status().isOk(),
                view().name("department"),
                model().attribute("faculties", expectedFaculties),
                model().attribute("departments", departmentsFaculty1),
                model().attribute("facultySelected", faculty1)
            ));
    }

    private List<Faculty> createExpectedFaculties() {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        return Arrays.asList(faculty1, faculty2);
    }
}
