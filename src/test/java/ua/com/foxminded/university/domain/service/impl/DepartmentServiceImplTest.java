package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.DepartmentRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepoMock;

    @InjectMocks
    private DepartmentServiceImpl departmentService;


    @Nested
    @DisplayName("test 'findById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Department then method " +
            "should return this DepartmentDto")
        void testReturnExpectedDepartment() {
            Department department = createTestDepartment(FACULTY_ID1);

            when(departmentRepoMock.findById(ID1)).thenReturn(Optional.of(department));

            Department actualDepartment = departmentService.findById(ID1);
            assertThat(actualDepartment).isEqualTo(department);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw" +
            " new MyEntityNotFoundException")
        void testReturnEmptyDepartment() {
            when(departmentRepoMock.findById(ID1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Department with id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'findAll' when Repository return List departments then " +
        "method should return this List")
    void testGetAll_ReturnListDepartments() {
        List<Department> testDepartments = createTestDepartments();

        when(departmentRepoMock.findAll()).thenReturn(testDepartments);

        assertThat(departmentService.findAll()).isEqualTo(testDepartments);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "departmentDao once")
    void testDelete_CallDaoOnce() {
        int departmentId = anyInt();
        departmentService.delete(departmentId);
        verify(departmentRepoMock).deleteById(departmentId);
    }

    @Test
    @DisplayName("test 'getAllByFaculty' when Repository return List departments " +
        "then method should return this List")
    void testGetAllByFacultyId_ReturnListDepartments() {
        List<Department> testDepartments = createTestDepartments();

        when(departmentRepoMock.findAllByFacultyId(ID1)).thenReturn(testDepartments);

        assertThat(departmentService.getAllByFaculty(ID1)).isEqualTo(testDepartments);

    }
}