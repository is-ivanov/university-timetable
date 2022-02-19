package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.DepartmentAssert;
import ua.com.foxminded.university.domain.entity.FacultyAssert;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_DEPARTMENTS;

@WebMvcTest(DepartmentRestController.class)
@Import(TestMapperConfig.class)
class DepartmentRestControllerTest {

    private static final String FAIL_DEPARTMENT_NAME = "fail department name";
    private static final String API_DEPARTMENTS_ID = "/api/departments/{id}";
    private static final String API_DEPARTMENTS_ID_TEACHERS = "/api/departments/{id}/teachers";

    @Captor
    ArgumentCaptor<Department> departmentCaptor;

    @Captor
    ArgumentCaptor<Integer> idCaptor;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentServiceMock;

    @MockBean
    private TeacherService teacherServiceMock;

    @Nested
    @DisplayName("test 'getDepartments' method")
    class GetDepartmentsTest {
        @Test
        @DisplayName("when GET request without parameters and service return list " +
            "departments then method return CollectionModel<DepartmentDto> with expected links")
        void whenServiceReturnListDepartments_MethodReturnCollectionModel() throws Exception {

            List<Department> testDepartments = createTestDepartments();

            when(departmentServiceMock.findAll()).thenReturn(testDepartments);

            mockMvc.perform(get(API_DEPARTMENTS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.departments", hasSize(2)),
                    jsonPath("$._embedded.departments[0].id",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.departments[0].name",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.departments[0]._links.self.href",
                        is(DEPARTMENT1_SELF_LINK)),
                    jsonPath("$._embedded.departments[1].id",
                        is(DEPARTMENT_ID2)),
                    jsonPath("$._embedded.departments[1].name",
                        is(NAME_SECOND_DEPARTMENT)),
                    jsonPath("$._embedded.departments[1]._links.self.href",
                        is(DEPARTMENT2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(DEPARTMENTS_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'getDepartmentsPaginatedAndSorted' method")
    class GetDepartmentsPaginatedAndSortedTest {
        @Test
        @DisplayName("when GET request with parameters page, size and sort then " +
            "should use this parameters and return HAL+JSON PagedModel with expected links")
        void whenServiceReturnPageCourseThenMethodReturnPagedModel() throws Exception {
            int page = 3;
            int size = 2;
            String sort = "id,desc";
            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("id")));
            Page<Department> departmentPage = createTestPageDepartment(pageable);

            when(departmentServiceMock.findAll(pageable)).thenReturn(departmentPage);
            String parameters = "?page=" + page + "&size=" + size + "&sort=" + sort;
            String sefLink = DEPARTMENTS_LINK + parameters;

            mockMvc.perform(get(API_DEPARTMENTS + parameters))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.departments", hasSize(2)),
                    jsonPath("$._embedded.departments[0].id",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.departments[0].name",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.departments[0]._links.self.href",
                        is(DEPARTMENT1_SELF_LINK)),
                    jsonPath("$._embedded.departments[1].id",
                        is(DEPARTMENT_ID2)),
                    jsonPath("$._embedded.departments[1].name",
                        is(NAME_SECOND_DEPARTMENT)),
                    jsonPath("$._embedded.departments[1]._links.self.href",
                        is(DEPARTMENT2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(sefLink)),
                    jsonPath("$.page.size", is(size)),
                    jsonPath("$.page.number", is(page))
                );
        }
    }

    @Nested
    @DisplayName("test 'getDepartment' method")
    class GetDepartmentTest {
        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should " +
            "return JSON with expected department")
        void whenGetRequestWithPathParameterIdThenShouldReturnJsonWithExpectedDepartment()
            throws Exception {
            Department testDepartment = createTestDepartment(FACULTY_ID1);

            when(departmentServiceMock.findById(DEPARTMENT_ID1))
                .thenReturn(testDepartment);

            mockMvc.perform(get(MappingConstants.API_DEPARTMENTS_ID, DEPARTMENT_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(DEPARTMENT_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY)),
                    jsonPath("$._links.self.href", is(DEPARTMENT1_SELF_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createDepartment' method")
    class CreateDepartmentTest {

        @Test
        @DisplayName("when POST request with parameters name and faculty.id then " +
            "should call departmentService.add once")
        void postRequestWithParametersNameAndFacultyId() throws Exception {
            String bodyRequest = "{\"name\": \"" + NAME_FIRST_DEPARTMENT +
                "\", \"facultyId\": " + FACULTY_ID1 + "}";
            Department departmentAfterSaving = createTestDepartment(FACULTY_ID1);

            when(departmentServiceMock.create(any()))
                .thenReturn(departmentAfterSaving);

            mockMvc.perform(post(API_DEPARTMENTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(DEPARTMENT_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY))
                );

            verify(departmentServiceMock).create(departmentCaptor.capture());

            Department departmentForSaving = departmentCaptor.getValue();
            DepartmentAssert.assertThat(departmentForSaving)
                .hasId(null)
                .hasName(NAME_FIRST_DEPARTMENT);
            FacultyAssert.assertThat(departmentForSaving.getFaculty())
                .hasId(FACULTY_ID1)
                .hasName(null);
        }

        @Test
        @DisplayName("when POST request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            String bodyRequest = "{\"name\": \"" + FAIL_DEPARTMENT_NAME +
                "\", \"facultyId\": " + FACULTY_ID1 + "}";

            mockMvc.perform(post(API_DEPARTMENTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }

    @Nested
    @DisplayName("test 'updateDepartment' method")
    class UpdateDepartmentTest {

        @Test
        @DisplayName("when PUT request with parameters 'id', 'name' and 'faculty.Id' " +
            "then should call departmentService.update once")
        void putRequestWithIdNameAndFacultyId() throws Exception {
            String newDepartmentName = "New Department Name";

            String jsonBodyRequest = "{\"name\": \"" + newDepartmentName +
                "\", \"facultyId\": " + FACULTY_ID1 + ", " +
                "\"id\":" + DEPARTMENT_ID1 + "}";
            Department updatedDepartment = createTestDepartment(FACULTY_ID1);
            updatedDepartment.setName(newDepartmentName);

            when(departmentServiceMock.update(anyInt(), any(Department.class)))
                .thenReturn(updatedDepartment);

            mockMvc.perform(put(API_DEPARTMENTS_ID, DEPARTMENT_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(DEPARTMENT_ID1)),
                    jsonPath("$.name", is(newDepartmentName)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY))
                );

            verify(departmentServiceMock).update(idCaptor.capture(), departmentCaptor.capture());

            assertThat(idCaptor.getValue()).isEqualTo(DEPARTMENT_ID1);
            Department expectedUpdatedDepartment = departmentCaptor.getValue();
            DepartmentAssert.assertThat(expectedUpdatedDepartment)
                .hasName(newDepartmentName)
                .hasId(DEPARTMENT_ID1);
            FacultyAssert.assertThat(expectedUpdatedDepartment.getFaculty())
                .hasId(FACULTY_ID1)
                .hasName(null);
        }

        @Test
        @DisplayName("when PUT request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPutRequestWithFailParameter() throws Exception {
            String jsonBodyRequest = "{\"name\": \"" + FAIL_DEPARTMENT_NAME +
                "\", \"facultyId\": " + FACULTY_ID1 + ", " +
                "\"id\":" + DEPARTMENT_ID1 + "}";

            mockMvc.perform(put(API_DEPARTMENTS_ID, DEPARTMENT_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }

    @Nested
    @DisplayName("test 'deleteDepartment' method")
    class DeleteDepartmentTest {

        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then call " +
            "departmentService.delete once")
        void deleteRequestWithId() throws Exception {
            mockMvc.perform(delete(API_DEPARTMENTS_ID, DEPARTMENT_ID1))
                .andDo(print())
                .andExpect(status().isNoContent());

            verify(departmentServiceMock).delete(DEPARTMENT_ID1);
        }

        @Test
        @DisplayName("when DELETE request with @PathParameter id < 1 then return" +
            " status BAD_REQUEST and never call service.delete()")
        void whenDeleteRequestWithPathParameterIllegalIdThenReturnStatusBadRequest()
            throws Exception {
            int illegalId = -9;

            mockMvc.perform(delete(API_DEPARTMENTS_ID, illegalId))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message", is("illegal ID"))
                );

            verify(departmentServiceMock, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("test 'getTeachersByDepartment' method")
    class GetTeachersByDepartmentTest {

        @Test
        @DisplayName("when GET request with parameters 'id' then should return " +
            "expected list teacherDTO")
        void GetTeachersByDepartment() throws Exception {
            int departmentId = DEPARTMENT_ID1;
            List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

            when(teacherServiceMock.getAllByDepartment(departmentId)).thenReturn(teachers);

            mockMvc.perform(get(API_DEPARTMENTS_ID_TEACHERS, departmentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.teachers", hasSize(2)),
                    jsonPath("$._embedded.teachers[0].id", is(TEACHER_ID1)),
                    jsonPath("$._embedded.teachers[0].firstName",
                        is(NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].lastName",
                        is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].departmentId",
                        is(departmentId)),
                    jsonPath("$._embedded.teachers[0].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.teachers[1].id", is(TEACHER_ID2)),
                    jsonPath("$._embedded.teachers[1].firstName",
                        is(NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].lastName",
                        is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].departmentId",
                        is(departmentId)),
                    jsonPath("$._embedded.teachers[1].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._links.self.href",
                        is("http://localhost/api/departments/8/teachers"))
                );
        }
    }

}