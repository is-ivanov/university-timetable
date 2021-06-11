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

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/faculty-test-data.sql" })
class FacultyDaoImplTest {

    private static final String TABLE_NAME = "faculties";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Faculty not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FacultyDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test faculty should CountRowsTable = 3")
        void testAddFaculty() {
            Faculty faculty = new Faculty();
            faculty.setName(TEST_FACULTY_NAME);

            dao.add(faculty);
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
        @DisplayName("with id=1 should return faculty (1, 'Foreign Language')")
        void testGetByIdFaculty() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Faculty actualFaculty = dao.getById(FIRST_ID).get();
            assertEquals(expectedFaculty, actualFaculty);
        }


        @Test
        @DisplayName("with id=4 should return DAOException 'Faculty not found: 4'")
        void testGetByIdFacultyException() throws DAOException {
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
        void testGetAllFaculties() {
            int expectedQuantityFaculties = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityFaculties = dao.getAll().size();
            assertEquals(expectedQuantityFaculties, actualQuantityFaculties);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update name faculty id=1 should write new fields and getById(1) return this fields")
        void testUpdateFaculties() throws DAOException {
            Faculty expectedFaculty = new Faculty(FIRST_ID, TEST_FACULTY_NAME);
            dao.update(expectedFaculty);
            Faculty actualFaculty = dao.getById(FIRST_ID).get();
            assertEquals(expectedFaculty, actualFaculty);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete faculty id=2 should delete one record and number records table should equals 1")
        void testDeleteFaculty() {
            int expectedQuantityFaculties = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Faculty faculty = new Faculty(SECOND_ID, SECOND_FACULTY_NAME);
            dao.delete(faculty);
            int actualQuantityFaculties = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityFaculties, actualQuantityFaculties);
        }
    }
}
