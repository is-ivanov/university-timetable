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
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    public static final String URI_GROUPS = "/groups";
    public static final String ON = "on";
    public static final String URI_GROUPS_ID = "/groups/{id}";

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
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
            .build();
    }

    @Nested
    @DisplayName("test 'showGroups' method")
    class ShowGroupsTest {

        @Test
        @DisplayName("when GET request without parameters then should call " +
            "groupService.getAll once")
        void testShowGroupsWithoutParameters() throws Exception {
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
        void testShowGroupsWithParameters() throws Exception {
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
                    jsonPath("$.id", is(equalTo(groupId))),
                    jsonPath("$.name", is(equalTo(NAME_FIRST_GROUP))),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.faculty.id", is(equalTo(ID1))),
                    jsonPath("$.faculty.name", is(equalTo(NAME_FIRST_FACULTY)))
                );
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
                .andDo(print());
        }
    }
}