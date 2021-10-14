package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

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
class StudentControllerTest {

    private static final int ID1 = 1;
    private static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "faculty1";
    public static final String NAME_SECOND_FACULTY = "faculty2";
    public static final String NAME_FIRST_GROUP = "group1";
    public static final String NAME_SECOND_GROUP = "group2";
    public static final String URL_ALL_PARAMETERS = "/student?facultyId=1&groupId=2&isShowInactiveGroups=on&isShowInactiveStudents=on";
    public static final String URL_FACULTY_PARAMETER = "/student?facultyId=1";
    public static final String URL_FACULTY_ID_0 = "/student/faculty?facultyId=0";
    public static final String URL_FACULTY_ID_1 = "/student/faculty?facultyId=1";

    private MockMvc mockMvc;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private GroupService groupServiceMock;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
            .setViewResolvers(viewResolver)
            .build();
    }

//    @Nested
//    @DisplayName("test showStudents")
//    class TestShowStudents {
//
//        @Test
//        @DisplayName("without parameters")
//        void testWithoutParameters() throws Exception {
//            Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
//            Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
//            List<Faculty> faculties = Arrays.asList(faculty1, faculty2);
//
//            Group group1 = new Group(ID1, NAME_FIRST_GROUP, faculty1, true);
//            Group group2 = new Group(ID2, NAME_SECOND_GROUP, faculty2, true);
//            List<Group> groups = Arrays.asList(group1, group2);
//
//            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
//            when(groupServiceMock.getAll()).thenReturn(groups);
//
//            mockMvc.perform(get("/student"))
//                .andDo(print())
//                .andExpect(matchAll(
//                    status().isOk(),
//                    view().name("student"),
//                    model().attributeDoesNotExist("isShowInactiveGroups",
//                        "isShowInactiveStudents", "students"),
//                    model().attribute("faculties", faculties),
//                    model().attribute("groups", groups),
//                    model().attribute("facultyIdSelect", is(nullValue())),
//                    model().attribute("groupIdSelect", is(nullValue()))
//                ));
//        }
//
//        @Test
//        @DisplayName("with all parameters")
//        void testWithAllParameters() throws Exception {
//            Student student1 = new Student();
//            student1.setId(ID1);
//            student1.setFirstName("student1 name");
//            List<Student> students = Collections.singletonList(student1);
//            Group group1 = new Group();
//            group1.setId(ID1);
//            List<Group> groups = Collections.singletonList(group1);
//
//            when(studentServiceMock.getStudentsByGroup(ID2)).thenReturn(students);
//            when(groupServiceMock.getAllByFacultyId(ID1)).thenReturn(groups);
//
//            mockMvc.perform(get(URL_ALL_PARAMETERS))
//                .andDo(print())
//                .andExpect(matchAll(
//                    status().isOk(),
//                    view().name("student"),
//                    model().attribute("isShowInactiveGroups", true),
//                    model().attribute("isShowInactiveStudents", true),
//                    model().attribute("students", students),
//                    model().attribute("groups", groups)
//                ));
//        }
//
//        @Test
//        @DisplayName("with facultyId and without groupId")
//        void testWithFacultyIdAndWithoutGroupId() throws Exception {
//            Student student1 = new Student();
//            student1.setId(ID1);
//            student1.setFirstName("student1 name");
//            List<Student> students = Collections.singletonList(student1);
//
//            when(studentServiceMock.getStudentsByFaculty(ID1)).thenReturn(students);
//
//            mockMvc.perform(get(URL_FACULTY_PARAMETER))
//                .andDo(print())
//                .andExpect(matchAll(
//                    status().isOk(),
//                    view().name("student"),
//                    model().attribute("students", students)
//                ));
//        }
//    }

//    @Nested
//    @DisplayName("test getGroups")
//    class TestGetGroups {
//
//        @Test
//        @DisplayName("with parameter facultyId = 0")
//        void withParameterFacultyId0() throws Exception {
//            Group group1 = new Group();
//            group1.setId(ID1);
//            group1.setName("group1");
//            Group group2 = new Group();
//            group2.setId(ID2);
//            group2.setName("group2");
//            List<Group> groups = Arrays.asList(group1, group2);
//
//            when(groupServiceMock.getAll()).thenReturn(groups);
//
//            mockMvc.perform(get(URL_FACULTY_ID_0))
//
//                .andDo(print())
//                .andExpect(matchAll(
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    status().isOk(),
//                    jsonPath("$.length()", is(2)),
//                    jsonPath("$.[0].name",is("group1")),
//                    jsonPath("$.[1].name", is("group2"))
//                ));
//        }
//
//        @Test
//        @DisplayName("with parameter facultyId = 1")
//        void withParameterFacultyId1() throws Exception {
//            Group group1 = new Group();
//            group1.setId(ID1);
//            group1.setName("group1");
//            List<Group> groups = Collections.singletonList(group1);
//
//            when(groupServiceMock.getAllByFacultyId(ID1)).thenReturn(groups);
//
//            mockMvc.perform(get(URL_FACULTY_ID_1))
//
//                .andDo(print())
//                .andExpect(matchAll(
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    status().isOk(),
//                    jsonPath("$.length()", is(1)),
//                    jsonPath("$.[0].name", is("group1"))
//                ));
//        }
//    }
}