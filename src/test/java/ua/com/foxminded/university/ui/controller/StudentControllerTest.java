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
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;
import static ua.com.foxminded.university.ui.controller.RoomControllerTest.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    public static final String URI_STUDENTS = "/students";
    public static final String URI_STUDENTS_ID = "/students/{id}";
    public static final String IS_SHOW_INACTIVE_GROUPS = "isShowInactiveGroups";
    public static final String IS_SHOW_INACTIVE_STUDENTS = "isShowInactiveStudents";
    public static final String URI_STUDENTS_ID_TIMETABLE = "/students/{id}/timetable";

    @Captor
    ArgumentCaptor<StudentDto> studentDtoCaptor;

    private MockMvc mockMvc;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private StudentDtoMapper studentDtoMapperMock;

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
            List<GroupDto> groups = createTestGroupDtos(FACULTY_ID1);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(groupServiceMock.getAll()).thenReturn(groups);

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

            List<GroupDto> testGroups = createTestGroupDtos(anyInt());
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
    @DisplayName("test 'createStudent' method")
    class CreateStudentTest {
        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call studentDtoMapper once")
        void postRequestWithParametersShouldCallStudentMapperAndRedirect() throws Exception {
            String firstName = NAME_FIRST_STUDENT;
            String patronymic = PATRONYMIC_FIRST_STUDENT;
            String lastName = LAST_NAME_FIRST_STUDENT;
            int groupId = 23;

            mockMvc.perform(post(URI_STUDENTS)
                    .param("firstName", firstName)
                    .param("patronymic", patronymic)
                    .param("lastName", lastName)
                    .param("active", ON)
                    .param("groupId", String.valueOf(groupId)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            verify(studentDtoMapperMock, times(1))
                .toStudent(studentDtoCaptor.capture());

            StudentDto expectedStudentDto = studentDtoCaptor.getValue();
            assertThat(expectedStudentDto.getFirstName(), is(equalTo(firstName)));
            assertThat(expectedStudentDto.getPatronymic(), is(equalTo(patronymic)));
            assertThat(expectedStudentDto.getLastName(), is(equalTo(lastName)));
            assertThat(expectedStudentDto.isActive(), is(true));
            assertThat(expectedStudentDto.getGroupId(), is(equalTo(groupId)));
        }
    }

    @Nested
    @DisplayName("test 'getStudent' method")
    class GetStudentTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should return " +
            "JSON with expected studentDto in body")
        void getRequestWithIdShouldReturnJsonWithStudentDto() throws Exception {
            StudentDto testStudentDto = createTestStudentDto();

            when(studentServiceMock.getById(STUDENT_ID1)).thenReturn(testStudentDto);

            mockMvc.perform(get(URI_STUDENTS_ID, STUDENT_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(STUDENT_ID1)),
                    jsonPath("$.firstName", is(NAME_FIRST_STUDENT)),
                    jsonPath("$.patronymic", is(PATRONYMIC_FIRST_STUDENT)),
                    jsonPath("$.lastName", is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$.fullName", is(FULL_NAME_FIRST_STUDENT)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.groupId", is(GROUP_ID1)),
                    jsonPath("$.groupName", is(NAME_FIRST_GROUP))
                );
        }
    }

    @Nested
    @DisplayName("test 'updateStudent' method")
    class UpdateStudentTest {
        @Test
        @DisplayName("when PUT request with all required parameters then should " +
            "call studentDtoMapper")
        void putRequestWithParametersShouldCallStudentDtoMapper() throws Exception {
            mockMvc.perform(put(URI_STUDENTS_ID, STUDENT_ID1)
                    .param("firstName", NAME_FIRST_STUDENT)
                    .param("patronymic", PATRONYMIC_FIRST_STUDENT)
                    .param("lastName", LAST_NAME_FIRST_STUDENT)
                    .param("active", ON)
                    .param("groupId", String.valueOf(GROUP_ID1)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            verify(studentDtoMapperMock, times(1))
                .toStudent(studentDtoCaptor.capture());

            StudentDto expectedStudentDto = studentDtoCaptor.getValue();

            assertThat(expectedStudentDto.getId(), is(STUDENT_ID1));
            assertThat(expectedStudentDto.getFirstName(), is(NAME_FIRST_STUDENT));
            assertThat(expectedStudentDto.getPatronymic(), is(PATRONYMIC_FIRST_STUDENT));
            assertThat(expectedStudentDto.getLastName(), is(LAST_NAME_FIRST_STUDENT));
            assertThat(expectedStudentDto.isActive(), is(true));
            assertThat(expectedStudentDto.getGroupId(), is(GROUP_ID1));
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

    @Nested
    @DisplayName("test 'getLessonsForStudent' method")
    class GetLessonsForStudentTest {
        @Test
        @DisplayName("when GET request with parameters id, start and end then " +
            "should return JSON with list lessonDtos in body")
        void getRequestWithParametersShouldReturnJsonWithListLessonDtos() throws Exception {
            int studentId = 32;
            List<LessonDto> testLessonDtos = createTestLessonDtos();

            when(lessonServiceMock.getAllForStudentForTimePeriod(studentId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(testLessonDtos);
            mockMvc.perform(get(URI_STUDENTS_ID_TIMETABLE, studentId)
                .param("start", START_TIME_TIMETABLE_ISO)
                .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2)),
                    jsonPath("$[0].id", is(LESSON_ID1)),
                    jsonPath("$[0].courseId", is(COURSE_ID1)),
                    jsonPath("$[0].courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$[0].teacherId", is(TEACHER_ID1)),
                    jsonPath("$[0].teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$[0].roomId", is(ROOM_ID1)),
                    jsonPath("$[0].buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$[0].timeStart", is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$[0].timeEnd", is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$[1].id", is(LESSON_ID2)),
                    jsonPath("$[1].courseId", is(COURSE_ID1)),
                    jsonPath("$[1].courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$[1].teacherId", is(TEACHER_ID1)),
                    jsonPath("$[1].teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$[1].roomId", is(ROOM_ID1)),
                    jsonPath("$[1].buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$[1].timeStart", is(TEXT_DATE_START_SECOND_LESSON)),
                    jsonPath("$[1].timeEnd", is(TEXT_DATE_END_SECOND_LESSON))
                );
        }
    }

}