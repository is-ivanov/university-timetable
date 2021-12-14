package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.DepartmentRepository;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;

import javax.persistence.EntityNotFoundException;
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

    @Mock
    private DepartmentDtoMapper mapperMock;

    @InjectMocks
    private DepartmentServiceImpl departmentService;


    @Test
    @DisplayName("test 'save' when call add method then should call Repository once")
    void testSave_CallDaoOnce() {
        Department department = new Department();
        departmentService.save(department);
        verify(departmentRepoMock).save(department);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Department then method " +
            "should return this DepartmentDto")
        void testReturnExpectedDepartment() {
            Department department = createTestDepartment(FACULTY_ID1);
            DepartmentDto expectedDepartmentDto = createTestDepartmentDto();

            when(departmentRepoMock.findById(ID1)).thenReturn(Optional.of(department));
            when(mapperMock.toDepartmentDto(department)).thenReturn(expectedDepartmentDto);

            DepartmentDto actualDepartmentDto = departmentService.getById(ID1);
            assertThat(actualDepartmentDto).isEqualTo(expectedDepartmentDto);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw" +
            " new EntityNotFoundException")
        void testReturnEmptyDepartment() {
            when(departmentRepoMock.findById(ID1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> departmentService.getById(ID1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Department id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'findAll' when Repository return List departments then " +
        "method should return this List")
    void testGetAll_ReturnListDepartments() {
        List<Department> testDepartments = createTestDepartments();
        List<DepartmentDto> testDepartmentDtos = createTestDepartmentDtos();

        when(departmentRepoMock.findAll()).thenReturn(testDepartments);
        when(mapperMock.toDepartmentDtos(testDepartments)).thenReturn(testDepartmentDtos);

        assertThat(departmentService.getAll()).isEqualTo(testDepartmentDtos);
    }

//    @Test
//    @DisplayName("test 'update' when call update method then should call " +
//        "departmentDao once")
//    void testUpdate_CallDaoOnce() {
//        Department department = new Department();
//        departmentService.update(department);
//        verify(departmentRepoMock).update(department);
//    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "departmentDao once")
    void testDelete_CallDaoOnce() {
        int departmentId = anyInt();
        departmentService.delete(departmentId);
        verify(departmentRepoMock).deleteById(departmentId);
    }

    @Test
    @DisplayName("test 'getAllByFacultyId' when Repository return List departments " +
        "then method should return this List")
    void testGetAllByFacultyId_ReturnListDepartments() {
        List<Department> testDepartments = createTestDepartments();
        List<DepartmentDto> testDepartmentDtos = createTestDepartmentDtos();

        when(departmentRepoMock.findAllByFacultyId(ID1)).thenReturn(testDepartments);
        when(mapperMock.toDepartmentDtos(testDepartments)).thenReturn(testDepartmentDtos);

        assertThat(departmentService.getAllByFaculty(ID1)).isEqualTo(testDepartmentDtos);

    }
}