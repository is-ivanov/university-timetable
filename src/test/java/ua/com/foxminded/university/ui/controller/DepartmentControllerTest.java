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
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "Faculty1 name";
    public static final String NAME_FIRST_DEPARTMENT = "First department";
    public static final String NAME_SECOND_DEPARTMENT = "Second department";
    public static final String URI_DEPARTMENTS = "/departments";
    public static final String URI_DEPARTMENTS_ID = "/departments/{id}";
    public static final String FIRST_NAME_FIRST_STUDENT = "John";
    public static final String LAST_NAME_FIRST_STUDENT = "Johnson";
    public static final String FIRST_NAME_SECOND_STUDENT = "Anna";
    public static final String LAST_NAME_SECOND_STUDENT = "Peterson";

    private MockMvc mockMvc;

    @Captor
    ArgumentCaptor<Department> departmentCaptor;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private TeacherDtoMapper teacherDtoMapperMock;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Nested
    @DisplayName("test 'showDepartments' method")
    class ShowDepartments {

        @Test
        @DisplayName("when GET request without parameters")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> expectedFaculties = createTestFaculties();
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
            List<Faculty> testFaculties = createTestFaculties();
            Faculty faculty1 = testFaculties.get(0);
            List<Department> departmentsFromFacultyId1 = createTestDepartments(FACULTY_ID1);

            when(facultyServiceMock.getAllSortedByNameAsc())
                .thenReturn(testFaculties);
            when(departmentServiceMock.getAllByFaculty(FACULTY_ID1))
                .thenReturn(departmentsFromFacultyId1);

            mockMvc.perform(get(URI_DEPARTMENTS)
                    .param("facultyId", String.valueOf(FACULTY_ID1)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("department"),
                    model().attribute("faculties", testFaculties),
                    model().attribute("departments", departmentsFromFacultyId1),
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
            String facultyId = String.valueOf(ID1);

            mockMvc.perform(post(URI_DEPARTMENTS)
                    .param("name", NAME_FIRST_DEPARTMENT)
                    .param("faculty.Id", facultyId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(departmentServiceMock).add(departmentCaptor.capture());

            Department expectedCreatedDepartment = departmentCaptor.getValue();
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

            mockMvc.perform(get(URI_DEPARTMENTS_ID, departmentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    content().string(containsString(String.valueOf(departmentId))),
                    content().string(containsString(NAME_FIRST_DEPARTMENT))
                );
        }
    }

    @Nested
    @DisplayName("test 'updateDepartment' method")
    class UpdateDepartmentTest {

        @Test
        @DisplayName("when PUT request with parameters 'id', 'name' and 'faculty.Id' " +
            "then should call departmentService.update once an redirect")
        void putRequestWithIdNameAndFacultyId() throws Exception {
            int departmentId = anyInt();

            mockMvc.perform(put(URI_DEPARTMENTS_ID, departmentId)
                    .param("name", NAME_FIRST_DEPARTMENT)
                    .param("faculty.Id", String.valueOf(ID1)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(departmentServiceMock).update(departmentCaptor.capture());

            Department expectedCreatedDepartment = departmentCaptor.getValue();
            assertThat(expectedCreatedDepartment.getName(), is(NAME_FIRST_DEPARTMENT));
            assertThat(expectedCreatedDepartment.getFaculty().getId(), is(ID1));
        }
    }

    @Nested
    @DisplayName("test 'deleteDepartment' method")
    class DeleteDepartmentTest {

        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then should call " +
            "departmentService.delete once and redirect")
        void deleteRequestWithId() throws Exception {
            int departmentId = anyInt();
            mockMvc.perform(delete(URI_DEPARTMENTS_ID, departmentId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(departmentServiceMock, times(1)).delete(departmentId);
        }
    }

    @Nested
    @DisplayName("test 'getTeachersByDepartment' method")
    class getTeachersByDepartmentTest {

        @Test
        @DisplayName("when GET request with parameters 'id' then should return " +
            "expected list teacherDTO")
        void GetTeachersByDepartment() throws Exception {
            int departmentId = anyInt();

            List<Teacher> teachers = new ArrayList<>();
            TeacherDto teacher1 = TeacherDto.builder()
                .id(ID1)
                .firstName(FIRST_NAME_FIRST_STUDENT)
                .lastName(LAST_NAME_FIRST_STUDENT)
                .departmentId(departmentId)
                .departmentName(NAME_FIRST_DEPARTMENT)
                .build();
            TeacherDto teacher2 = TeacherDto.builder()
                .id(ID2)
                .firstName(FIRST_NAME_SECOND_STUDENT)
                .lastName(LAST_NAME_SECOND_STUDENT)
                .departmentId(departmentId)
                .departmentName(NAME_FIRST_DEPARTMENT)
                .build();
            List<TeacherDto> teacherDtos = Arrays.asList(teacher1, teacher2);

            when(teacherServiceMock.getAllByDepartment(departmentId)).thenReturn(teachers);
            when(teacherDtoMapperMock.teachersToTeacherDtos(anyList())).thenReturn(teacherDtos);

            mockMvc.perform(get("/departments/{id}/teachers", departmentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2)),
                    jsonPath("$[0].id", is(ID1)),
                    jsonPath("$[0].firstName", is(FIRST_NAME_FIRST_STUDENT)),
                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$[0].departmentId", is(departmentId)),
                    jsonPath("$[0].departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[1].id", is(ID2)),
                    jsonPath("$[1].firstName", is(FIRST_NAME_SECOND_STUDENT)),
                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_STUDENT)),
                    jsonPath("$[1].departmentId", is(departmentId)),
                    jsonPath("$[1].departmentName", is(NAME_FIRST_DEPARTMENT))
                );
            verify(teacherServiceMock).getAllByDepartment(departmentId);
        }
    }
}
