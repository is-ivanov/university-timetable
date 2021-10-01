package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

//@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_COURSE = "firstCourse";
    public static final String NAME_SECOND_COURSE = "secondCourse";

    @Mock
    private CourseService courseServiceMock;

    @Mock
    private PageSequenceCreator pageSequenceCreatorMock;

    @InjectMocks
    private CourseController courseController;

    private CourseRequestBuilder requestBuilder;

    @BeforeEach
    void setUp() {
        MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(courseController)
            .build();
        requestBuilder = new CourseRequestBuilder(mockMvc);
    }


//
//    @BeforeEach
//    public void setUp() {
////        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
////        viewResolver.setPrefix("/WEB-INF/templates/");
////        viewResolver.setSuffix(".html");
//        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
////            .setViewResolvers(viewResolver)
//            .build();
//    }
//
//    @Test
//    @DisplayName("Test showCourses")
//    void testShowCourses() throws Exception {
//        Course firstCourse = new Course(ID1, NAME_FIRST_COURSE);
//        Course secondCourse = new Course(ID2, NAME_SECOND_COURSE);
//
//        List<Course> expectedCourses = Arrays.asList(firstCourse, secondCourse);
//        when(courseServiceMock.getAll()).thenReturn(expectedCourses);
//
//        mockMvc.perform(get("/courses"))
//            .andDo(print())
//            .andExpectAll(
//                status().isOk(),
//                view().name("course"),
//                model().attributeExists("courses"),
//                model().attribute("courses", expectedCourses)
//            );
//    }

}