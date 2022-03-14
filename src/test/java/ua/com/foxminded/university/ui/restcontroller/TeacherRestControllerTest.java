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
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.restcontroller.RoomRestControllerTest.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.*;

@WebMvcTest(TeacherRestController.class)
@Import(TestMapperConfig.class)
class TeacherRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherServiceMock;

    @MockBean
    private LessonService lessonServiceMock;

    @Captor
    private ArgumentCaptor<Teacher> teacherCaptor;

    @Nested
    @DisplayName("test 'getTeachers' method")
    class GetTeachersTest {
        @Test
        @DisplayName("when GET request without parameters then should return " +
            "all teachers with status OK")
        void getRequestWithoutParameters() throws Exception {
            List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

            when(teacherServiceMock.findAll()).thenReturn(teachers);

            mockMvc.perform(get(API_TEACHERS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.teachers", hasSize(2)),
                    jsonPath("$._embedded.teachers[0].id",
                        is(TEACHER_ID1)),
                    jsonPath("$._embedded.teachers[0].firstName",
                        is(NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].lastName",
                        is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].patronymic",
                        is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].active",
                        is(true)),
                    jsonPath("$._embedded.teachers[0].fullName",
                        is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].departmentId",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.teachers[0].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.teachers[0]._links.self.href",
                        is(TEACHER1_SELF_LINK)),
                    jsonPath("$._embedded.teachers[1].id",
                        is(TEACHER_ID2)),
                    jsonPath("$._embedded.teachers[1].firstName",
                        is(NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].lastName",
                        is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].patronymic",
                        is(PATRONYMIC_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].active",
                        is(true)),
                    jsonPath("$._embedded.teachers[1].fullName",
                        is(FULL_NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].departmentId",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.teachers[1].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.teachers[1]._links.self.href",
                        is(TEACHER2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(TEACHERS_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createTeacher' method")
    class CreateTeacherTest {
        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call teacherService.create")
        void postRequestWithParameters_TeacherServiceCreate() throws Exception {
            String firstName = NAME_FIRST_TEACHER;
            String patronymic = PATRONYMIC_FIRST_TEACHER;
            String lastName = LAST_NAME_FIRST_TEACHER;
            boolean activeTeacher = true;
            int departmentId = 75;
            String jsonBodyRequest = "{" +
                "    \"firstName\": \"" + firstName + "\"," +
                "    \"patronymic\": \"" + patronymic + "\"," +
                "    \"lastName\": \"" + lastName + "\"," +
                "    \"departmentId\": " + departmentId + "," +
                "    \"active\": " + activeTeacher +
                "}";
            Teacher teacherAfterSaving = createTestTeacher();

            when(teacherServiceMock.create(any(Teacher.class)))
                .thenReturn(teacherAfterSaving);

            mockMvc.perform(post(API_TEACHERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(TEACHER_ID1)),
                    jsonPath("$.firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$.lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$.patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.fullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$.departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._links.self.href", is(TEACHER1_SELF_LINK))
                );

            verify(teacherServiceMock, times(1))
                .create(teacherCaptor.capture());

            Teacher teacherForSaving = teacherCaptor.getValue();
            TeacherAssert.assertThat(teacherForSaving)
                .hasId(null)
                .hasFirstName(firstName)
                .hasPatronymic(patronymic)
                .hasLastName(lastName)
                .isActive();
            DepartmentAssert.assertThat(teacherForSaving.getDepartment())
                .hasId(departmentId)
                .hasName(null);
        }
    }

    @Nested
    @DisplayName("test 'getTeacher' method")
    class GetTeacherTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should return " +
            "JSON with expected teacherDto in body")
        void getRequestWithIdShouldReturnJsonWithTeacherDto() throws Exception {
            Teacher teacher = createTestTeacher();

            when(teacherServiceMock.findById(TEACHER_ID1)).thenReturn(teacher);

            mockMvc.perform(get(API_TEACHERS_ID, TEACHER_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(TEACHER_ID1)),
                    jsonPath("$.firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$.patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$.lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$.fullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.departmentId", is(DEPARTMENT_ID1)),
                    jsonPath("$.departmentName", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._links.self.href", is(TEACHER1_SELF_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'updateTeacher' method")
    class UpdateTeacherTest {
        @Test
        @DisplayName("when PUT request with all required parameters then should " +
            "call teacherService.update")
        void putRequestWithAllParameters_CallTeacherServiceUpdate() throws Exception {
            int teacherId = 457;
            String firstName = NAME_FIRST_TEACHER;
            String patronymic = PATRONYMIC_FIRST_TEACHER;
            String lastName = LAST_NAME_FIRST_TEACHER;
            boolean activeTeacher = true;
            int departmentId = 75;
            String departmentName = "Any department name";
            String jsonBodyRequest = "{" +
                "    \"id\": " + teacherId + ", " +
                "    \"firstName\": \"" + firstName + "\"," +
                "    \"patronymic\": \"" + patronymic + "\"," +
                "    \"lastName\": \"" + lastName + "\"," +
                "    \"departmentId\": " + departmentId + "," +
                "    \"active\": " + activeTeacher +
                "}";
            Department department = Department.builder()
                .id(departmentId)
                .name(departmentName)
                .build();
            Teacher teacherAfterSaving = Teacher.builder()
                .id(teacherId)
                .firstName(firstName)
                .patronymic(patronymic)
                .lastName(lastName)
                .active(activeTeacher)
                .department(department)
                .build();
            String selfLink = "http://localhost/api/teachers/" + teacherId;


            when(teacherServiceMock.update(eq(teacherId), any(Teacher.class)))
                .thenReturn(teacherAfterSaving);

            mockMvc.perform(put(API_TEACHERS_ID, teacherId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(teacherId)),
                    jsonPath("$.firstName", is(firstName)),
                    jsonPath("$.lastName", is(lastName)),
                    jsonPath("$.patronymic", is(patronymic)),
                    jsonPath("$.active", is(activeTeacher)),
                    jsonPath("$.departmentId", is(departmentId)),
                    jsonPath("$.departmentName", is(departmentName)),
                    jsonPath("$._links.self.href", is(selfLink))
                );

            verify(teacherServiceMock, times(1))
                .update(eq(teacherId), teacherCaptor.capture());

            Teacher teacherForSaving = teacherCaptor.getValue();
            TeacherAssert.assertThat(teacherForSaving)
                .hasId(teacherId)
                .hasFirstName(firstName)
                .hasPatronymic(patronymic)
                .hasLastName(lastName)
                .isActive();
            DepartmentAssert.assertThat(teacherForSaving.getDepartment())
                .hasId(departmentId)
                .hasName(null);
        }
    }

    @Nested
    @DisplayName("test 'getLessonsForTeacher' method")
    class GetLessonsForTeacherTest {
        @Test
        @DisplayName("when GET request with parameters id, start and end then " +
            "should return JSON with list lessonDtos in body ")
        void getRequestWithParametersShouldReturnJsonWithListLessonDtos()
            throws Exception {
            int teacherId = 46;
            List<Lesson> lessons = createTestLessons();


            when(lessonServiceMock.getAllForTeacherForTimePeriod(teacherId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(lessons);


            mockMvc.perform(get(API_TEACHERS + ID_TIMETABLE, teacherId)
                    .param("start", START_TIME_TIMETABLE_ISO)
                    .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.lessons", hasSize(2)),
                    jsonPath("$._embedded.lessons[0].id", is(LESSON_ID1)),
                    jsonPath("$._embedded.lessons[0].courseId",
                        is(COURSE_ID1)),
                    jsonPath("$._embedded.lessons[0].courseName",
                        is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.lessons[0].teacherId",
                        is(TEACHER_ID1)),
                    jsonPath("$._embedded.lessons[0].teacherFullName",
                        is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.lessons[0].roomId", is(ROOM_ID1)),
                    jsonPath("$._embedded.lessons[0].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.lessons[0].timeStart",
                        is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$._embedded.lessons[0].timeEnd",
                        is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$._embedded.lessons[1].id", is(LESSON_ID2)),
                    jsonPath("$._embedded.lessons[1].courseId",
                        is(COURSE_ID1)),
                    jsonPath("$._embedded.lessons[1].courseName",
                        is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.lessons[1].teacherId",
                        is(TEACHER_ID1)),
                    jsonPath("$._embedded.lessons[1].teacherFullName",
                        is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.lessons[1].roomId", is(ROOM_ID1)),
                    jsonPath("$._embedded.lessons[1].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.lessons[1].timeStart",
                        is(TEXT_DATE_START_SECOND_LESSON)),
                    jsonPath("$._embedded.lessons[1].timeEnd",
                        is(TEXT_DATE_END_SECOND_LESSON))
                );
        }
    }

    @Nested
    @DisplayName("test 'getFreeTeachers' method")
    class GetFreeTeachersTest {
        @Test
        @DisplayName("when GET request with parameters time_start and time_end " +
            "then should return JSON with list teacherDtos in body")
        void getRequestWithParametersShouldReturnJsonListTeacherDtos() throws Exception {
            List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

            when(teacherServiceMock.getFreeTeachersOnLessonTime(DATE_FROM, DATE_TO))
                .thenReturn(teachers);

            mockMvc.perform(get(API_TEACHERS + FREE)
                    .param("time_start", TEXT_DATE_FROM)
                    .param("time_end", TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.teachers", hasSize(2)),
                    jsonPath("$._embedded.teachers[0].id", is(TEACHER_ID1)),
                    jsonPath("$._embedded.teachers[0].firstName",
                        is(NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].patronymic",
                        is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].lastName",
                        is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.teachers[0].departmentId",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.teachers[0].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.teachers[0].active", is(true)),
                    jsonPath("$._embedded.teachers[1].id", is(TEACHER_ID2)),
                    jsonPath("$._embedded.teachers[1].firstName",
                        is(NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].patronymic",
                        is(PATRONYMIC_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].lastName",
                        is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$._embedded.teachers[1].departmentId",
                        is(DEPARTMENT_ID1)),
                    jsonPath("$._embedded.teachers[1].departmentName",
                        is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$._embedded.teachers[1].active", is(true))
                );
        }
    }
}