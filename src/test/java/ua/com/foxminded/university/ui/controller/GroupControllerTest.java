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
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

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
class GroupControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "Faculty1";
    public static final String NAME_SECOND_FACULTY = "Faculty2";
    public static final String NAME_FIRST_GROUP = " First group";

    private MockMvc mockMvc;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test showGroups without parameters")
    void testShowGroupsWithoutParameters() throws Exception {
        List<Faculty> expectedFaculties = createExpectedFaculties();
        Group group1 = new Group(ID1, NAME_FIRST_GROUP,
            expectedFaculties.get(0), true);
        Group group2 = new Group(ID1, NAME_FIRST_GROUP,
            expectedFaculties.get(1), true);
        List<Group> allGroups = Arrays.asList(group1, group2);

        when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
        when(groupServiceMock.getAll()).thenReturn(allGroups);

        mockMvc.perform(get("/group"))
            .andDo(print())
            .andExpect(matchAll(
                status().isOk(),
                view().name("group"),
                model().attribute("faculties", expectedFaculties),
                model().attribute("groups", allGroups),
                model().attribute("facultySelect", is(nullValue())),
                model().attributeDoesNotExist("isShowInactive")
            ));
    }

    @Test
    @DisplayName("Test showGroups with parameters")
    void testShowGroupsWithParameters() throws Exception {
        List<Faculty> expectedFaculties = createExpectedFaculties();
        Faculty faculty = expectedFaculties.get(0);
        Group group1 = new Group(ID1, NAME_FIRST_GROUP,
            faculty, true);
        List<Group> expectedGroups = Collections.singletonList(group1);

        when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(expectedFaculties);
        when(groupServiceMock.getAllByFacultyId(ID1)).thenReturn(expectedGroups);

        mockMvc.perform(get("/group?facultyId=1&isShowInactive=on"))
            .andDo(print())
            .andExpect(matchAll(
                status().isOk(),
                view().name("group"),
                model().attribute("faculties", expectedFaculties),
                model().attribute("groups", expectedGroups),
                model().attribute("facultyIdSelect", ID1),
                model().attribute("isShowInactive", true)
            ));
    }

    private List<Faculty> createExpectedFaculties() {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        return Arrays.asList(faculty1, faculty2);
    }

}