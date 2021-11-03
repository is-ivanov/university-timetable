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
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.TimetableController.TIMETABLE_TEMPLATE;

@ExtendWith(MockitoExtension.class)
class TimetableControllerTest {

    public static final String URI_TIMETABLE_STUDENT = "/timetable/students/{id}";
    public static final String URI_TIMETABLE_TEACHER = "/timetable/teachers/{id}";
    public static final String URI_TIMETABLE_ROOM = "/timetable/rooms/{id}";

    private MockMvc mockMvc;

    @Mock
    private StudentService studentServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private RoomService roomServiceMock;

    @Mock
    private StudentDtoMapper studentMapperMock;

    @Mock
    private TeacherDtoMapper teacherMapperMock;

    @InjectMocks
    private TimetableController timetableController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(timetableController)
            .build();
    }

    @Nested
    @DisplayName("test 'showStudentTimetable' method")
    class ShowStudentTimetableTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should call " +
            "expected services and return template")
        void getRequestWithPathVariableIdShouldCallServicesAndReturnTemplate() throws Exception {
            int studentId = 23;

            Student testStudent = createTestStudent();
            StudentDto testStudentDto = createTestStudentDto();

            when(studentServiceMock.getById(studentId)).thenReturn(testStudent);
            when(studentMapperMock.studentToStudentDto(testStudent))
                .thenReturn(testStudentDto);

            mockMvc.perform(get(URI_TIMETABLE_STUDENT, studentId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name(TIMETABLE_TEMPLATE),
                    model().attribute("type", "students"),
                    model().attribute("object", testStudentDto)
                );
        }
    }

    @Nested
    @DisplayName("test 'showTeacherTimetable' method")
    class ShowTeacherTimetableTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should call " +
            "expected services and return template")
        void getRequestWithPathVariableIdShouldCallServicesAndReturnTemplate() throws Exception {
            int teacherId = 89;

            Teacher testTeacher = createTestTeacher();
            TeacherDto testTeacherDto = createTestTeacherDto();

            when(teacherServiceMock.getById(teacherId)).thenReturn(testTeacher);
            when(teacherMapperMock.teacherToTeacherDto(testTeacher))
                .thenReturn(testTeacherDto);

            mockMvc.perform(get(URI_TIMETABLE_TEACHER, teacherId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name(TIMETABLE_TEMPLATE),
                    model().attribute("type", "teachers"),
                    model().attribute("object", testTeacherDto)
                );
        }
    }

    @Nested
    @DisplayName("test 'showRoomTimetable' method")
    class ShowRoomTimetableTest {
        @Test
        @DisplayName("when GET request with PathVariable id then should call " +
            "expected services and return template")
        void getRequestWithPathVariableIdShouldCallServicesAndReturnTemplate() throws Exception {
            int roomId = 3;

            Room testRoom = createTestRoom();

            when(roomServiceMock.getById(roomId)).thenReturn(testRoom);

            mockMvc.perform(get(URI_TIMETABLE_ROOM, roomId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name(TIMETABLE_TEMPLATE),
                    model().attribute("type", "rooms"),
                    model().attribute("object", testRoom)
                );
        }
    }
}