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
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.restcontroller.link.DepartmentDtoAssembler;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    public static final String URI_DEPARTMENTS = "/departments";
    public static final String URI_DEPARTMENTS_ID = "/departments/{id}";

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private DepartmentDtoAssembler assemblerMock;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(departmentController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("test 'showDepartments' method")
    class ShowDepartmentsTest {

        @Test
        @DisplayName("when GET request without parameters")
        void getRequestWithoutParameters() throws Exception {
            List<Faculty> expectedFaculties = createTestFaculties();
            List<Department> testDepartments = createTestDepartments();
            CollectionModel<DepartmentDto> collectionModel = createTestCollectionModelDepartments();

            when(facultyServiceMock.getAllSortedByNameAsc())
                .thenReturn(expectedFaculties);
            when(departmentServiceMock.findAll()).thenReturn(testDepartments);
            when(assemblerMock.toCollectionModel(testDepartments))
                .thenReturn(collectionModel);

            mockMvc.perform(get(URI_DEPARTMENTS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("department"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("departments", collectionModel),
                    model().attribute("facultySelected", is(nullValue()))
                );
        }

        @Test
        @DisplayName("when GET request with parameter facultyId")
        void getRequestWithParameterFacultyId() throws Exception {
            List<Faculty> testFaculties = createTestFaculties();
            Faculty faculty1 = testFaculties.get(0);
            List<Department> departmentsFromFacultyId1 = createTestDepartments();
            CollectionModel<DepartmentDto> collectionModel =
                createTestCollectionModelDepartments();

            when(facultyServiceMock.getAllSortedByNameAsc())
                .thenReturn(testFaculties);
            when(departmentServiceMock.getAllByFaculty(FACULTY_ID1))
                .thenReturn(departmentsFromFacultyId1);
            when(assemblerMock.toCollectionModel(departmentsFromFacultyId1))
                .thenReturn(collectionModel);

            mockMvc.perform(get(URI_DEPARTMENTS)
                    .param("facultyId", String.valueOf(FACULTY_ID1)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("department"),
                    model().attribute("faculties", testFaculties),
                    model().attribute("departments", collectionModel),
                    model().attribute("facultySelected", faculty1)
                );
        }

    }

    @Nested
    @DisplayName("test 'deleteDepartment' method")
    class DeleteDepartmentTest {

        @Test
        @DisplayName("when DELETE request with @PathParameter 'id' then should call " +
            "departmentService.delete once and redirect")
        void deleteRequestWithId() throws Exception {
            int departmentId = anyInt();
            mockMvc.perform(delete(URI_DEPARTMENTS_ID, departmentId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(departmentServiceMock, times(1)).delete(departmentId);
        }
    }

}
