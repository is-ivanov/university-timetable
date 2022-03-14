package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    public static final String URI_STUDENTS = "/students";
    public static final String URI_STUDENTS_ID = "/students/{id}";
    public static final String IS_SHOW_INACTIVE_GROUPS = "isShowInactiveGroups";
    public static final String IS_SHOW_INACTIVE_STUDENTS = "isShowInactiveStudents";

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
        mockMvc = MockMvcBuilders
            .standaloneSetup(studentController)
            .build();
    }

    @Nested
    @DisplayName("test 'showStudents' method")
    class ShowStudentsTest {

        @Test
        @DisplayName("when GET request without parameters then should call expected " +
            "services and not load students in attribute of model")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();
            List<Group> groups = createTestGroups(FACULTY_ID1);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(groupServiceMock.findAll()).thenReturn(groups);

            mockMvc.perform(get(URI_STUDENTS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("student"),
                    model().attributeDoesNotExist(IS_SHOW_INACTIVE_GROUPS,
                        IS_SHOW_INACTIVE_STUDENTS, "students"),
                    model().attribute("faculties", faculties),
                    model().attribute("groups", groups),
                    model().attribute("facultyIdSelect", is(nullValue())),
                    model().attribute("groupIdSelect", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with all parameters")
        void getRequestWithAllParameters() throws Exception {
            int facultyId = 3;
            int groupId = 5;

            List<Group> testGroups = createTestGroups(anyInt());
            List<StudentDto> testStudentDtos = createTestStudentDtos(groupId);

            when(studentServiceMock.getStudentsByGroup(groupId)).thenReturn(testStudentDtos);
            when(groupServiceMock.getAllByFacultyId(facultyId)).thenReturn(testGroups);

            mockMvc.perform(get(URI_STUDENTS)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("groupId", String.valueOf(groupId))
                    .param(IS_SHOW_INACTIVE_GROUPS, ON)
                    .param(IS_SHOW_INACTIVE_STUDENTS, ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("student"),
                    model().attribute(IS_SHOW_INACTIVE_GROUPS, true),
                    model().attribute(IS_SHOW_INACTIVE_STUDENTS, true),
                    model().attribute("students", testStudentDtos),
                    model().attribute("groups", testGroups)
                );
            verify(studentServiceMock, never()).getStudentsByFaculty(anyInt());
        }

        @Test
        @DisplayName("when GET request with facultyId and without groupId")
        void getRequestWithFacultyIdAndWithoutGroupId() throws Exception {
            int facultyId = 15;

            List<StudentDto> testStudentDtos = createTestStudentDtos(ID1);

            when(studentServiceMock.getStudentsByFaculty(facultyId)).thenReturn(testStudentDtos);

            mockMvc.perform(get(URI_STUDENTS)
                    .param("facultyId", String.valueOf(facultyId)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    model().attribute("students", testStudentDtos)
                );
            verify(studentServiceMock, never()).getStudentsByGroup(anyInt());
        }
    }

    @Nested
    @DisplayName("test 'deleteStudent' method")
    class DeleteStudentTest {
        @Test
        @DisplayName("when DELETE request with PathVariable id then should call " +
            "studentService.delete and redirect")
        void deleteRequestWithStudentIdShouldCallService() throws Exception {
            int studentId = 45;
            mockMvc.perform(delete(URI_STUDENTS_ID, studentId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(studentServiceMock, times(1)).delete(studentId);
        }
    }



}