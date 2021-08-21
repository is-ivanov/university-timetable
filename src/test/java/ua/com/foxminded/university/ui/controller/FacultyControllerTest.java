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
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NAME_FIRST_FACULTY = "Faculty1 name";
    public static final String NAME_SECOND_FACULTY = "Faculty2 name";

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyServiceMock;

    @InjectMocks
    private FacultyController facultyController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(facultyController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test showFaculties")
    void testShowFaculties() throws Exception {
        Faculty faculty1 = new Faculty(ID1, NAME_FIRST_FACULTY);
        Faculty faculty2 = new Faculty(ID2, NAME_SECOND_FACULTY);
        List<Faculty> expectedFaculties = Arrays.asList(faculty1, faculty2);

        when(facultyServiceMock.getAll()).thenReturn(expectedFaculties);

        mockMvc.perform(get("/faculties"))
            .andDo(print())
            .andExpect(matchAll(
               status().isOk(),
                view().name("faculty"),
                model().attribute("faculties", expectedFaculties)
            ));
    }
}