package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@Sql("/sql/hibernate/department-test-data.sql")
class DepartmentJpaRepositoryTest extends IntegrationTestBase {

    @Autowired
    private DepartmentRepository repo;

    @Nested
    @DisplayName("test 'getAllByFacultyId' method")
    class GetAllByFacultyIdTest {

        @Test
        @DisplayName("with faculty_id1 should return List with size = 2")
        void testGetAllDepartmentsByFacultyId1() {
            int expectedQuantityDepartments = 2;
            List<Department> departments = repo.findAllByFacultyId(FACULTY_ID1);
            assertThat(departments).hasSize(expectedQuantityDepartments);
            assertThat(departments).extracting(Department::getName)
                .containsOnly(NAME_FIRST_DEPARTMENT, NAME_SECOND_DEPARTMENT)
                .doesNotContain(NAME_THIRD_DEPARTMENT);
        }

        @Test
        @DisplayName("with faculty id=3 should return empty List")
        void testGetAllDepartmentsByFacultyId3() {
            assertThat(repo.findAllByFacultyId(ID3)).isEmpty();
        }
    }
}