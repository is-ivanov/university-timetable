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
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    public static final String URI_LESSONS = "/lessons";
    public static final String URI_LESSONS_FILTER = "/lessons/filter";
    public static final String URI_LESSONS_ID_STUDENTS = "/lessons/{id}/students";
    private MockMvc mockMvc;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private RoomService roomServiceMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private LessonDtoMapper lessonDtoMapperMock;

    @Mock
    private TeacherDtoMapper teacherDtoMapperMock;

    @InjectMocks
    private LessonController lessonController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(lessonController)
            .build();
    }


    @Nested
    @DisplayName("test 'showLessons' method")
    class ShowLessonsTest {

        @Test
        @DisplayName("when GET request without parameters then should load into " +
            "model expected data")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();
            List<Department> departments = createTestDepartments();
            List<Teacher> teachers = createTestTeachers(ID1);
            List<TeacherDto> teacherDtos = createTestTeacherDtos(ID1);
            List<Course> courses = createTestCourses();
            List<Room> rooms = createTestRooms();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.getAll()).thenReturn(departments);
            when(teacherServiceMock.getAll()).thenReturn(teachers);
            when(teacherDtoMapperMock.teachersToTeacherDtos(teachers)).thenReturn(teacherDtos);
            when(courseServiceMock.getAll()).thenReturn(courses);
            when(roomServiceMock.getAll()).thenReturn(rooms);

            mockMvc.perform(get(URI_LESSONS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("all_lessons"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "isShowPastLessons", "lessons"),
                    model().attribute("lessonFilter", new LessonFilter()),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("teachers", teacherDtos),
                    model().attribute("courses", courses),
                    model().attribute("rooms", rooms)
                );
        }
    }

    @Nested
    @DisplayName("test 'showFilteredLesson' method")
    class ShowFilteredLessonTest {

        @Test
        @DisplayName("when GET request with all parameters then should call " +
            "expected methods in services")
        void getRequestWithAllParameters() throws Exception {
            int departmentId = 4;
            int facultyId = 2;
            int teacherId = 1;
            int courseId = 5;
            int roomId = 10;
            LocalDateTime dateFrom = LocalDateTime.of(2021, 8, 10, 8, 0);
            LocalDateTime dateTo = LocalDateTime.of(2021, 9, 15, 23, 0);
            LessonFilter lessonFilter = LessonFilter.builder()
                .facultyId(facultyId)
                .departmentId(departmentId)
                .teacherId(teacherId)
                .courseId(courseId)
                .roomId(roomId)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();

            List<Teacher> teachers = createTestTeachers(ID1);
            when(teacherServiceMock.getAllByDepartment(departmentId))
                .thenReturn(teachers);

            List<Department> departments = createTestDepartments();
            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(departments);

            List<Lesson> testLessons = createTestLessons();
            when(lessonServiceMock.getAllWithFilter(lessonFilter))
                .thenReturn(testLessons);

            List<LessonDto> testLessonDtos = createTestLessonDtos();
            when(lessonDtoMapperMock.lessonsToLessonDtos(testLessons))
                .thenReturn(testLessonDtos);

            mockMvc.perform(get(URI_LESSONS_FILTER)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("departmentId", String.valueOf(departmentId))
                    .param("teacherId", String.valueOf(teacherId))
                    .param("courseId", String.valueOf(courseId))
                    .param("roomId", String.valueOf(roomId))
                    .param("dateFrom", "2021-08-10 08:00")
                    .param("dateTo", "2021-09-15 23:00")
                    .param("isShowInactiveTeachers", ON)
                    .param("isShowPastLessons", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("all_lessons"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("isShowPastLessons", true),
                    model().attribute("departments", departments)
                );

            verify(lessonServiceMock, times(1)).getAllWithFilter(lessonFilter);
            verify(teacherServiceMock, times(0)).getAllByFaculty(anyInt());
            verify(teacherDtoMapperMock, times(1)).teachersToTeacherDtos(teachers);
        }

        @Test
        @DisplayName("when GET request with parameter facultyId and without " +
            "departmentId then should call teacherService.getAllByDepartment")
        void getRequestWithFacultyIdAndWithoutDepartmentId() throws Exception {
            int facultyId = 3;
            LessonFilter lessonFilter = LessonFilter.builder()
                .facultyId(facultyId)
                .build();

            mockMvc.perform(get(URI_LESSONS_FILTER)
                    .param("facultyId", String.valueOf(facultyId)))
                .andDo(print())
                .andExpect(status().isOk());

            verify(lessonServiceMock, times(1)).getAllWithFilter(lessonFilter);
            verify(teacherServiceMock, times(1)).getAllByFaculty(facultyId);
        }

        @Test
        @DisplayName("when GET request without parameters departmentId and facultyId " +
            "then should call departmentService.getAll and teacherService.getAll")
        void getRequestWithoutParametersFacultyIdAndDepartmentId() throws Exception {
            mockMvc.perform(get(URI_LESSONS_FILTER))
                .andDo(print())
                .andExpect(status().isOk());
            verify(departmentServiceMock, times(2)).getAll();
            verify(teacherServiceMock, times(2)).getAll();
        }

    }

    @Nested
    @DisplayName("test 'getLessonWithStudents' method")
    class GetLessonWithStudentsTest {

        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "JSON with lessonDto in body")
        void whenGetRequestWithPathVariableIdThenShouldReturnJson() throws Exception {
            int lessonId = 5;
            Lesson testLesson = createTestLesson(lessonId);
            LessonDto testLessonDto = createTestLessonDto(lessonId);
            when(lessonServiceMock.getById(lessonId)).thenReturn(testLesson);
            when(lessonDtoMapperMock.lessonToLessonDto(testLesson))
                .thenReturn(testLessonDto);
            mockMvc.perform(get("/lessons/{id}", lessonId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(lessonId)),
                    jsonPath("$.courseId", is(ID1)),
                    jsonPath("$.courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$.teacherId", is(ID1)),
                    jsonPath("$.teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$.roomId", is(ID1)),
                    jsonPath("$.buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$.timeStart", is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$.timeEnd", is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$.students").isArray(),
                    jsonPath("$.students", hasSize(2))
                );
        }
    }

    @Nested
    @DisplayName("test 'showLessonWithStudents' method")
    class ShowLessonWithStudentsTest {

        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "view 'lesson' and call expected services")
        void whenGetRequestWithPathVariableIdThenShouldReturnViewLesson() throws Exception {
            int lessonId = 3;

            Lesson testLesson = createTestLesson(lessonId);
            when(lessonServiceMock.getById(lessonId)).thenReturn(testLesson);

            LessonDto testLessonDto = createTestLessonDto(lessonId);
            when(lessonDtoMapperMock.lessonToLessonDto(testLesson))
                .thenReturn(testLessonDto);

            List<Teacher> testTeachers = createTestTeachers(ID1);
            when(teacherServiceMock.getFreeTeachersOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(testTeachers);

            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(ID1);
            when(teacherDtoMapperMock.teachersToTeacherDtos(any())).thenReturn(testTeacherDtos);

            List<Room> testRooms = createTestRooms();
            when(roomServiceMock.getFreeRoomsOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(testRooms);

            List<Group> testGroups = createTestGroups();
            when(groupServiceMock.getFreeGroupsOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(testGroups);

            mockMvc.perform(get(URI_LESSONS_ID_STUDENTS, lessonId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attributeExists("lesson", "teachers", "rooms",
                        "groups"),
                    model().attribute("lesson", testLessonDto),
                    model().attribute("teachers", testTeacherDtos),
                    model().attribute("groups", testGroups)
                );
        }
    }

    @Nested
    @DisplayName("test 'createLesson' method")
    class CreateLessonTest {

        @Test
        @DisplayName("when POST request with all required parameters then should " +
            "call lessonService.add once and redirect")
        void whenPostRequestWithParametersThenShouldCallServiceAndRedirect() throws Exception {
            int courseId = 14;
            int teacherId = 5;
            int roomId = 7;
            LocalDateTime timeStart = LocalDateTime.of(2021, 10, 14, 15, 45);
            LocalDateTime timeEnd = LocalDateTime.of(2021, 10, 14, 17, 15);

            LessonDto lessonDto = LessonDto.builder()
                .courseId(courseId)
                .teacherId(teacherId)
                .roomId(roomId)
                .timeStart(timeStart)
                .timeEnd(timeEnd)
                .build();

            Lesson lesson = createTestLesson(ID1);

            when(lessonDtoMapperMock.lessonDtoToLesson(lessonDto)).thenReturn(lesson);

            mockMvc.perform(post(URI_LESSONS)
                    .param("course.id", String.valueOf(courseId))
                    .param("teacherDto.id", String.valueOf(teacherId))
                    .param("room.id", String.valueOf(roomId))
                    .param("timeStart", "2021-10-14 15:45")
                    .param("timeEnd", "2021-10-14 17:15"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(lessonDtoMapperMock, times(1))
                .lessonDtoToLesson(lessonDto);
            verify(lessonServiceMock, times(1)).add(lesson);
        }
    }


}