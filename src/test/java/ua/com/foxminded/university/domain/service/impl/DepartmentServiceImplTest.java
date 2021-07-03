package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentDao departmentDaoMock;

    @BeforeEach
    void setUp() {
        departmentService = new DepartmentServiceImpl(departmentDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Department department = new Department();
        departmentService.add(department);
        verify(departmentDaoMock).add(department);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Department then method " +
            "should return this Department")
        void testReturnExpectedDepartment() throws Exception {
            Department expectedDepartment = new Department();
            expectedDepartment.setId(1);
            expectedDepartment.setName("Department name");
            expectedDepartment.setFaculty(new Faculty());
            when(departmentDaoMock.getById(1))
                .thenReturn(Optional.of(expectedDepartment));
            assertEquals(expectedDepartment, departmentService.getById(1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should return" +
            " empty Department")
        void testReturnEmptyDepartment() throws Exception {
            Optional<Department> optional = Optional.empty();
            when(departmentDaoMock.getById(1)).thenReturn(optional);
            assertEquals(new Department(), departmentService.getById(1));
        }

        @Test
        @DisplayName("when Dao return DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception {
            when(departmentDaoMock.getById(1)).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> departmentService.getById(1));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List departments then " +
        "method should return this List")
    void testGetAll_ReturnListDepartments() {
        Department department1 = new Department();
        department1.setId(1);
        Department department2 = new Department();
        department2.setId(1);
        List<Department> expectedDepartments = new ArrayList<>();
        expectedDepartments.add(department1);
        expectedDepartments.add(department2);
        when(departmentDaoMock.getAll()).thenReturn(expectedDepartments);
        assertEquals(expectedDepartments, departmentService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "departmentDao once")
    void testUpdate_CallDaoOnce() {
        Department department = new Department();
        departmentService.update(department);
        verify(departmentDaoMock).update(department);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "departmentDao once")
    void testDelete_CallDaoOnce() {
        Department department = new Department();
        departmentService.delete(department);
        verify(departmentDaoMock).delete(department);
    }
}