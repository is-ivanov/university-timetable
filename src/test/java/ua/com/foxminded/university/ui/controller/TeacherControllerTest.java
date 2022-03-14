package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.restcontroller.link.TeacherDtoAssembler;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.GroupControllerTest.ON;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    public static final String URI_TEACHERS = "/teachers";
    public static final String URI_TEACHERS_ID = "/teachers/{id}";

    private MockMvc mockMvc;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private TeacherDtoAssembler teacherAssemblerMock;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(teacherController)
            .build();
    }

    @Nested
    @DisplayName("test 'showTeachers' method")
    class ShowTeachersTest {
        @Test
        @DisplayName("when GET request without parameters then should call expected " +
            "services and not load teachers in attribute of model")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> faculties = createTestFaculties();
            List<Department> departments = createTestDepartments();

            when(facultyServiceMock.getAllSortedByNameAsc()).thenReturn(faculties);
            when(departmentServiceMock.findAll()).thenReturn(departments);

            mockMvc.perform(get(URI_TEACHERS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attributeDoesNotExist("isShowInactiveTeachers",
                        "teachers"),
                    model().attribute("faculties", faculties),
                    model().attribute("departments", departments),
                    model().attribute("facultyIdSelect", is(nullValue())),
                    model().attribute("groupIdSelect", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with all parameters")
        void getRequestWithAllParameters() throws Exception {
            int facultyId = 15;
            int departmentId = 47;
            List<Department> departments = createTestDepartments();
            List<Teacher> teachers = createTestTeachers(facultyId);
            CollectionModel<TeacherDto> modelsTeachers = createTestModelTeacherDtos();


            when(teacherServiceMock.getAllByDepartment(departmentId))
                .thenReturn(teachers);
            when(departmentServiceMock.getAllByFaculty(facultyId))
                .thenReturn(departments);
            when(teacherAssemblerMock.toCollectionModel(teachers))
                .thenReturn(modelsTeachers);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId))
                    .param("departmentId", String.valueOf(departmentId))
                    .param("isShowInactiveTeachers", ON))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("isShowInactiveTeachers", true),
                    model().attribute("teachers", modelsTeachers),
                    model().attribute("departments", departments)
                );
        }

        @Test
        @DisplayName("when GET request with parameter facultyId and without departmentId")
        void getRequestWithFacultyIdAndWithoutDepartmentId() throws Exception {
            int facultyId = 34;

            List<Teacher> teachers = createTestTeachers(facultyId);
            CollectionModel<TeacherDto> modelsTeachers = createTestModelTeacherDtos();

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(teachers);
            when(teacherAssemblerMock.toCollectionModel(teachers))
                .thenReturn(modelsTeachers);

            mockMvc.perform(get(URI_TEACHERS)
                    .param("facultyId", String.valueOf(facultyId)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("teacher"),
                    model().attribute("teachers", modelsTeachers)
                );
            verify(departmentServiceMock, times(1))
                .getAllByFaculty(facultyId);
        }
    }



    @Nested
    @DisplayName("test 'deleteTeacher' method")
    class DeleteTeacherTest {
        @Test
        @DisplayName("when DELETE request with PathVariable id then should call teacherService.delete and redirect")
        void deleteRequestWithIdShouldCallTeacherServiceAndRedirect() throws Exception {
            int teacherId = 23;
            mockMvc.perform(delete(URI_TEACHERS_ID, teacherId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(teacherServiceMock, times(1)).delete(teacherId);
        }
    }


}
