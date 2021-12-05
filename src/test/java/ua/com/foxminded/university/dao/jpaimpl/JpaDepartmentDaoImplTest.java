package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/department-test-data.sql")
class JpaDepartmentDaoImplTest extends IntegrationTestBase {

    private static final String MESSAGE_DELETE_EXCEPTION =
        "Can't delete because department id(3) not found";

    @Autowired
    private DepartmentDao dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test department should CountRowsTable = 3")
        void testAddDepartment() {
            Faculty faculty = new Faculty(ID1, NAME_FIRST_FACULTY);
            Department department = new Department(NAME_THIRD_DEPARTMENT, faculty);

            dao.add(department);

            Optional<Department> departmentOptional = dao.getById(3);
            Department actualDepartment = departmentOptional.get();

            assertThat(actualDepartment).isEqualTo(department);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id=1 should return expected department)")
        void testGetByIdDepartment() {

            Department actualDepartment = dao.getById(ID1).get();
            assertThat(actualDepartment.getId()).isEqualTo(ID1);
            assertThat(actualDepartment.getName()).isEqualTo(NAME_FIRST_DEPARTMENT);
        }

        @Test
        @DisplayName("with id=3 should return empty Optional")
        void testGetByIdDepartmentEmptyOptional() {
            Optional<Department> departmentOptional = dao.getById(ID3);
            assertThat(departmentOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllDepartments() {
            List<Department> departments = dao.getAll();
            assertThat(departments).hasSize(2);
            assertThat(departments).extracting(Department::getName)
                .contains(NAME_FIRST_DEPARTMENT, NAME_SECOND_DEPARTMENT);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with department id=1 should write new fields and " +
            "getById(1) return expected department")
        void testUpdateExistingDepartment_WriteNewDepartmentFields() {
            Faculty expectedFaculty = new Faculty(ID1, NAME_FIRST_FACULTY);
            Department expectedDepartment = new Department(ID1,
                NAME_THIRD_DEPARTMENT, expectedFaculty);

            dao.update(expectedDepartment);

            Department actualDepartment = dao.getById(ID1).get();
            assertThat(actualDepartment).isEqualTo(expectedDepartment);
        }

        @Test
        @DisplayName("with department id=3 should write new department")
        void testUpdateNonExistingDepartment_CreateNewDepartment() {
            Faculty expectedFaculty = new Faculty(ID1, NAME_FIRST_FACULTY);
            Department expectedDepartment = new Department(ID3,
                NAME_THIRD_DEPARTMENT, expectedFaculty);

            dao.update(expectedDepartment);

            Department actualDepartment = dao.getById(ID3).get();
            assertThat(actualDepartment).isEqualTo(expectedDepartment);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Nested
        @DisplayName("delete(department) method")
        class DeleteDepartmentTest {

            @Test
            @DisplayName("with department id=1 should delete one record and " +
                "number records table should equals 1")
            void testDeleteExistingDepartment_ReduceNumberRowsInTable() {

                Department departmentForDeleting = dao.getById(ID1).get();
                dao.delete(departmentForDeleting);
                List<Department> departments = dao.getAll();
                assertThat(departments).hasSize(1);
                assertThat(departments).extracting(Department::getName)
                        .doesNotContain(NAME_FIRST_DEPARTMENT);
            }

        }

        @Nested
        @DisplayName("delete(departmentId) method")
        class DeleteDepartmentIdTest {

            @Test
            @DisplayName("with department id=1 should delete one record and " +
                "number records table should equals 1")
            void testDeleteExistingDepartmentId1_ReduceNumberRowsInTable() {
                dao.delete(ID1);
                List<Department> departments = dao.getAll();
                assertThat(departments).hasSize(1);
                assertThat(departments).extracting(Department::getName)
                    .doesNotContain(NAME_FIRST_DEPARTMENT);
            }

            @Test
            @DisplayName("with department id=3 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingDepartment_ExceptionWriteLogWarn() {
               assertThatThrownBy(() -> dao.delete(ID3))
                   .isInstanceOf(DaoException.class)
                   .hasMessageContaining(MESSAGE_DELETE_EXCEPTION);
            }
        }
    }

    @Nested
    @DisplayName("test 'getAllByFacultyId' method")
    class GetAllByFacultyIdTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 2")
        void testGetAllDepartmentsByFacultyId1() {
            int expectedQuantityDepartments = 2;
            List<Department> departmentsFromFaculty1 = dao.getAllByFacultyId(ID1);
            assertThat(departmentsFromFaculty1).hasSize(expectedQuantityDepartments);

        }

        @Test
        @DisplayName("with faculty id=3 should return empty List")
        void testGetAllDepartmentsByFacultyId3() {
            assertThat(dao.getAllByFacultyId(ID3)).isEmpty();
        }
    }
}