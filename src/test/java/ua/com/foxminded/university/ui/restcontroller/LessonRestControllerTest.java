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
import ua.com.foxminded.university.springconfig.TestMapperConfig;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_LESSONS;
import static ua.com.foxminded.university.ui.util.MappingConstants.API_LESSONS_ID;

@WebMvcTest(LessonRestController.class)
@Import(TestMapperConfig.class)
class LessonRestControllerTest {

    @Captor
    private ArgumentCaptor<Lesson> lessonCapture;

    @MockBean
    private LessonService lessonServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("test 'getLessonWithStudents' method")
    class GetLessonWithStudentsTest {

        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "JSON with lessonDto in body")
        void getRequestWithPathVariableIdThenShouldReturnJson() throws Exception {
            int lessonId = 5;
            Lesson lesson = createTestLesson(lessonId);

            when(lessonServiceMock.findById(lessonId)).thenReturn(lesson);

            mockMvc.perform(get(API_LESSONS_ID, lessonId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(lessonId)),
                    jsonPath("$.courseId", is(COURSE_ID1)),
                    jsonPath("$.courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$.teacherId", is(TEACHER_ID1)),
                    jsonPath("$.teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.roomId", is(ROOM_ID1)),
                    jsonPath("$.buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$.timeStart", is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$.timeEnd", is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$.students").isArray(),
                    jsonPath("$.students", hasSize(2))
                );
        }
    }

    @Nested
    @DisplayName("test 'createLesson' method")
    class CreateLessonTest {

        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call lessonService.add once")
        void postRequestWithParametersThenShouldCallServiceAndRedirect()
            throws Exception {
            String bodyRequest = "{" +
                "    \"courseId\": 21," +
                "    \"teacherId\": 78," +
                "    \"roomId\": 123," +
                "    \"timeStart\": \"2022-02-03T15:00\"," +
                "    \"timeEnd\": \"2022-02-03T16:30\"" +
                "}";

            Lesson lessonAfterCreating = createTestLesson(ID1);

            when(lessonServiceMock.create(any(Lesson.class)))
                .thenReturn(lessonAfterCreating);

            mockMvc.perform(post(API_LESSONS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(ID1)),
                    jsonPath("$.courseId", is(COURSE_ID1)),
                    jsonPath("$.courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$.teacherId", is(TEACHER_ID1)),
                    jsonPath("$.teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.roomId", is(ROOM_ID1)),
                    jsonPath("$.buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM))
                );

            verify(lessonServiceMock, times(1)).create(lessonCapture.capture());

            Lesson savingLesson = lessonCapture.getValue();
            LessonAssert.assertThat(savingLesson)
                .hasId(null)
                .hasTimeStart(LocalDateTime.of(2022, 2, 3, 15, 0))
                .hasTimeEnd(LocalDateTime.of(2022, 2, 3, 16, 30));
            CourseAssert.assertThat(savingLesson.getCourse())
                .hasId(21)
                .hasName(null);
            TeacherAssert.assertThat(savingLesson.getTeacher())
                .hasId(78)
                .hasFirstName(null)
                .hasLastName(null)
                .hasDepartment(null);
            RoomAssert.assertThat(savingLesson.getRoom())
                .hasId(123)
                .hasBuilding(null)
                .hasNumber(null);
        }
    }

//    @Nested
//    @DisplayName("test 'updateLesson' method")
//    class UpdateLessonTest {
//
//        @Test
//        @DisplayName("when PUT request with all required parameters then should " +
//            "call lessonService.update")
//        void putRequestWithAllParametersShouldCallLessonServiceAndRedirect() throws Exception {
//            int lessonId = 45;
//            LessonDto testLessonDto = LessonDto.builder()
//                .id(lessonId)
//                .courseId(COURSE_ID1)
//                .teacherId(TEACHER_ID1)
//                .roomId(ROOM_ID1)
//                .timeStart(DATE_START_FIRST_LESSON)
//                .timeEnd(DATE_END_FIRST_LESSON)
//                .build();
//
//            mockMvc.perform(put(URI_LESSONS_ID, lessonId)
//                    .param("courseId", String.valueOf(COURSE_ID1))
//                    .param("teacherId", String.valueOf(TEACHER_ID1))
//                    .param("roomId", String.valueOf(ROOM_ID1))
//                    .param("timeStart", TEXT_DATE_START_FIRST_LESSON)
//                    .param("timeEnd", TEXT_DATE_END_FIRST_LESSON))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(lessonServiceMock, times(1)).update(testLessonDto);
//        }
//    }
}