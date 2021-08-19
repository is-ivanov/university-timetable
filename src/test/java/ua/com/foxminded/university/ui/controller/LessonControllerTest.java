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
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.*;
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
    public static final String TEACHER_NAME = "teacher name";
    public static final String URL_ALL_PARAMETERS = "/teacher?facultyId=1&departmentId=2&isShowInactiveTeachers=on";
    public static final String URL_FACULTY_PARAMETER = "/teacher?facultyId=1";
    public static final String URL_FACULTY_ID_0 = "/teacher/faculty?facultyId=0";
    public static final String URL_FACULTY_ID_1 = "/teacher/faculty?facultyId=1";

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
            when(teacherServiceMock.convertListTeachersToDtos(any())).thenReturn(teachers);
            when(courseServiceMock.getAll()).thenReturn(courses);
            when(roomServiceMock.getAll()).thenReturn(rooms);

            mockMvc.perform(get("/lesson"))
                .andDo(print())
                .andExpect(matchAll(
                    status().isOk(),
                    view().name("lesson"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "isShowPastLessons"),
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

            mockMvc.perform(post("/lesson/filter"))
                .andDo(print());
        }
    }

}