package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_COURSES;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_DEPARTMENTS;

@WebMvcTest(DepartmentRestController.class)
@Import(TestMapperConfig.class)
class DepartmentRestControllerTest {

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

//    @Nested
//    @DisplayName("test 'createDepartment' method")
//    class CreateDepartmentTest {
//
//        @Test
//        @DisplayName("when POST request with parameters name and faculty.id then " +
//            "should call departmentService.add once")
//        void postRequestWithParametersNameAndFacultyId() throws Exception {
//            String facultyId = String.valueOf(ID1);
//
//            mockMvc.perform(post(URI_DEPARTMENTS)
//                    .param("name", NAME_FIRST_DEPARTMENT)
//                    .param("faculty.Id", facultyId))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(departmentServiceMock).save(departmentCaptor.capture());
//
//            Department expectedCreatedDepartment = departmentCaptor.getValue();
//            assertThat(expectedCreatedDepartment.getName(), is(NAME_FIRST_DEPARTMENT));
//            assertThat(expectedCreatedDepartment.getFaculty().getId(), is(ID1));
//        }
//
//        @Test
//        @DisplayName("when POST request with fail parameter (name with first " +
//            "letter lower case) then should return error 400.BAD_REQUEST")
//        void whenPostRequestWithFailParameter() throws Exception {
//            String facultyId = String.valueOf(ID1);
//
//            mockMvc.perform(post(URI_DEPARTMENTS)
//                    .param("name", FAIL_NAME_FIRST_DEPARTMENT)
//                    .param("faculty.Id", facultyId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isBadRequest(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$.violations[0].field", is("name")),
//                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getDepartment' method")
//    class GetDepartmentTest {
//
//        @Test
//        @DisplayName("when GET request with @PathParameter 'id' then should return " +
//            "JSON with expected department")
//        void getRequestWithIdParameter() throws Exception {
//            int departmentId = anyInt();
//            Faculty faculty = new Faculty(ID1, NAME_FIRST_FACULTY);
//            DepartmentDto expectedDepartment = new DepartmentDto(departmentId,
//                NAME_FIRST_DEPARTMENT, faculty.getId(), faculty.getName());
//
//            when(departmentServiceMock.getById(departmentId)).thenReturn(expectedDepartment);
//
//            mockMvc.perform(get(URI_DEPARTMENTS_ID, departmentId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isOk(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    content().string(containsString(String.valueOf(departmentId))),
//                    content().string(containsString(NAME_FIRST_DEPARTMENT))
//                );
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'updateDepartment' method")
//    class UpdateDepartmentTest {
//
//        @Test
//        @DisplayName("when PUT request with parameters 'id', 'name' and 'faculty.Id' " +
//            "then should call departmentService.update once")
//        void putRequestWithIdNameAndFacultyId() throws Exception {
//            int departmentId = anyInt();
//
//            mockMvc.perform(put(URI_DEPARTMENTS_ID, departmentId)
//                    .param("name", NAME_FIRST_DEPARTMENT)
//                    .param("faculty.Id", String.valueOf(ID1)))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(departmentServiceMock).save(departmentCaptor.capture());
//
//            Department expectedCreatedDepartment = departmentCaptor.getValue();
//            assertThat(expectedCreatedDepartment.getName(), is(NAME_FIRST_DEPARTMENT));
//            assertThat(expectedCreatedDepartment.getFaculty().getId(), is(ID1));
//        }
//
//        @Test
//        @DisplayName("when PUT request with fail parameter (name with first " +
//            "letter lower case) then should return error 400.BAD_REQUEST")
//        void whenPutRequestWithFailParameter() throws Exception {
//            String facultyId = String.valueOf(ID1);
//
//            mockMvc.perform(put(URI_DEPARTMENTS_ID, DEPARTMENT_ID1)
//                    .param("name", FAIL_NAME_FIRST_DEPARTMENT)
//                    .param("faculty.Id", facultyId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isBadRequest(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$.violations[0].field", is("name")),
//                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'deleteDepartment' method")
//    class DeleteDepartmentTest {
//
//        @Test
//        @DisplayName("when DELETE request with @PathParameter 'id' then should call " +
//            "departmentService.delete once and redirect")
//        void deleteRequestWithId() throws Exception {
//            int departmentId = anyInt();
//            mockMvc.perform(delete(URI_DEPARTMENTS_ID, departmentId))
//                .andDo(print())
//                .andExpect(status().is3xxRedirection());
//
//            verify(departmentServiceMock, times(1)).delete(departmentId);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getTeachersByDepartment' method")
//    class GetTeachersByDepartmentTest {
//
//        @Test
//        @DisplayName("when GET request with parameters 'id' then should return " +
//            "expected list teacherDTO")
//        void GetTeachersByDepartment() throws Exception {
//            int departmentId = DEPARTMENT_ID1;
//            List<TeacherDto> teachers = createTestTeacherDtos(FACULTY_ID1);
//
//            when(teacherServiceMock.getAllByDepartment(departmentId)).thenReturn(teachers);
//
//            mockMvc.perform(get("/departments/{id}/teachers", departmentId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isOk(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$", hasSize(2)),
//                    jsonPath("$[0].id", is(TEACHER_ID1)),
//                    jsonPath("$[0].firstName", is(NAME_FIRST_TEACHER)),
//                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_TEACHER)),
//                    jsonPath("$[0].departmentId", is(departmentId)),
//                    jsonPath("$[0].departmentName", is(NAME_FIRST_DEPARTMENT)),
//                    jsonPath("$[1].id", is(TEACHER_ID2)),
//                    jsonPath("$[1].firstName", is(NAME_SECOND_TEACHER)),
//                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_TEACHER)),
//                    jsonPath("$[1].departmentId", is(departmentId)),
//                    jsonPath("$[1].departmentName", is(NAME_FIRST_DEPARTMENT))
//                );
//            verify(teacherServiceMock).getAllByDepartment(departmentId);
//        }
//    }

}