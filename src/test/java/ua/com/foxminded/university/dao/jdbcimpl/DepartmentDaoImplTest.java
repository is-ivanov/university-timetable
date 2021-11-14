package ua.com.foxminded.university.dao.jdbcimpl;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/department-test-data.sql"})
class DepartmentDaoImplTest {

    private static final String TABLE_NAME = "departments";
    private static final int ID1 = 1;
    private static final int ID3 = 3;
    private static final String FIRST_FACULTY_NAME = "Chemical Technology";
    private static final String TEST_DEPARTMENT_NAME = "Test Department";
    private static final String FIRST_DEPARTMENT_NAME = "Chemistry";
    private static final String MESSAGE_EXCEPTION = "Department id(3) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_DELETE_ID_MASK = "Can't delete department id(%s)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update because " +
        "department id(3) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete because " +
        "department id(3) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DepartmentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test department should CountRowsTable = 3")
        void testAddDepartment() {
            Faculty faculty = new Faculty();
            faculty.setId(ID1);
            faculty.setName(FIRST_FACULTY_NAME);
            Department department = new Department();
            department.setName(TEST_DEPARTMENT_NAME);
            department.setFaculty(faculty);

            dao.add(department);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id=1 should return expected department)")
        void testGetByIdDepartment() throws DaoException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Department expectedDepartment = new Department(ID1,
                FIRST_DEPARTMENT_NAME, expectedFaculty);

            Department actualDepartment = dao.getById(ID1).orElse(null);
            assertEquals(expectedDepartment, actualDepartment);
        }

        @Test
        @DisplayName("with id=3 should return DAOException")
        void testGetByIdDepartmentException() throws DaoException {
            DaoException exception = assertThrows(DaoException.class,
                () -> dao.getById(ID3));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllDepartments() {
            int expectedQuantityDepartments = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityDepartments = dao.getAll().size();
            assertEquals(expectedQuantityDepartments,
                actualQuantityDepartments);
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

        @Test
        @DisplayName("with department id=3 should write new log.warn and throw " +
            "new DAOException")
        void testUpdateNonExistingDepartment_ExceptionAndWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
            Department department = new Department(ID3, TEST_DEPARTMENT_NAME,
                new Faculty());
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, department);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.update(department));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
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
                int expectedQuantityDepartment = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                Department department = new Department();
                department.setId(ID1);
                dao.delete(department);
                int actualQuantityDepartments = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityDepartment, actualQuantityDepartments);
            }

            @Test
            @DisplayName("with department id=3 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingDepartment_ExceptionWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
                Department department = new Department(ID3, TEST_DEPARTMENT_NAME,
                    new Faculty());
                String expectedLog = String.format(MESSAGE_DELETE_MASK, department);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(department));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }

        @Nested
        @DisplayName("delete(departmentId) method")
        class DeleteDepartmentIdTest {

            @Test
            @DisplayName("with department id=1 should delete one record and " +
                "number records table should equals 1")
            void testDeleteExistingDepartmentId1_ReduceNumberRowsInTable() {
                int expectedQuantityDepartment = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                dao.delete(ID1);
                int actualQuantityDepartments = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityDepartment, actualQuantityDepartments);
            }

            @Test
            @DisplayName("with department id=3 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingDepartment_ExceptionWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(DepartmentDaoImpl.class);
                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID3);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(ID3));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
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
            int actualQuantityDepartments =
                dao.getAllByFacultyId(ID1).size();
            assertEquals(expectedQuantityDepartments,
                actualQuantityDepartments);
        }

        @Test
        @DisplayName("with faculty id=3 should return empty List")
        void testGetAllDepartmentsByFacultyId3() {
            assertTrue(dao.getAllByFacultyId(ID3).isEmpty());
        }
    }

}