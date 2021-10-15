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
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    public static final String URI_LESSONS = "/lessons";
    public static final String URI_LESSONS_ID = "/lessons/{lessonId}";
    public static final String URI_LESSONS_FILTER = "/lessons/filter";
    public static final String URI_LESSONS_ID_STUDENTS = "/lessons/{lessonId}/students";
    public static final String URI_LESSONS_ID_GROUPS = "/lessons/{lessonId}/groups";
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
            LessonFilter lessonFilter = createTestLessonFilter();

            List<Teacher> teachers = createTestTeachers(FACULTY_ID1);
            when(teacherServiceMock.getAllByDepartment(DEPARTMENT_ID1))
                .thenReturn(teachers);

            List<Department> departments = createTestDepartments();
            when(departmentServiceMock.getAllByFaculty(FACULTY_ID1))
                .thenReturn(departments);

            List<Lesson> testLessons = createTestLessons();
            when(lessonServiceMock.getAllWithFilter(lessonFilter))
                .thenReturn(testLessons);

            List<LessonDto> testLessonDtos = createTestLessonDtos();
            when(lessonDtoMapperMock.lessonsToLessonDtos(testLessons))
                .thenReturn(testLessonDtos);

            mockMvc.perform(get(URI_LESSONS_FILTER)
                    .param("facultyId", String.valueOf(FACULTY_ID1))
                    .param("departmentId", String.valueOf(DEPARTMENT_ID1))
                    .param("teacherId", String.valueOf(TEACHER_ID1))
                    .param("courseId", String.valueOf(COURSE_ID1))
                    .param("roomId", String.valueOf(ROOM_ID1))
                    .param("dateFrom", TEXT_DATE_FROM)
                    .param("dateTo", TEXT_DATE_TO)
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
        void getRequestWithPathVariableIdThenShouldReturnJson() throws Exception {
            int lessonId = 5;
            Lesson testLesson = createTestLesson(lessonId);
            LessonDto testLessonDto = createTestLessonDto(lessonId);
            when(lessonServiceMock.getById(lessonId)).thenReturn(testLesson);
            when(lessonDtoMapperMock.lessonToLessonDto(testLesson))
                .thenReturn(testLessonDto);
            mockMvc.perform(get(URI_LESSONS_ID, lessonId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
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
    @DisplayName("test 'showLessonWithStudents' method")
    class ShowLessonWithStudentsTest {

        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "view 'lesson' and call expected services")
        void getRequestWithPathVariableIdThenShouldReturnViewLesson() throws Exception {
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
        void postRequestWithParametersThenShouldCallServiceAndRedirect() throws Exception {
            LessonDto lessonDto = LessonDto.builder()
                .courseId(COURSE_ID1)
                .teacherId(TEACHER_ID1)
                .roomId(ROOM_ID1)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            Lesson lesson = createTestLesson(ID1);

            when(lessonDtoMapperMock.lessonDtoToLesson(lessonDto)).thenReturn(lesson);

            mockMvc.perform(post(URI_LESSONS)
                    .param("courseId", String.valueOf(COURSE_ID1))
                    .param("teacherId", String.valueOf(TEACHER_ID1))
                    .param("roomId", String.valueOf(ROOM_ID1))
                    .param("timeStart", TEXT_DATE_START_FIRST_LESSON)
                    .param("timeEnd", TEXT_DATE_END_FIRST_LESSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(lessonDtoMapperMock, times(1))
                .lessonDtoToLesson(lessonDto);
            verify(lessonServiceMock, times(1)).add(lesson);
        }
    }

    @Nested
    @DisplayName("test 'addStudentToLesson' method")
    class AddStudentToLessonTest {

        @Test
        @DisplayName("when POST request with parameters studentId and id then " +
            "should call lessonService.addStudentToLesson and redirect")
        void postRequestWithParametersShouldCallLessonServiceAndRedirect() throws Exception {
            int lessonId = 12;
            int studentId = 78;

            mockMvc.perform(post(URI_LESSONS_ID_STUDENTS, lessonId)
                    .param("studentId", String.valueOf(studentId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(lessonServiceMock, times(1))
                .addStudentToLesson(lessonId, studentId);
            verifyNoMoreInteractions(lessonServiceMock);
        }
    }

    @Nested
    @DisplayName("test 'addStudentsFromGroupToLesson' method")
    class AddStudentsFromGroupToLessonTest {

        @Test
        @DisplayName("when POST request with parameters groupId and id then should call lessonService.addStudentsFromGroupToLesson and redirect")
        void postRequestWithParametersShouldCallLessonServiceAndRedirect() throws Exception {
            int lessonId = 45;
            int groupId = 78;

            mockMvc.perform(post(URI_LESSONS_ID_GROUPS, lessonId)
                    .param("groupId", String.valueOf(groupId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(lessonServiceMock, times(1))
                .addStudentsFromGroupToLesson(groupId, lessonId);
        }
    }

    @Nested
    @DisplayName("test 'updateLesson' method")
    class UpdateLessonTest {

        @Test
        @DisplayName("when PUT request with all required parameters then should call lessonService.update and redirect")
        void putRequestWithAllParametersShouldCallLessonServiceAndRedirect() throws Exception {
            int lessonId = 45;
            LessonDto testLessonDto = LessonDto.builder()
                .id(lessonId)
                .courseId(COURSE_ID1)
                .teacherId(TEACHER_ID1)
                .roomId(ROOM_ID1)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            Lesson testLesson = createTestLesson(lessonId);
            when(lessonDtoMapperMock.lessonDtoToLesson(testLessonDto)).thenReturn(testLesson);

            mockMvc.perform(put(URI_LESSONS_ID, lessonId)
                    .param("courseId", String.valueOf(COURSE_ID1))
                    .param("teacherId", String.valueOf(TEACHER_ID1))
                    .param("roomId", String.valueOf(ROOM_ID1))
                    .param("timeStart", TEXT_DATE_START_FIRST_LESSON)
                    .param("timeEnd", TEXT_DATE_END_FIRST_LESSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(lessonServiceMock, times(1)).update(testLesson);
        }
    }

    @Nested
    @DisplayName("test 'removeStudentFromLesson' method")
    class RemoveStudentFromLessonTest {

        @Test
        @DisplayName("when DELETE request with one parameter studentIds then should " +
            "call lessonService.removeStudentFromLesson and redirect")
        void deleteRequestWithOneStudentIdShouldCallLessonService() throws Exception {
            int lessonId = 13;
            int studentId = 5;
            mockMvc.perform(delete(URI_LESSONS_ID_STUDENTS, lessonId)
                    .param("studentIds", String.valueOf(studentId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(lessonServiceMock, times(1))
                .removeStudentFromLesson(lessonId, studentId);
            verifyNoMoreInteractions(lessonServiceMock);
        }

        @Test
        @DisplayName("when DELETE request with array studentIds then should call " +
            "lessonService.removeStudentsFromLesson and redirect")
        void deleteRequestWithArrayStudentIdsShouldCallLessonServiceAndRedirect()
            throws Exception {
            int lessonId = 78;
            int studentId1 = 15;
            int studentId2 = 42;
            int studentId3 = 1;
            int[] studentIds = new int[] {studentId1, studentId2, studentId3};
            mockMvc.perform(delete(URI_LESSONS_ID_STUDENTS, lessonId)
                    .param("studentIds", String.valueOf(studentId1),
                        String.valueOf(studentId2), String.valueOf(studentId3)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(lessonServiceMock, times(1))
                .removeStudentsFromLesson(lessonId, studentIds);
            verifyNoMoreInteractions(lessonServiceMock);
        }
    }

    @Nested
    @DisplayName("test 'deleteLesson' method")
    class DeleteLessonTest {

        @Test
        @DisplayName("when DELETE request with @PathVariable lessonId then should " +
            "call lessonService.delete and redirect")
        void deleteRequestWithLessonIdShouldCallService() throws Exception {
            int lessonId = 12;
            mockMvc.perform(delete(URI_LESSONS_ID, lessonId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(lessonServiceMock, times(1)).delete(lessonId);
        }
    }
}