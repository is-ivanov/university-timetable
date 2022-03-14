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
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
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

@WebMvcTest(StudentRestController.class)
@Import(TestMapperConfig.class)
class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentServiceMock;

    @MockBean
    private LessonService lessonServiceMock;

    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    @Nested
    @DisplayName("test 'getStudents' method")
    class GetStudentsTest {
        @Test
        @DisplayName("when GET request without parameters then should return " +
            "all students with status OK")
        void getRequestWithoutParameters() throws Exception {
            List<Student> students = createTestStudents();

            when(studentServiceMock.findAll()).thenReturn(students);

            mockMvc.perform(get(API_STUDENTS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.students", hasSize(2)),
                    jsonPath("$._embedded.students[0].id",
                        is(STUDENT_ID1)),
                    jsonPath("$._embedded.students[0].firstName",
                        is(NAME_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].lastName",
                        is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].patronymic",
                        is(PATRONYMIC_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].active",
                        is(true)),
                    jsonPath("$._embedded.students[0].fullName",
                        is(FULL_NAME_FIRST_STUDENT)),
                    jsonPath("$._embedded.students[0].groupId",
                        is(GROUP_ID1)),
                    jsonPath("$._embedded.students[0].groupName",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.students[0]._links.self.href",
                        is(STUDENT1_SELF_LINK)),
                    jsonPath("$._embedded.students[1].id",
                        is(STUDENT_ID2)),
                    jsonPath("$._embedded.students[1].firstName",
                        is(NAME_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].lastName",
                        is(LAST_NAME_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].patronymic",
                        is(PATRONYMIC_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].active",
                        is(true)),
                    jsonPath("$._embedded.students[1].fullName",
                        is(FULL_NAME_SECOND_STUDENT)),
                    jsonPath("$._embedded.students[1].groupId",
                        is(GROUP_ID1)),
                    jsonPath("$._embedded.students[1].groupName",
                        is(NAME_FIRST_GROUP)),
                    jsonPath("$._embedded.students[1]._links.self.href",
                        is(STUDENT2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(STUDENTS_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createStudent' method")
    class CreateStudentTest {
        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call studentService.create once")
        void postRequestWithParametersShouldCallStudentService() throws Exception {
            String firstName = NAME_FIRST_STUDENT;
            String patronymic = PATRONYMIC_FIRST_STUDENT;
            String lastName = LAST_NAME_FIRST_STUDENT;
            boolean activeStudent = true;
            int groupId = 23;
            String jsonBodyRequest = "{" +
                "    \"firstName\": \"" + firstName + "\"," +
                "    \"patronymic\": \"" + patronymic + "\"," +
                "    \"lastName\": \"" + lastName + "\"," +
                "    \"groupId\": " + groupId + "," +
                "    \"active\": " + activeStudent +
                "}";
            Student studentAfterSaving = createTestStudent();

            when(studentServiceMock.create(any(Student.class)))
                .thenReturn(studentAfterSaving);

            mockMvc.perform(post(API_STUDENTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(STUDENT_ID1)),
                    jsonPath("$.firstName", is(NAME_FIRST_STUDENT)),
                    jsonPath("$.lastName", is(LAST_NAME_FIRST_STUDENT)),
                    jsonPath("$.patronymic", is(PATRONYMIC_FIRST_STUDENT)),
                    jsonPath("$.active", is(true)),
                    jsonPath("$.fullName", is(FULL_NAME_FIRST_STUDENT)),
                    jsonPath("$.groupId", is(GROUP_ID1)),
                    jsonPath("$.groupName", is(NAME_FIRST_GROUP)),
                    jsonPath("$._links.self.href", is(STUDENT1_SELF_LINK))
                );

            verify(studentServiceMock, times(1))
                .create(studentCaptor.capture());

            Student studentForSaving = studentCaptor.getValue();
            StudentAssert.assertThat(studentForSaving)
                .hasId(null)
                .hasFirstName(firstName)
                .hasPatronymic(patronymic)
                .hasLastName(lastName)
                .isActive();
            GroupAssert.assertThat(studentForSaving.getGroup())
                .hasId(groupId)
                .hasName(null);
        }
    }

    @Nested
    @DisplayName("test 'getStudent' method")
    class GetStudentTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should return " +
            "JSON with expected studentDto in body")
        void getRequestWithIdShouldReturnJsonWithStudentDto() throws Exception {
            Student student = createTestStudent();

            when(studentServiceMock.findById(STUDENT_ID1)).thenReturn(student);

            mockMvc.perform(get(API_STUDENTS_ID, STUDENT_ID1))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
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
            "call studentService.update")
        void putRequestWithParametersShouldCallStudentService() throws Exception {
            int studentId = 12;
            String firstName = "John";
            String patronymic = "";
            String lastName = "Wayne";
            boolean activeStudent = false;
            int groupId = 23;
            String groupName = "Any group";
            String jsonBodyRequest = "{" +
                "    \"id\":" + studentId + "," +
                "    \"firstName\": \"" + firstName + "\"," +
                "    \"patronymic\": \"" + patronymic + "\"," +
                "    \"lastName\": \"" + lastName + "\"," +
                "    \"groupId\": " + groupId + "," +
                "    \"active\": " + activeStudent +
                "}";
            Group group = Group.builder()
                .id(groupId)
                .name(groupName)
                .build();
            Student studentAfterSaving = Student.builder()
                .id(studentId)
                .firstName(firstName)
                .patronymic(patronymic)
                .lastName(lastName)
                .active(activeStudent)
                .group(group)
                .build();
            String selfLink = "http://localhost/api/students/" + studentId;


            when(studentServiceMock.update(eq(studentId), any(Student.class)))
                .thenReturn(studentAfterSaving);


            mockMvc.perform(put(API_STUDENTS_ID, studentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(studentId)),
                    jsonPath("$.firstName", is(firstName)),
                    jsonPath("$.lastName", is(lastName)),
                    jsonPath("$.patronymic", is(patronymic)),
                    jsonPath("$.active", is(activeStudent)),
                    jsonPath("$.groupId", is(groupId)),
                    jsonPath("$.groupName", is(groupName)),
                    jsonPath("$._links.self.href", is(selfLink))
                );

            verify(studentServiceMock, times(1))
                .update(eq(studentId), studentCaptor.capture());

            Student studentForSaving = studentCaptor.getValue();
            StudentAssert.assertThat(studentForSaving)
                .hasId(studentId)
                .hasFirstName(firstName)
                .hasPatronymic(patronymic)
                .hasLastName(lastName)
                .isNotActive();
            GroupAssert.assertThat(studentForSaving.getGroup())
                .hasId(groupId)
                .hasName(null);
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
            List<Lesson> lessons = createTestLessons();

            when(lessonServiceMock.getAllForStudentForTimePeriod(studentId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(lessons);

            mockMvc.perform(get(API_STUDENTS + ID_TIMETABLE, studentId)
                    .param("start", START_TIME_TIMETABLE_ISO)
                    .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
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
                    jsonPath("$._embedded.lessons[1].courseId", is(COURSE_ID1)),
                    jsonPath("$._embedded.lessons[1].courseName",
                        is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.lessons[1].teacherId", is(TEACHER_ID1)),
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
}