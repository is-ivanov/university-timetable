package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

class DepartmentDtoMapperTest {

    private DepartmentDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DepartmentDtoMapperImpl();
    }

    @Nested
    @DisplayName("test 'toDepartmentDto' method")
    class ToDepartmentDtoTest {
        @Test
        @DisplayName("when department with full fields then should return " +
            "DepartmentDto with full fields")
        void whenConvertDepartmentWithFullFields_ReturnDepartmentDtoWithFullFields() {
            int facultyId = 2;
            Department department = createTestDepartment(facultyId);

            DepartmentDto departmentDto = mapper.toDepartmentDto(department);

            assertThat(departmentDto.getId()).isEqualTo(department.getId());
            assertThat(departmentDto.getName()).isEqualTo(department.getName());
            assertThat(departmentDto.getFacultyId()).isEqualTo(department.getFaculty().getId());
            assertThat(departmentDto.getFacultyName()).isEqualTo(department.getFaculty().getName());
        }
    }

    @Nested
    @DisplayName("test 'toDepartment' method")
    class ToDepartmentTest {
        @Test
        @DisplayName("when DepartmentDto with full fields then should return Department with full fields")
        void whenDepartmentDtoWithFullFields_ReturnDepartmentWithFullFields() {
            DepartmentDto departmentDto = createTestDepartmentDto();

            Department department = mapper.toDepartment(departmentDto);

            assertThat(department.getId()).isEqualTo(departmentDto.getId());
            assertThat(department.getName()).isEqualTo(departmentDto.getName());
            assertThat(department.getFaculty().getId()).isEqualTo(departmentDto.getFacultyId());
            assertThat(department.getFaculty().getName()).isEqualTo(departmentDto.getFacultyName());
        }
    }

    @Nested
    @DisplayName("test 'toDepartmentDtos' method")
    class ToDepartmentDtosTest {
        @Test
        void testConvertListDepartmentsToListDtos() {
            List<Department> departments = createTestDepartments();

            List<DepartmentDto> departmentDtos = mapper.toDepartmentDtos(departments);

            assertThat(departmentDtos).hasSize(departments.size());
            assertThat(departmentDtos).extracting(DepartmentDto::getId)
                .contains(DEPARTMENT_ID1, DEPARTMENT_ID2);
            assertThat(departmentDtos).extracting(DepartmentDto::getName)
                .contains(NAME_FIRST_DEPARTMENT, NAME_SECOND_DEPARTMENT);

        }
    }
}