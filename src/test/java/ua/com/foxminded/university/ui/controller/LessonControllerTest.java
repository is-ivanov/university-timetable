package ua.com.foxminded.university.ui.controller;

import org.assertj.core.util.Arrays;
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
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;
import ua.com.foxminded.university.ui.restcontroller.LessonRestController;

import java.util.List;

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
    private LessonRestController restControllerMock;

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
            List<Teacher> teacherDtos = createTestTeachers(ID1);
            List<Course> courses = createTestCourses();
            List<Room> rooms = createTestRooms();


            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.findAll()).thenReturn(departments);
            when(teacherServiceMock.findAll()).thenReturn(teacherDtos);
            when(courseServiceMock.findAll()).thenReturn(courses);
            when(roomServiceMock.findAll()).thenReturn(rooms);

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

            List<Lesson> lessons = createTestLessons();
            when(lessonServiceMock.getAllWithFilter(lessonFilter))
                .thenReturn(lessons);


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
        }

        @Test
        @DisplayName("when GET request with parameter facultyId and without " +
            "departmentId then should call teacherService.getAllByFaculty")
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
            "then should call departmentService.getAll and teacherService.findAll")
        void getRequestWithoutParametersFacultyIdAndDepartmentId() throws Exception {
            mockMvc.perform(get(URI_LESSONS_FILTER))
                .andDo(print())
                .andExpect(status().isOk());
            verify(departmentServiceMock, times(2)).findAll();
            verify(teacherServiceMock, times(2)).findAll();
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

            Lesson lesson = createTestLesson(lessonId);
            List<Teacher> teachers = createTestTeachers(ID1);
            List<Room> rooms = createTestRooms();
            List<Group> groups = createTestGroups(FACULTY_ID1);
            LessonDto lessonDto = createTestLessonDto(ID2);

            when(lessonServiceMock.findById(lessonId)).thenReturn(lesson);
            when(teacherServiceMock.getFreeTeachersOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(teachers);
            when(roomServiceMock.getFreeRoomsOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(rooms);
            when(groupServiceMock.getFreeGroupsOnLessonTime(DATE_START_FIRST_LESSON,
                DATE_END_FIRST_LESSON)).thenReturn(groups);
            when(lessonDtoMapperMock.toDto(lesson)).thenReturn(lessonDto);



            mockMvc.perform(get(URI_LESSONS_ID_STUDENTS, lessonId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attributeExists("lesson", "teachers", "rooms",
                        "groups"),
                    model().attribute("lesson", lessonDto),
                    model().attribute("teachers", teachers),
                    model().attribute("groups", groups)
                );
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

            verify(restControllerMock, times(1))
                .addStudentToLesson(lessonId, studentId);
            verifyNoMoreInteractions(restControllerMock);
        }
    }

    @Nested
    @DisplayName("test 'addStudentsFromGroupToLesson' method")
    class AddStudentsFromGroupToLessonTest {

        @Test
        @DisplayName("when POST request with parameters groupId and id then should " +
            "call lessonService.addStudentsFromGroupToLesson and redirect")
        void postRequestWithParametersShouldCallLessonServiceAndRedirect() throws Exception {
            int lessonId = 45;
            int groupId = 78;

            mockMvc.perform(post(URI_LESSONS_ID_GROUPS, lessonId)
                    .param("groupId", String.valueOf(groupId)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(restControllerMock, times(1))
                .addStudentsFromGroupToLesson(lessonId, groupId);
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
            verify(restControllerMock, times(1))
                .removeStudentFromLesson(lessonId, Arrays.array(studentId));
            verifyNoMoreInteractions(restControllerMock);
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
            Integer[] studentIds = new Integer[] {studentId1, studentId2, studentId3};
            mockMvc.perform(delete(URI_LESSONS_ID_STUDENTS, lessonId)
                    .param("studentIds", String.valueOf(studentId1),
                        String.valueOf(studentId2), String.valueOf(studentId3)))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(restControllerMock, times(1))
                .removeStudentFromLesson(lessonId, studentIds);
            verifyNoMoreInteractions(restControllerMock);
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