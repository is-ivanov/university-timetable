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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.university.domain.entity.FacultyAssert;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.GroupAssert;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.*;

@WebMvcTest(GroupRestController.class)
@Import(TestMapperConfig.class)
class GroupRestControllerTest {

    @Captor
    ArgumentCaptor<Group> groupCaptor;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupServiceMock;

    @MockBean
    private StudentService studentServiceMock;

    @Nested
    @DisplayName("test 'getGroups' method")
    class GetGroupsTest {
        @Test
        @DisplayName("when GET request without parameters and service return list " +
            "groups then method return CollectionModel<GroupDto> with expected links")
        void whenServiceReturnListGroups_MethodReturnCollectionModel() throws Exception {

            List<Group> testGroups = createTestGroups();

            when(groupServiceMock.findAll()).thenReturn(testGroups);

            mockMvc.perform(get(API_GROUPS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.groups", hasSize(2)),
                    jsonPath("$._embedded.groups[0].id",
                        is(GROUP_ID1)),
                    jsonPath("$._embedded.groups[0].name",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.groups[0]._links.self.href",
                        is(GROUP1_SELF_LINK)),
                    jsonPath("$._embedded.groups[1].id",
                        is(GROUP_ID2)),
                    jsonPath("$._embedded.groups[1].name",
                        is(NAME_SECOND_GROUP)),
                    jsonPath("$._embedded.groups[1]._links.self.href",
                        is(GROUP2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(GROUPS_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'getGroup' method")
    class GetGroupTest {

        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should call " +
            "groupService.getById once and return JSON with expected group")
        void getRequestWithParameterId() throws Exception {
            Group testGroup = createTestGroup();

            when(groupServiceMock.findById(GROUP_ID1)).thenReturn(testGroup);

            mockMvc.perform(get(MappingConstants.API_GROUPS_ID, GROUP_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(GROUP_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_GROUP)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY)),
                    jsonPath("$._links.self.href", is(GROUP1_SELF_LINK))
                );

        }
    }

    @Nested
    @DisplayName("test 'createGroup' method")
    class CreateGroupTest {

        @Test
        @DisplayName("when POST request with parameters 'name', 'active' and " +
            "'faculty.id' then should call groupService.add once")
        void postRequestWithParametersNameActiveFacultyId() throws Exception {
            String bodyRequest = "{\"name\": \"" + NAME_FIRST_GROUP + "\"," +
                "\"facultyId\": \"" + FACULTY_ID1 + "\"," +
                "\"active\": " + TRUE + "}";
            Group groupAfterCreating = createTestGroup();

            when(groupServiceMock.create(any())).thenReturn(groupAfterCreating);

            mockMvc.perform(post(API_GROUPS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(GROUP_ID1)),
                    jsonPath("$.name", is(NAME_FIRST_GROUP)),
                    jsonPath("$.active", is(TRUE)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY))
                );

            verify(groupServiceMock, times(1)).create(groupCaptor.capture());
            Group expectedGroup = groupCaptor.getValue();
            GroupAssert.assertThat(expectedGroup)
                .hasId(null)
                .hasName(NAME_FIRST_GROUP)
                .isActive();
            FacultyAssert.assertThat(expectedGroup.getFaculty())
                .hasId(FACULTY_ID1)
                .hasName(null);
        }

        @Test
        @DisplayName("when POST request with fail parameter (empty name) " +
            "then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            String bodyRequest = "{\"name\": \" \"," +
                "\"facultyId\": \"" + FACULTY_ID1 + "\"," +
                "\"active\": " + TRUE + "}";

            mockMvc.perform(post(API_GROUPS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message",
                        is("The group name must not be blank")));
        }
    }

    @Nested
    @DisplayName("test 'updateGroup' method")
    class UpdateGroupTest {

        @Test
        @DisplayName("when PUT request with parameters 'id', 'name', 'active' and " +
            "'faculty.id' then should call groupService.update once")
        void putRequestWithParameters() throws Exception {
            String newGroupName = "new name";
            String jsonBodyRequest = "{\"name\": \"" + newGroupName + "\"," +
                "\"facultyId\": \"" + FACULTY_ID1 + "\"," +
                "\"active\": " + TRUE + ", " +
                "\"id\":" + GROUP_ID1 + "}";
            Group groupAfterCreating = createTestGroup();
            groupAfterCreating.setName(newGroupName);

            when(groupServiceMock.update(anyInt(), any(Group.class)))
                .thenReturn(groupAfterCreating);

            mockMvc.perform(put(API_GROUPS_ID, GROUP_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(GROUP_ID1)),
                    jsonPath("$.name", is(newGroupName)),
                    jsonPath("$.active", is(TRUE)),
                    jsonPath("$.facultyId", is(FACULTY_ID1)),
                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY))
                );

            verify(groupServiceMock).update(eq(GROUP_ID1), groupCaptor.capture());
            Group expectedGroup = groupCaptor.getValue();

            GroupAssert.assertThat(expectedGroup)
                .hasId(GROUP_ID1)
                .hasName(newGroupName)
                .isActive();
            FacultyAssert.assertThat(expectedGroup.getFaculty())
                .hasId(FACULTY_ID1)
                .hasName(null);
        }

        @Test
        @DisplayName("when PUT request with json body without property 'id' then " +
            "should return error 400.BAD_REQUEST")
        void whenPutRequestWithBodyWithoutPropertyId_ReturnError400BadRequest() throws Exception {
            String newGroupName = "new name";
            String jsonBodyRequest = "{\"name\": \"" + newGroupName + "\"," +
                "\"facultyId\": \"" + FACULTY_ID1 + "\"," +
                "\"active\": " + TRUE + "}";

            mockMvc.perform(put(API_GROUPS_ID, GROUP_ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.message", is("not ID in the request body"))
                );
        }
    }

    @Nested
    @DisplayName("test 'getFreeStudentsFromGroup' method")
    class GetFreeStudentsFromGroupTest {

        @Test
        @DisplayName("when GET request with parameters 'id', 'time_start' and " +
            "'time_end' then should return expected CollectionModel<StudentDto>")
        void getRequestWithPathParameterId() throws Exception {
            int groupId = 5;
            LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 13, 15);
            LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 14, 45);

            List<Student> testStudents = createTestStudents();

            when(studentServiceMock.getFreeStudentsFromGroup(groupId, startTime,
                endTime)).thenReturn(testStudents);

            String timeStartParameter = "2021-10-12 13:15";
            String timeEndParameter = "2021-10-12 14:45";
            String timeStartEncodedParameter = URLEncoder.encode(timeStartParameter,
                StandardCharsets.UTF_8.toString());
            String timeEndEncodedParameter = URLEncoder.encode(timeEndParameter,
                StandardCharsets.UTF_8.toString());

            String uriSelf = "http://localhost/api/groups/" + groupId +
                "/students/free?" +
                TIME_START + "=" + timeStartEncodedParameter + "&"
                + TIME_END + "=" + timeEndEncodedParameter;

            mockMvc.perform(get(API_GROUPS_ID_STUDENTS_FREE, groupId)
                    .param(TIME_START, timeStartParameter)
                    .param(TIME_END, timeEndParameter))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.students",
                        hasSize(testStudents.size())),
                    jsonPath("$._embedded.students[0].id",
                        is(STUDENT_ID1)),
                    jsonPath("$._embedded.students[0].firstName",
                        is(NAME_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].patronymic",
                        is(PATRONYMIC_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].lastName",
                        is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].active",
                        is(TRUE)),
                    jsonPath("$._embedded.students[0].groupName",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.students[1].id",
                        is(STUDENT_ID2)),
                    jsonPath("$._embedded.students[1].firstName",
                        is(NAME_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].patronymic",
                        is(PATRONYMIC_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].lastName",
                        is(LAST_NAME_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].active",
                        is(TRUE)),
                    jsonPath("$._embedded.students[1].groupName",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._links.self.href",
                        is(uriSelf.replace("+", "%20")))
                );
        }
    }

}