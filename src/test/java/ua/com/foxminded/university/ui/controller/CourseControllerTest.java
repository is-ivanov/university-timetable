package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_COURSE = "firstCourse";
    public static final String NAME_SECOND_COURSE = "secondCourse";

    private MockMvc mockMvc;

    @Mock
    private CourseService courseServiceMock;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test showCourses")
    void testShowCourses() throws Exception {
        Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
        Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);

        List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
        when(courseServiceMock.getAll()).thenReturn(expectedCourses);
        mockMvc.perform(get("/course"))
            .andDo(print())
            .andExpect(matchAll(
                status().isOk(),
                view().name("course"),
                model().attributeExists("courses"),
                model().attribute("courses", expectedCourses)
            ));
    }

}