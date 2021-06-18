package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/department-test-data.sql" })
class DepartmentDaoImplTest {

    private static final String TABLE_NAME = "departments";
    private static final int FIRST_ID = 1;
    private static final String FIRST_FACULTY_NAME = "Chemical Technology";
    private static final String TEST_DEPARTMENT_NAME = "Test Department";
    private static final String FIRST_DEPARTMENT_NAME = "Chemistry";
    private static final String MESSAGE_EXCEPTION = "Department not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    DepartmentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test department should CountRowsTable = 3")
        void testAddDepartment() {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
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
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return expected department)")
        void testGetByIdDepartment() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);
            
            Department expectedDepartment = new Department(FIRST_ID,
                    FIRST_DEPARTMENT_NAME, expectedFaculty);

            Department actualDepartment = dao.getById(FIRST_ID).get();
            assertEquals(expectedDepartment, actualDepartment);
        }

        @Test
        @DisplayName("with id=4 should return DAOException 'Department not found: 4'")
        void testGetByIdDepartmentException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                    () -> dao.getById(4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

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
    class updateTest {

        @Test
        @DisplayName("update properties department id=1 should write new fields and getById(1) return this fields")
        void testUpdateDepartment() throws DAOException {
            Faculty expectedFaculty = new Faculty(FIRST_ID, FIRST_FACULTY_NAME);
            Department expectedDepartment = new Department(FIRST_ID,
                    TEST_DEPARTMENT_NAME, expectedFaculty);
            dao.update(expectedDepartment);
            Department actualDepartment = dao.getById(FIRST_ID).get();
            assertEquals(expectedDepartment, actualDepartment);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete department id=1 should delete one record and number records table should equals 1")
        void testDeleteDepartment() {
            int expectedQuantityDepartment = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Department department = new Department();
            department.setId(FIRST_ID);
            dao.delete(department);
            int actualQuantityDepartments = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityDepartment, actualQuantityDepartments);
        }
    }

}
