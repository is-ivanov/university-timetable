package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

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
import ua.com.foxminded.university.domain.entity.Teacher;
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
    private static final String NAME_DEAN = "Ivan";
    private static final String SURNAME_DEAN = "Petrov";
    private static final String PATRONYMIC_DEAN = "Sergeevich";

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
            Teacher dean = new Teacher();
            faculty.setDean(dean);

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
        @DisplayName("with id=1 should return faculty (1, 'Foreign Language', dean id=1)")
        void testGetByIdFacultyWithDean() {
            Teacher expectedDean = new Teacher();
            expectedDean.setId(FIRST_ID);
            expectedDean.setFirstName(NAME_DEAN);
            expectedDean.setPatronymic(PATRONYMIC_DEAN);
            expectedDean.setLastName(SURNAME_DEAN);

            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);
            expectedFaculty.setDean(expectedDean);

            Faculty actualFaculty = dao.getById(FIRST_ID).get();
            assertEquals(expectedFaculty, actualFaculty);
        }

        @Test
        @DisplayName("with id=2 should return faculty (2, 'Chemical Technology', dean=null)")
        void testGetByIdFacultyWithoutDean() {
            Teacher emptyDean = new Teacher();

            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(SECOND_ID);
            expectedFaculty.setName(SECOND_FACULTY_NAME);
            expectedFaculty.setDean(emptyDean);

            Faculty actualFaculty = dao.getById(SECOND_ID).get();
            assertEquals(expectedFaculty, actualFaculty);
        }

        @Test
        @DisplayName("with id=4 should return 'Faculty not found: 4' and empty Optional")
        void testGetByIdFacultyException() {

            Optional<Faculty> opt = dao.getById(4);
            assertFalse(opt.isPresent());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 3")
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
        @DisplayName("update name and dean_id (set to null) faculty id=1 should write new fields and getById(1) return this fields")
        void testUpdateFaculties() {
            Teacher dean = new Teacher();
            Faculty expectedFaculty = new Faculty(FIRST_ID, TEST_FACULTY_NAME,
                    dean);
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
            Teacher dean = new Teacher();
            Faculty faculty = new Faculty(SECOND_ID, SECOND_FACULTY_NAME, dean);
            dao.delete(faculty);
            int actualQuantityRooms = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityFaculties, actualQuantityRooms);
        }
    }
}
