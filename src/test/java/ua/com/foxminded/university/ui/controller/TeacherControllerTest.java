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
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

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
class TeacherControllerTest {

    public static final String URI_TEACHERS = "/teachers";
    public static final String URI_TEACHERS_FREE = "/teachers/free";
    public static final String URI_TEACHERS_ID = "/teachers/{id}";
    public static final String URI_TEACHERS_ID_TIMETABLE = "/teachers/{id}/timetable";

    @Captor
    ArgumentCaptor<TeacherDto> teacherDtoCaptor;

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private TeacherDtoMapper teacherMapperMock;

    @Mock
    private LessonService lessonServiceMock;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(teacherController)
            .build();
    }

    @Nested
    @DisplayName("test 'showTeachers' method")
    class ShowTeachersTest {
        @Test
        @DisplayName("when GET request without parameters then should call expected " +
            "services and not load teachers in attribute of model")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();
            List<DepartmentDto> departments = createTestDepartmentDtos();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.getAll()).thenReturn(departments);

            mockMvc.perform(get(URI_TEACHERS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "teachers"),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("facultyIdSelect", is(nullValue())),
                    model().attribute("groupIdSelect", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with all parameters")
        void getRequestWithAllParameters() throws Exception {
            int facultyId = 15;
            int departmentId = 47;

            List<DepartmentDto> departments = createTestDepartmentDtos();
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByDepartment(departmentId))
                .thenReturn(testTeacherDtos);
            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(departments);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("departmentId", String.valueOf(departmentId))
                    .param("isShowInactiveTeachers", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("teachers", testTeacherDtos),
                    model().attribute("departments", departments)
                );
        }

        @Test
        @DisplayName("when GET request with parameter facultyId and without departmentId")
        void getRequestWithFacultyIdAndWithoutDepartmentId() throws Exception {
            int facultyId = 34;

            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("teachers", testTeacherDtos)
                );
            verify(departmentServiceMock, times(1))
                .getAllByFaculty(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'getFreeTeachers' method")
    class GetFreeTeachersTest {
        @Test
        @DisplayName("when GET request with parameters time_start and time_end " +
            "then should return JSON with list teacherDtos in body")
        void getRequestWithParametersShouldReturnJsonListTeacherDtos() throws Exception {
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(FACULTY_ID1);

            when(teacherServiceMock.getFreeTeachersOnLessonTime(DATE_FROM, DATE_TO))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_TEACHERS_FREE)
                    .param("time_start", TEXT_DATE_FROM)
                    .param("time_end", TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2)),
                    jsonPath("$[0].id", is(TEACHER_ID1)),
                    jsonPath("$[0].firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$[0].patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$[0].departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$[0].departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[0].active", is(true)),
                    jsonPath("$[1].id", is(TEACHER_ID2)),
                    jsonPath("$[1].firstName", is(NAME_SECOND_TEACHER)),
                    jsonPath("$[1].patronymic", is(PATRONYMIC_SECOND_TEACHER)),
                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$[1].departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$[1].departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[1].active", is(true))
                );
        }
    }

    @Nested
    @DisplayName("test 'createTeacher' method")
    class CreateTeacherTest {
        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call teacherDtoMapper and redirect")
        void postRequestWithParametersShouldCallTeacherMapperAndRedirect() throws Exception {
            mockMvc.perform(post(URI_TEACHERS)
                    .param("firstName", NAME_FIRST_TEACHER)
                    .param("patronymic", PATRONYMIC_FIRST_TEACHER)
                    .param("lastName", LAST_NAME_FIRST_TEACHER)
                    .param("active", ON)
                    .param("departmentId", String.valueOf(DEPARTMENT_ID1)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(teacherMapperMock, times(1))
                .toTeacher(teacherDtoCaptor.capture());

            TeacherDto expectedTeacherDto = teacherDtoCaptor.getValue();
            assertThat(expectedTeacherDto.getFirstName(), is(NAME_FIRST_TEACHER));
            assertThat(expectedTeacherDto.getPatronymic(), is(PATRONYMIC_FIRST_TEACHER));
            assertThat(expectedTeacherDto.getLastName(), is(LAST_NAME_FIRST_TEACHER));
            assertThat(expectedTeacherDto.isActive(), is(true));
            assertThat(expectedTeacherDto.getDepartmentId(), is(DEPARTMENT_ID1));
        }
    }

    @Nested
    @DisplayName("test 'getTeacher' method")
    class GetTeacherTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should return JSON with expected teacherDto in body")
        void getRequestWithIdShouldReturnJsonWithTeacherDto() throws Exception {
            TeacherDto testTeacherDto = createTestTeacherDto();

            when(teacherServiceMock.getById(TEACHER_ID1)).thenReturn(testTeacherDto);

            mockMvc.perform(get(URI_TEACHERS_ID, TEACHER_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(TEACHER_ID1)),
                    jsonPath("$.firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$.patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$.lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$.fullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$.departmentName", is(NAME_FIRST_DEPARTMENT))
                );
        }
    }

    @Nested
    @DisplayName("test 'updateTeacher' method")
    class UpdateTeacherTest {
        @Test
        @DisplayName("when PUT request with all required parameters then should " +
            "call teacherDtoMapper and redirect")
        void putRequestWithAllParametersShouldCallTeacherMapperAndRedirect() throws Exception {
            mockMvc.perform(put(URI_TEACHERS_ID, TEACHER_ID1)
                    .param("firstName", NAME_FIRST_TEACHER)
                    .param("patronymic", PATRONYMIC_FIRST_TEACHER)
                    .param("lastName", LAST_NAME_FIRST_TEACHER)
                    .param("active", ON)
                    .param("departmentId", String.valueOf(DEPARTMENT_ID1)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(teacherMapperMock, times(1))
                .toTeacher(teacherDtoCaptor.capture());

            TeacherDto expectedTeacherDto = teacherDtoCaptor.getValue();
            assertThat(expectedTeacherDto.getId(), is(TEACHER_ID1));
            assertThat(expectedTeacherDto.getFirstName(), is(NAME_FIRST_TEACHER));
            assertThat(expectedTeacherDto.getPatronymic(), is(PATRONYMIC_FIRST_TEACHER));
            assertThat(expectedTeacherDto.getLastName(), is(LAST_NAME_FIRST_TEACHER));
            assertThat(expectedTeacherDto.isActive(), is(true));
            assertThat(expectedTeacherDto.getDepartmentId(), is(DEPARTMENT_ID1));
        }
    }

    @Nested
    @DisplayName("test 'deleteTeacher' method")
    class DeleteTeacherTest {
        @Test
        @DisplayName("when DELETE request with PathVariable id then should call teacherService.delete and redirect")
        void deleteRequestWithIdShouldCallTeacherServiceAndRedirect() throws Exception {
            int teacherId = 23;
            mockMvc.perform(delete(URI_TEACHERS_ID, teacherId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(teacherServiceMock, times(1)).delete(teacherId);
        }
    }

    @Nested
    @DisplayName("test 'getLessonsForTeacher' method")
    class GetLessonsForTeacherTest {
        @Test
        @DisplayName("when GET request with parameters id, start and end then " +
            "should return JSON with list lessonDtos in body ")
        void getRequestWithParametersShouldReturnJsonWithListLessonDtos() throws Exception {
            int teacherId = 46;
            List<LessonDto> testLessonDtos = createTestLessonDtos();

            when(lessonServiceMock.getAllForTeacherForTimePeriod(teacherId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(testLessonDtos);

            mockMvc.perform(get(URI_TEACHERS_ID_TIMETABLE, teacherId)
                .param("start", START_TIME_TIMETABLE_ISO)
                .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
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
