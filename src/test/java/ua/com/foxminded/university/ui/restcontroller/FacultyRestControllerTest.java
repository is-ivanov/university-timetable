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
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_FACULTIES;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_FACULTIES_ID;

@WebMvcTest(FacultyRestController.class)
@Import(TestMapperConfig.class)
class FacultyRestControllerTest {

    public static final String URI_FACULTIES_ID_GROUPS = "/api/faculties/{id}/groups";
    public static final String URI_FACULTIES_ID_DEPARTMENTS = "/api/faculties/{id}/departments";
    public static final String URI_FACULTIES_ID_TEACHERS = "/api/faculties/{id}/teachers";
    public static final String URI_FACULTIES_ID_GROUPS_FREE = "/api/faculties/{id}/groups/free";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";
    public static final String FAIL_FACULTY_NAME = "fail Name";
    public static final String MESSAGE_NOT_ID = "not ID in the request body";
    public static final String MESSAGE_DIFFERENT_ID =
        "ID in the request body have to be equal ID in URI";

    @Captor
    ArgumentCaptor<Faculty> facultyCaptor;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyServiceMock;

    @MockBean
    private GroupService groupServiceMock;

    @MockBean
    private DepartmentService departmentServiceMock;

    @MockBean
    private TeacherService teacherServiceMock;

    @Nested
    @DisplayName("test 'getFaculties' method")
    class GetFacultiesTest {
        @Test
        @DisplayName("when GET request without parameters then should return " +
            "all faculties with status OK")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();

            when(facultyServiceMock.findAll()).thenReturn(faculties);

            mockMvc.perform(get(MappingConstants.API_FACULTIES))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.faculties", hasSize(2)),
                    jsonPath("$._embedded.faculties[0].id",
                        is(FACULTY_ID1)),
                    jsonPath("$._embedded.faculties[0].name",
                        is(NAME_FIRST_FACULTY)),
                    jsonPath("$._embedded.faculties[0]._links.self.href",
                        is(FACULTY1_SELF_LINK)),
                    jsonPath("$._embedded.faculties[1].id",
                        is(FACULTY_ID2)),
                    jsonPath("$._embedded.faculties[1].name",
                        is(NAME_SECOND_FACULTY)),
                    jsonPath("$._embedded.faculties[1]._links.self.href",
                        is(FACULTY2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(FACULTIES_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'getFacultiesPaginatedAndSorted' method")
    class GetFacultiesPaginatedAndSortedTest {
        @Test
        @DisplayName("when GET request with parameters page, size and sort then " +
            "should use this parameters and return HAL+JSON PagedModel with expected links")
        void whenServiceReturnPageCourseThenMethodReturnPagedModel() throws Exception {
            int page = 3;
            int size = 2;
            String sort = "id,desc";
            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("id")));
            Page<Faculty> facultyPage = createTestPageFaculty(pageable);

            when(facultyServiceMock.findAll(pageable)).thenReturn(facultyPage);
            String parameters = "?page=" + page + "&size=" + size + "&sort=" + sort;
            String sefLink = FACULTIES_LINK + parameters;

            mockMvc.perform(get(API_FACULTIES + parameters))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.faculties", hasSize(2)),
                    jsonPath("$._embedded.faculties[0].id",
                        is(FACULTY_ID1)),
                    jsonPath("$._embedded.faculties[0].name",
                        is(NAME_FIRST_FACULTY)),
                    jsonPath("$._embedded.faculties[0]._links.self.href",
                        is(FACULTY1_SELF_LINK)),
                    jsonPath("$._embedded.faculties[1].id",
                        is(FACULTY_ID2)),
                    jsonPath("$._embedded.faculties[1].name",
                        is(NAME_SECOND_FACULTY)),
                    jsonPath("$._embedded.faculties[1]._links.self.href",
                        is(FACULTY2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(sefLink)),
                    jsonPath("$.page.size", is(size)),
                    jsonPath("$.page.number", is(page))
                );
        }
    }

    @Nested
    @DisplayName("test 'getFaculty' method")
    class GetFacultyTest {

        @Test
        @DisplayName("when GET request with @PathVariable 'id' then should return " +
            "JSON with expected faculty")
        void getRequestWithId() throws Exception {
            Faculty testFaculty = createTestFaculty(FACULTY_ID1);

            when(facultyServiceMock.findById(FACULTY_ID1)).thenReturn(testFaculty);

            mockMvc.perform(get(API_FACULTIES_ID, FACULTY_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(FACULTY_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_FACULTY)),
                    jsonPath("$._links.self.href", is(FACULTY1_SELF_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createFaculty' method")
    class CreateFacultyTest {

        @Test
        @DisplayName("when POST request with parameter name then should call " +
            "facultyService.add once")
        void postRequestWithParameterName() throws Exception {
            String jsonBodyRequest = "{\"name\": \"" + NAME_FIRST_FACULTY + "\"}";
            Faculty createdFaculty = createTestFaculty();

            when(facultyServiceMock.create(any())).thenReturn(createdFaculty);

            mockMvc.perform(post(API_FACULTIES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(FACULTY_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_FACULTY)),
                    jsonPath("$._links.self.href", is(FACULTY1_SELF_LINK))
                );

            verify(facultyServiceMock).create(facultyCaptor.capture());

            Faculty facultyForSaving = facultyCaptor.getValue();
            FacultyAssert.assertThat(facultyForSaving)
                .hasId(null)
                .hasName(NAME_FIRST_FACULTY);
        }

        @Test
        @DisplayName("when POST request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            String jsonBodyRequest = "{\"name\": \"" + FAIL_FACULTY_NAME + "\"}";

            mockMvc.perform(post(API_FACULTIES)
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
    @DisplayName("test 'updateFaculty' method")
    class UpdateFacultyTest {

        @Test
        @DisplayName("when PUT request with parameters 'id' and 'name' then should " +
            "call facultyService.update call")
        void putRequestWithIdAndName() throws Exception {
            String newFacultyName = "New Faculty Name";
            String jsonBodyRequest = "{\"name\": \"" + newFacultyName + "\", " +
                "\"id\": " + FACULTY_ID1 + "}";
            Faculty updatedFaculty = new Faculty(FACULTY_ID1, newFacultyName);

            when(facultyServiceMock.update(FACULTY_ID1, updatedFaculty))
                .thenReturn(updatedFaculty);

            mockMvc.perform(put(API_FACULTIES_ID, FACULTY_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(FACULTY_ID1)),
                    jsonPath("$.name", is(newFacultyName))
                );
        }

        @Test
        @DisplayName("when PUT request without 'id' in body then should return" +
            " error 400.BAD_REQUEST")
        void whenPutRequestWithoutIdInBody_Error400BadRequest() throws Exception {
            String newFacultyName = "New Faculty Name";
            String jsonBodyRequest = "{\"name\": \"" + newFacultyName + "\"}";

            mockMvc.perform(put(API_FACULTIES_ID, FACULTY_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.message", is(MESSAGE_NOT_ID))
                );
        }

        @Test
        @DisplayName("when PUT request with 'id' in body different 'id' in path " +
            "then should return error 400.BAD_REQUEST")
        void whenPutRequestWithIdInBodyDifferentIdPath_Error400BadRequest()
            throws Exception {
            int bodyId = 2;
            int pathId = 3;
            String newFacultyName = "New Faculty Name";
            String jsonBodyRequest = "{\"name\": \"" + newFacultyName + "\", " +
                "\"id\": " + bodyId + "}";

            mockMvc.perform(put(API_FACULTIES_ID, pathId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.message", is(MESSAGE_DIFFERENT_ID))
                );
        }
    }


    @Nested
    @DisplayName("test 'getGroupsByFaculty' method")
    class GetGroupsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id = 0 then should call " +
            "groupService.findAll once and return JSON with groups")
        void getRequestWithId0() throws Exception {
            int facultyId = 0;
            List<Group> testGroups = createTestGroups();

            when(groupServiceMock.findAll()).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.groups", hasSize(testGroups.size())),
                    jsonPath("$._embedded.groups[0].id", is(GROUP_ID1)),
                    jsonPath("$._embedded.groups[0].name",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.groups[0].facultyId",
                        is(FACULTY_ID1)),
                    jsonPath("$._embedded.groups[0].facultyName",
                        is(NAME_FIRST_FACULTY)),
                    jsonPath("$._embedded.groups[1].id", is(GROUP_ID2)),
                    jsonPath("$._embedded.groups[1].name",
                        is(NAME_SECOND_GROUP)),
                    jsonPath("$._embedded.groups[1].facultyId",
                        is(FACULTY_ID1)),
                    jsonPath("$._embedded.groups[1].facultyName",
                        is(NAME_FIRST_FACULTY))
                );

            verify(groupServiceMock, never()).getAllByFacultyId(facultyId);
        }

        @Test
        @DisplayName("when GET request with parameter id != 0 then should call " +
            "groupService.getAllByFacultyId once and return JSON with groups")
        void getRequestWithId2() throws Exception {
            int facultyId = 3;
            List<Group> testGroups = createTestGroups(facultyId);

            when(groupServiceMock.getAllByFacultyId(facultyId))
                .thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON)
                );
        }
    }

    @Nested
    @DisplayName("test 'getFreeGroupsByFaculty' method")
    class GetFreeGroupsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameters then should call " +
            "groupService.getFreeGroupsByFacultyOnLessonTime once return JSON with groups")
        void getRequestWithParameters() throws Exception {
            int facultyId = 4;
            List<Group> testGroups = createTestGroups(facultyId);

            when(groupServiceMock.getFreeGroupsByFacultyOnLessonTime(facultyId,
                DATE_FROM, DATE_TO)).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS_FREE, facultyId)
                    .param(TIME_START, TEXT_DATE_FROM)
                    .param(TIME_END, TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.groups", hasSize(testGroups.size())),
                    jsonPath("$._embedded.groups[0].id", is(GROUP_ID1)),
                    jsonPath("$._embedded.groups[0].name",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.groups[0].facultyId",
                        is(facultyId)),
                    jsonPath("$._embedded.groups[0].facultyName",
                        is(NAME_FIRST_FACULTY)),
                    jsonPath("$._embedded.groups[1].id", is(GROUP_ID2)),
                    jsonPath("$._embedded.groups[1].name",
                        is(NAME_SECOND_GROUP)),
                    jsonPath("$._embedded.groups[1].facultyId",
                        is(facultyId)),
                    jsonPath("$._embedded.groups[1].facultyName",
                        is(NAME_FIRST_FACULTY))
                );
        }
    }

    @Nested
    @DisplayName("test 'getDepartmentsByFaculty' method")
    class GetDepartmentsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id = 0 then should call " +
            "departmentService.getAll once and return JSON with departments")
        void getRequestWithParameterIdEquals0() throws Exception {
            int facultyId = 0;
            List<Department> testDepartments = createTestDepartments();

            when(departmentServiceMock.findAll()).thenReturn(testDepartments);

            mockMvc.perform(get(URI_FACULTIES_ID_DEPARTMENTS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.departments",
                        hasSize(testDepartments.size())),
                    jsonPath("$._embedded.departments[0].name",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.departments[1].name",
                        is(NAME_SECOND_DEPARTMENT))
                );

            verify(departmentServiceMock, never()).getAllByFaculty(facultyId);
        }

        @Test
        @DisplayName("when GET request with parameter id > 0 then should call " +
            "departmentService.getAllByFaculty once and return JSON with departments")
        void getRequestWithParameterIdEquals8() throws Exception {
            int facultyId = 8;
            List<Department> testDepartments = createTestDepartments();

            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testDepartments);

            mockMvc.perform(get(URI_FACULTIES_ID_DEPARTMENTS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.departments",
                        hasSize(testDepartments.size())),
                    jsonPath("$._embedded.departments[0].name",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.departments[1].name",
                        is(NAME_SECOND_DEPARTMENT))
                );
            verify(departmentServiceMock, never()).findAll();
        }
    }

    @Nested
    @DisplayName("test 'getTeachersByFaculty' method")
    class GetTeachersByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id then should call " +
            "teacherDtoMapper and teacherService once")
        void getRequestWithParameterIdEquals4() throws Exception {
            int facultyId = 4;
            List<Teacher> testTeacherDtos = createTestTeachers(facultyId);

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_FACULTIES_ID_TEACHERS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.teachers",
                        hasSize(testTeacherDtos.size())),
                    jsonPath("$._embedded.teachers[0].firstName",
                        is(NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].lastName",
                        is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].patronymic",
                        is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[1].firstName",
                        is(NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].lastName",
                        is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].patronymic",
                        is(PATRONYMIC_SECOND_TEACHER))
                );
        }
    }
}