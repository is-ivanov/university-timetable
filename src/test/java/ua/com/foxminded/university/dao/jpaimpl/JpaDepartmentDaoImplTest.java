package ua.com.foxminded.university.dao.jpaimpl;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/department-test-data.sql")
class JpaDepartmentDaoImplTest extends IntegrationTestBase {

    @Autowired
    @Qualifier("jpaDepartmentDaoImpl")
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
        void testGetByIdDepartmentException() {
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
        void testUpdateExistingDepartment_WriteNewDepartmentFields() throws DaoException {
            Faculty expectedFaculty = new Faculty(ID1, FIRST_FACULTY_NAME);
            Department expectedDepartment = new Department(ID1,
                TEST_DEPARTMENT_NAME, expectedFaculty);
            dao.update(expectedDepartment);
            Department actualDepartment = dao.getById(ID1).orElse(new Department());
            assertEquals(expectedDepartment, actualDepartment);
        }

//        @Test
//        @DisplayName("with department id=3 should write new log.warn and throw " +
//            "new DAOException")
//        void testUpdateNonExistingDepartment_ExceptionAndWriteLogWarn() {
//            LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
//            Department department = new Department(ID3, TEST_DEPARTMENT_NAME,
//                new Faculty());
//            String expectedLog = String.format(MESSAGE_UPDATE_MASK, department);
//            Exception ex = assertThrows(DaoException.class,
//                () -> dao.update(department));
//            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
//        }
    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Nested
//        @DisplayName("delete(department) method")
//        class DeleteDepartmentTest {
//
//            @Test
//            @DisplayName("with department id=1 should delete one record and " +
//                "number records table should equals 1")
//            void testDeleteExistingDepartment_ReduceNumberRowsInTable() {
//                int expectedQuantityDepartment = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                Department department = new Department();
//                department.setId(ID1);
//                dao.delete(department);
//                int actualQuantityDepartments = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityDepartment, actualQuantityDepartments);
//            }
//
//            @Test
//            @DisplayName("with department id=3 should write new log.warn and throw " +
//                "new DAOException")
//            void testDeleteNonExistingDepartment_ExceptionWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
//                Department department = new Department(ID3, TEST_DEPARTMENT_NAME,
//                    new Faculty());
//                String expectedLog = String.format(MESSAGE_DELETE_MASK, department);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(department));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayName("delete(departmentId) method")
//        class DeleteDepartmentIdTest {
//
//            @Test
//            @DisplayName("with department id=1 should delete one record and " +
//                "number records table should equals 1")
//            void testDeleteExistingDepartmentId1_ReduceNumberRowsInTable() {
//                int expectedQuantityDepartment = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                dao.delete(ID1);
//                int actualQuantityDepartments = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityDepartment, actualQuantityDepartments);
//            }
//
//            @Test
//            @DisplayName("with department id=3 should write new log.warn and throw " +
//                "new DAOException")
//            void testDeleteNonExistingDepartment_ExceptionWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
//                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID3);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(ID3));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllByFacultyId' method")
//    class GetAllByFacultyIdTest {
//
//        @Test
//        @DisplayName("with faculty id=1 should return List with size = 2")
//        void testGetAllDepartmentsByFacultyId1() {
//            int expectedQuantityDepartments = 2;
//            int actualQuantityDepartments =
//                dao.getAllByFacultyId(ID1).size();
//            assertEquals(expectedQuantityDepartments,
//                actualQuantityDepartments);
//        }
//
//        @Test
//        @DisplayName("with faculty id=3 should return empty List")
//        void testGetAllDepartmentsByFacultyId3() {
//            assertTrue(dao.getAllByFacultyId(ID3).isEmpty());
//        }
//    }
}