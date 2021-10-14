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
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.FacultyControllerTest.TIME_END;
import static ua.com.foxminded.university.ui.controller.FacultyControllerTest.TIME_START;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    public static final String URI_GROUPS = "/groups";
    public static final String URI_GROUPS_ID = "/groups/{id}";
    public static final String URI_GROUPS_ID_STUDENTS_FREE = "/groups/{id}/students/free";
    public static final String ON = "on";

    @Captor
    ArgumentCaptor<Group> groupCaptor;

    private MockMvc mockMvc;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private StudentDtoMapper studentDtoMapperMock;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Nested
    @DisplayName("test 'showGroups' method")
    class ShowGroupsTest {

        @Test
        @DisplayName("when GET request without parameters then should call " +
            "groupService.getAll once")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> allFaculties = createTestFaculties();
            List<Group> allGroups = createTestGroups();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(allFaculties);
            when(groupServiceMock.getAll()).thenReturn(allGroups);

            mockMvc.perform(get(URI_GROUPS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("group"),
                    model().attributeExists("faculties", "groups"),
                    model().attribute("faculties", allFaculties),
                    model().attribute("groups", allGroups),
                    model().attributeDoesNotExist("isShowInactive", "facultySelect")
                );

            verify(groupServiceMock, times(0)).getAllByFacultyId(anyInt());
        }

        @Test
        @DisplayName("when GET request with parameters facultyId and isShowInactive " +
            "then should call groupService.getAllByFacultyId once")
        void getRequestWithParameters() throws Exception {
            int facultyId = 1;

            List<Faculty> expectedFaculties = createTestFaculties();
            List<Group> expectedGroups = createTestGroups(facultyId);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
            when(groupServiceMock.getAllByFacultyId(facultyId)).thenReturn(expectedGroups);

            mockMvc.perform(get(URI_GROUPS)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("isShowInactive", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("group"),
                    model().attributeExists("isShowInactive", "faculties",
                        "groups", "facultyIdSelect"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("groups", expectedGroups),
                    model().attribute("facultyIdSelect", facultyId),
                    model().attribute("isShowInactive", true)
                );

            verify(groupServiceMock, times(0)).getAll();
            verify(groupServiceMock, times(1)).getAllByFacultyId(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'createGroup' method")
    class CreateGroupTest {

        @Test
        @DisplayName("when POST request with parameters 'name', 'active' and " +
            "'faculty.id' then should call groupService.add once and redirect")
        void postRequestWithParametersNameActiveFacultyId() throws Exception {
            mockMvc.perform(post(URI_GROUPS)
                    .param("name", NAME_FIRST_GROUP)
                    .param("active", ON)
                    .param("faculty.id", String.valueOf(ID1)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(groupServiceMock, times(1)).add(groupCaptor.capture());
            Group expectedGroup = groupCaptor.getValue();
            assertThat(expectedGroup.getFaculty().getId(), is(equalTo(ID1)));
            assertThat(expectedGroup.getName(), is(equalTo(NAME_FIRST_GROUP)));
            assertThat(expectedGroup.isActive(), is(true));
        }
    }

    @Nested
    @DisplayName("test 'getGroup' method")
    class GetGroupTest {

        @Test
        @DisplayName("when GET request with @PathParameter 'id' then should call " +
            "groupService.getById once and return JSON with expected group")
        void getRequestWithParameterId() throws Exception {
            int groupId = ID1;
            Group testGroup = createTestGroup();

            when(groupServiceMock.getById(groupId)).thenReturn(testGroup);

            mockMvc.perform(get(URI_GROUPS_ID, groupId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(groupId)),
                    jsonPath("$.name", is(NAME_FIRST_GROUP)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.faculty.id", is(ID1)),
                    jsonPath("$.faculty.name", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock,times(1)).getById(groupId);
        }
    }

    @Nested
    @DisplayName("test 'updateGroup' method")
    class UpdateGroupTest {

        @Test
        @DisplayName("when PUT request with parameters 'id', 'name', 'active' and " +
            "'faculty.id' then should call groupService.update once and redirect")
        void putRequestWithParameters() throws Exception {
            int groupId = anyInt();
            mockMvc.perform(put(URI_GROUPS_ID, groupId)
                    .param("name", NAME_SECOND_GROUP)
                    .param("active", ON)
                    .param("faculty.id", String.valueOf(ID2)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(groupServiceMock, times(1)).update(groupCaptor.capture());
            Group expectedGroup = groupCaptor.getValue();
            assertThat(expectedGroup.getId(), is(equalTo(groupId)));
            assertThat(expectedGroup.getName(), is(equalTo(NAME_SECOND_GROUP)));
            assertThat(expectedGroup.isActive(), is(true));
            assertThat(expectedGroup.getFaculty().getId(), is(equalTo(ID2)));
        }
    }

    @Nested
    @DisplayName("test 'deleteGroup' method")
    class DeleteGroupTest {

        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then should call " +
            "groupService.delete once and redirect")
        void deleteRequestWithId() throws Exception {
            int groupId = anyInt();
            mockMvc.perform(delete(URI_GROUPS_ID, groupId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(groupServiceMock, times(1)).delete(groupId);
        }
    }

    @Nested
    @DisplayName("test 'getFreeStudentsFromGroup' method")
    class GetFreeStudentsFromGroupTest {

        @Test
        @DisplayName("when GET request with parameters 'id', 'time_start' and " +
            "'time_end' then should return expected list StudentDtos")
        void getRequestWithPathParameterId() throws Exception {
            int groupId = 5;
            LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 13, 15);
            LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 14, 45);

            List<Student> testStudents = createTestStudents();
            List<StudentDto> testStudentDtos = createTestStudentDtos(groupId);

            when(studentServiceMock.getFreeStudentsFromGroup(groupId, startTime,
                endTime)).thenReturn(testStudents);
            when(studentDtoMapperMock.studentsToStudentDtos(testStudents))
                .thenReturn(testStudentDtos);

            mockMvc.perform(get(URI_GROUPS_ID_STUDENTS_FREE, groupId)
                    .param(TIME_START, "2021-10-12 13:15")
                    .param(TIME_END, "2021-10-12 14:45"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testStudentDtos.size())),
                    jsonPath("$[0].id", is(ID1)),
                    jsonPath("$[0].firstName", is(NAME_FIRST_STUDENT)),
                    jsonPath("$[0].patronymic", is(PATRONYMIC_FIRST_STUDENT)),
                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$[0].active", is(true)),
                    jsonPath("$[0].groupId", is(groupId)),
                    jsonPath("$[0].groupName", is(NAME_FIRST_GROUP)),
                    jsonPath("$[1].id", is(ID2)),
                    jsonPath("$[1].firstName", is(NAME_SECOND_STUDENT)),
                    jsonPath("$[1].patronymic", is(PATRONYMIC_SECOND_STUDENT)),
                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_STUDENT)),
                    jsonPath("$[1].active", is(false)),
                    jsonPath("$[1].groupId", is(groupId)),
                    jsonPath("$[1].groupName", is(NAME_FIRST_GROUP))
                );
                verify(studentServiceMock, times(1))
                    .getFreeStudentsFromGroup(groupId, startTime, endTime);
        }
    }

}