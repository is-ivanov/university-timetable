package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    public static final String URI_LESSONS = "/lessons";
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
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();
    }


    @Nested
    @DisplayName("test 'showLessons' method")
    class ShowLessonsTest {

        @Test
        @DisplayName("when GET request without parameters then should load into " +
            "model expected data")
        void withoutParameters() throws Exception {
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
        @DisplayName("when GET request with all parameters")
        void testWithAllParameters() throws Exception {
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
            List<TeacherDto> teacherDtos = createTestTeacherDtos(ID1);
            when(teacherServiceMock.getAllByDepartment(departmentId))
                .thenReturn(teachers);
            when(teacherDtoMapperMock.teachersToTeacherDtos(teachers))
                .thenReturn(teacherDtos);

            List<Department> departments = createTestDepartments();
            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(departments);

            List<Lesson> testLessons = createTestLessons();
            when(lessonServiceMock.getAllWithFilter(lessonFilter))
                .thenReturn(testLessons);

            List<LessonDto> testLessonDtos = createTestLessonDtos();
            when(lessonDtoMapperMock.lessonsToLessonDtos(testLessons))
                .thenReturn(testLessonDtos);

            mockMvc.perform(get("/lessons/filter")
                    .flashAttr("lessonFilter", lessonFilter)
                    .param("isShowInactiveTeachers", ON)
                    .param("isShowPastLessons", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("all_lessons"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("isShowPastLessons", true),
                    model().attribute("teachers", teacherDtos),
                    model().attribute("departments", departments)
                );

            verify(lessonServiceMock, times(1)).getAllWithFilter(lessonFilter);
            verify(teacherServiceMock, times(0)).getAllByFaculty(anyInt());
        }

        @Test
        @DisplayName("Test with facultyId and without departmentId")
        void testWithFacultyIdAndWithoutDepartmentId() throws Exception {
            LessonFilter lessonFilter = LessonFilter.builder()
                .facultyId(ID1)
                .build();
            List<Teacher> teachers = Collections.singletonList(new Teacher());
            List<TeacherDto> teacherDtos = Collections.singletonList(new TeacherDto());
            when(teacherServiceMock.getAllByFaculty(ID1)).thenReturn(teachers);
            when(teacherServiceMock.getAll()).thenReturn(null);
//            TODO
//            doReturn(teacherDtos).when(teacherServiceMock).convertListTeachersToDtos(teachers);
//            doReturn(null).when(teacherServiceMock).convertListTeachersToDtos(null);

            mockMvc.perform(post("/lesson/filter")
                    .flashAttr("lessonFilter", lessonFilter))
                .andDo(print())
                .andExpect(model().attribute("teachers", teacherDtos));
        }
    }

    @Nested
    @DisplayName("test getTeachersByDepartment")
    class TestGetTeachersByDepartment {

        @Test
        @DisplayName("with parameter departmentId = 0")
        void withParameterDepartmentId0() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            Teacher teacher2 = new Teacher();
            teacher2.setId(ID2);
            List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(ID1);
            TeacherDto teacherDto2 = new TeacherDto();
            teacherDto2.setId(ID2);
            List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

            when(teacherServiceMock.getAll()).thenReturn(teachers);
//            TODO
//            when(teacherServiceMock.convertListTeachersToDtos(teachers)).thenReturn(teacherDtos);

            mockMvc.perform(get("/lesson/department?departmentId=0"))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()").value(2),
                    jsonPath("$.[0].id").value(1),
                    jsonPath("$.[1].id").value(2)
                ));
        }

        @Test
        @DisplayName("with parameter departmentId = 1")
        void withParameterDepartmentId1() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            List<Teacher> teachers = Collections.singletonList(teacher1);

            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(ID1);
            List<TeacherDto> teacherDtos = Collections.singletonList(teacherDto1);

            when(teacherServiceMock.getAllByDepartment(ID1)).thenReturn(teachers);
            when(teacherServiceMock.getAll()).thenReturn(null);
//            TODO
//            doReturn(teacherDtos).when(teacherServiceMock).convertListTeachersToDtos(teachers);
//            doReturn(null).when(teacherServiceMock).convertListTeachersToDtos(null);

            mockMvc.perform(get("/lesson/department?departmentId=1"))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()").value(1),
                    jsonPath("$.[0].id").value(1)
                ));
        }
    }

    @Nested
    @DisplayName("test getTeachersByFaculty")
    class TestGetTeachersByFaculty {

        @Test
        @DisplayName("with parameters facultyId = 0")
        void withParametersFacultyId0() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            Teacher teacher2 = new Teacher();
            teacher2.setId(ID2);
            List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(ID1);
            TeacherDto teacherDto2 = new TeacherDto();
            teacherDto2.setId(ID2);
            List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

            when(teacherServiceMock.getAll()).thenReturn(teachers);
//            TODO
//            when(teacherServiceMock.convertListTeachersToDtos(teachers)).thenReturn(teacherDtos);

            mockMvc.perform(get("/lesson/faculty?facultyId=0"))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()").value(2),
                    jsonPath("$.[0].id").value(1),
                    jsonPath("$.[1].id").value(2)
                ));
        }

        @Test
        @DisplayName("with parameter facultyId = 1")
        void withParameterFacultyId1() throws Exception {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            List<Teacher> teachers = Collections.singletonList(teacher1);

            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(ID1);
            List<TeacherDto> teacherDtos = Collections.singletonList(teacherDto1);

            when(teacherServiceMock.getAllByFaculty(ID1)).thenReturn(teachers);
            when(teacherServiceMock.getAll()).thenReturn(null);
//            TODO
//            doReturn(teacherDtos).when(teacherServiceMock).convertListTeachersToDtos(teachers);
//            doReturn(null).when(teacherServiceMock).convertListTeachersToDtos(null);

            mockMvc.perform(get("/lesson/faculty?facultyId=1"))
                .andDo(print())
                .andExpect(matchAll(
                    content().contentType(MediaType.APPLICATION_JSON),
                    status().isOk(),
                    jsonPath("$.length()").value(1),
                    jsonPath("$.[0].id").value(1)
                ));
        }
    }

}