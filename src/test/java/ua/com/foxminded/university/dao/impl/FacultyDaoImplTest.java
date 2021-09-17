package ua.com.foxminded.university.dao.impl;

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
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/faculty-test-data.sql"})
class FacultyDaoImplTest {

    private static final String TABLE_NAME = "faculties";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Faculty id(3) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because faculty id(3) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because faculty id(3) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FacultyDaoImpl dao;

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
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Faculty actualFaculty = dao.getById(ID1).orElse(null);
            assertEquals(expectedFaculty, actualFaculty);
        }

        @Test
        @DisplayName("with id=3 should return DAOException")
        void testGetByIdFacultyException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                () -> dao.getById(ID3));
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
        @DisplayName("with faculty id=1 should write new fields and" +
            " getById(1) return expected faculty")
        void testUpdateExistingFaculty_WriteNewFacultyName() throws DAOException {
            Faculty expectedFaculty = new Faculty(ID1, TEST_FACULTY_NAME);
            dao.update(expectedFaculty);
            Faculty actualFaculty = dao.getById(ID1).orElse(new Faculty());
            assertEquals(expectedFaculty, actualFaculty);
        }

        @Test
        @DisplayName("with faculty id=3 should write new log.warn with " +
            "expected message")
        void testUpdateNonExistingFaculty_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(FacultyDaoImpl.class);
            Faculty faculty = new Faculty(ID3, TEST_FACULTY_NAME);
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, faculty);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.update(faculty));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("with faculty id=2 should delete one record and number " +
            "records table should equals 1")
        void testDeleteExistingFaculty_ReduceNumberRowsInTable() {
            int expectedQuantityFaculties = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Faculty faculty = new Faculty(ID2, SECOND_FACULTY_NAME);
            dao.delete(faculty);
            int actualQuantityFaculties = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityFaculties, actualQuantityFaculties);
        }

        @Test
        @DisplayName("with faculty id=3 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingFaculty_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(FacultyDaoImpl.class);
            Faculty faculty = new Faculty(ID3, TEST_FACULTY_NAME);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, faculty);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.delete(faculty));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Test
    @DisplayName("test 'getAllSortedByNameAsc 'method")
    void testShouldReturnFacultiesInOrder() {
        List<Faculty> sortedFaculties = dao.getAllSortedByNameAsc();
        assertEquals(SECOND_FACULTY_NAME, sortedFaculties.get(0).getName());
        assertEquals(FIRST_FACULTY_NAME, sortedFaculties.get(1).getName());
    }

    @Nested
    @DisplayName("test 'getAllSotedPaginated' method")
    class getAllSortedPaginatedTest {

        @Test
        @DisplayName("when size 2 and page 1 then return first two faculty sorted by name")
        void testShouldReturnTwoSortedFaculties() {
            //TODO !!!!!!!!!!!!!!!!!!!!!!!
        }

    }

}