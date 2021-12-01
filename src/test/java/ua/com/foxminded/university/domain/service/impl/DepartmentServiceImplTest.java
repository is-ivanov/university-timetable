package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    public static final String DEPARTMENT_NAME = "Department name";
    public static final int ID1 = 1;

    @Mock
    private DepartmentDao departmentDaoMock;

    @InjectMocks
    private DepartmentServiceImpl departmentService;


    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Department department = new Department();
        departmentService.add(department);
        verify(departmentDaoMock).add(department);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Department then method " +
            "should return this Department")
        void testReturnExpectedDepartment() {
            Department expectedDepartment = new Department();
            expectedDepartment.setId(ID1);
            expectedDepartment.setName(DEPARTMENT_NAME);
            expectedDepartment.setFaculty(new Faculty());
            when(departmentDaoMock.getById(ID1))
                .thenReturn(Optional.of(expectedDepartment));
            assertEquals(expectedDepartment, departmentService.getById(ID1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should return" +
            " empty Department")
        void testReturnEmptyDepartment() {
            Optional<Department> optional = Optional.empty();
            when(departmentDaoMock.getById(ID1)).thenReturn(optional);
            assertEquals(new Department(), departmentService.getById(ID1));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List departments then " +
        "method should return this List")
    void testGetAll_ReturnListDepartments() {
        Department department1 = new Department();
        department1.setId(ID1);
        Department department2 = new Department();
        department2.setId(ID1);
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

    @Test
    @DisplayName("test 'getAllByFacultyId' when Dao return List departments " +
        "then method should return this List")
    void testGetAllByFacultyId_ReturnListDepartments() {
        Department department1 = new Department();
        department1.setId(ID1);
        Department department2 = new Department();
        department2.setId(ID1);
        List<Department> expectedDepartments = new ArrayList<>();
        expectedDepartments.add(department1);
        expectedDepartments.add(department2);
        when(departmentDaoMock.getAllByFacultyId(ID1)).thenReturn(expectedDepartments);
        assertEquals(expectedDepartments,
            departmentService.getAllByFaculty(ID1));
    }
}