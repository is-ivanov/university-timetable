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
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LessonControllerTest {

    private static final int ID1 = 1;
    private static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "faculty1";
    public static final String NAME_SECOND_FACULTY = "faculty2";
    public static final String NAME_FIRST_DEPARTMENT = "dep1";
    public static final String NAME_SECOND_DEPARTMENT = "dep2";

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

    @InjectMocks
    private LessonController lessonController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Nested
    @DisplayName("test showLessons")
    class TestShowLessons {

        @Test
        @DisplayName("without parameters")
        void withoutParameters() throws Exception {
            Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
            Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
            List<Faculty> faculties = Arrays.asList(faculty1, faculty2);

            Department department1 = new Department(ID1, NAME_FIRST_DEPARTMENT,
                faculty1);
            Department department2 = new Department(ID2, NAME_SECOND_DEPARTMENT,
                faculty2);
            List<Department> departments = Arrays.asList(department1, department2);

            TeacherDto teacherDto1 = new TeacherDto();
            List<TeacherDto> teachers = Collections.singletonList(teacherDto1);

            Course course = new Course();
            List<Course> courses = Collections.singletonList(course);

            Room room = new Room(ID1, "building", "212");
            List<Room> rooms = Collections.singletonList(room);

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.getAll()).thenReturn(departments);
            //TODO
            //            when(teacherServiceMock.convertListTeachersToDtos(any())).thenReturn(teachers);
            when(courseServiceMock.getAll()).thenReturn(courses);
            when(roomServiceMock.getAll()).thenReturn(rooms);

            mockMvc.perform(get("/lesson"))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "isShowPastLessons", "lessons"),
                    model().attribute("lessonFilter", new LessonFilter()),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("teachers", teachers),
                    model().attribute("courses", courses),
                    model().attribute("rooms", rooms)
                ));
        }
    }

    @Nested
    @DisplayName("test showFilteredLesson")
    class TestShowFilteredLesson {

        @Test
        @DisplayName("without parameters")
        void withoutParameters() throws Exception {
            Department department1 = new Department();
            department1.setId(ID2);
            List<Department> departments = Collections.singletonList(department1);

            TeacherDto teacherDto1 = new TeacherDto();
            teacherDto1.setId(ID1);
            List<TeacherDto> teachers = Collections.singletonList(teacherDto1);

            LessonDto lessonDto1 = LessonDto.builder()
                .id(ID1)
                .build();
            List<LessonDto> lessonDtos = Collections.singletonList(lessonDto1);
//TODO
//            when(teacherServiceMock.convertListTeachersToDtos(any())).thenReturn(teachers);
            when(departmentServiceMock.getAll()).thenReturn(departments);
//            when(lessonServiceMock.convertListLessonsToDtos(any())).thenReturn(lessonDtos);

            mockMvc.perform(post("/lesson/filter"))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "isShowPastLessons"),
                    model().attribute("teachers", teachers),
                    model().attribute("departments", departments),
                    model().attribute("lessons", lessonDtos)
                ));
        }

        @Test
        @DisplayName("Test with all parameters")
        void testWithAllParameters() throws Exception {

            LocalDateTime dateFrom = LocalDateTime.of(2021, 8, 10, 8, 0);
            LocalDateTime dateTo = LocalDateTime.of(2021, 9, 15, 23, 0);
            LessonFilter lessonFilter = LessonFilter.builder()
                .facultyId(1)
                .departmentId(2)
                .teacherId(1)
                .courseId(5)
                .roomId(10)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();

            List<Teacher> teachers = Collections.singletonList(new Teacher());
            List<TeacherDto> teacherDtos = Collections.singletonList(new TeacherDto());
            when(teacherServiceMock.getAllByDepartment(ID2)).thenReturn(teachers);
            when(teacherServiceMock.getAll()).thenReturn(null);
//            TODO
//            doReturn(teacherDtos).when(teacherServiceMock).convertListTeachersToDtos(teachers);
//            doReturn(null).when(teacherServiceMock).convertListTeachersToDtos(null);

            List<Department> departments = Collections.singletonList(new Department());
            when(departmentServiceMock.getAllByFaculty(ID1)).thenReturn(departments);

            mockMvc.perform(post("/lesson/filter")
                    .flashAttr("lessonFilter", lessonFilter)
                    .param("isShowInactiveTeachers", "on")
                    .param("isShowPastLessons", "on"))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("isShowPastLessons", true),
                    model().attribute("teachers", teacherDtos),
                    model().attribute("departments", departments)
                ));
            verify(lessonServiceMock, times(1)).getAllWithFilter(lessonFilter);
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