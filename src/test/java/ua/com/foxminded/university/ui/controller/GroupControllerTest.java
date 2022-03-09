package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.restcontroller.link.GroupDtoAssembler;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    public static final String URI_GROUPS = "/groups";
    public static final String URI_GROUPS_ID = "/groups/{id}";
    public static final String ON = "on";


    private MockMvc mockMvc;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private GroupDtoAssembler assemblerMock;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(groupController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("test 'showGroups' method")
    class ShowGroupsTest {

        @Test
        @DisplayName("when GET request without parameters then should call " +
            "groupService.getAll once")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> allFaculties = createTestFaculties();
            List<Group> allGroups = createTestGroups(FACULTY_ID1);
            CollectionModel<GroupDto> modelGroups = createTestCollectionModelGroups();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(allFaculties);
            when(groupServiceMock.findAll()).thenReturn(allGroups);
            when(assemblerMock.toCollectionModel(allGroups)).thenReturn(modelGroups);

            mockMvc.perform(get(URI_GROUPS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("group"),
                    model().attributeExists("faculties", "groups"),
                    model().attribute("faculties", allFaculties),
                    model().attribute("groups", modelGroups),
                    model().attributeDoesNotExist("isShowInactive", "facultySelect")
                );

            verify(groupServiceMock, never()).getAllByFacultyId(anyInt());
        }

        @Test
        @DisplayName("when GET request with parameters facultyId and isShowInactive " +
            "then should call groupService.getAllByFacultyId once")
        void getRequestWithParameters() throws Exception {
            int facultyId = 1;

            List<Faculty> expectedFaculties = createTestFaculties();
            List<Group> expectedGroups = createTestGroups(facultyId);
            CollectionModel<GroupDto> modelGroups = createTestCollectionModelGroups();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
            when(groupServiceMock.getAllByFacultyId(facultyId)).thenReturn(expectedGroups);
            when(assemblerMock.toCollectionModel(expectedGroups)).thenReturn(modelGroups);

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
                    model().attribute("groups", modelGroups),
                    model().attribute("facultyIdSelect", facultyId),
                    model().attribute("isShowInactive", true)
                );

            verify(groupServiceMock, times(0)).findAll();
            verify(groupServiceMock, times(1)).getAllByFacultyId(facultyId);
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



}