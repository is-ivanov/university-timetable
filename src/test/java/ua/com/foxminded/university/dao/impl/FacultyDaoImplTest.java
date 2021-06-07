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
    private static final String TEST_ROOM_NUMBER = "testRoom";
    private static final int FIRST_ID = 1;
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String FIRST_ROOM_NUMBER = "1457a";
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

            dao.add(faculty);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
        // TODO test add faculty with null dean should create record with null
        // dean

        // TODO test add faculty with some dean should create record with dean
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {


        @Test
        @DisplayName("with id = 1 should return faculty (1, 'Foreign Language', dean id=1)")
        void testGetByIdFaculty() {
            Teacher dean = new Teacher();
            dean.setId(FIRST_ID);
            dean.setFirstName(NAME_DEAN);
            dean.setPatronymic(PATRONYMIC_DEAN);
            dean.setLastName(SURNAME_DEAN);

            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);
            expectedFaculty.setDean(dean);

            Faculty actualFaculty = dao.getById(FIRST_ID).get();
            assertEquals(expectedFaculty, actualFaculty);
        }

        @Test
        @DisplayName("with id=4 should return 'Faculty not found: 4' and empty Optional")
        void testGetByIdFacultyException() {

            Optional<Faculty> opt = dao.getById(4);
            assertFalse(opt.isPresent());
        }
    }
//
//    @Nested
//    @DisplayName("test 'getAll' method")
//    class getAllTest {
//
//        @Test
//        @DisplayName("should return List with size = 3")
//        void testGetAllRooms() {
//            int expectedQuantityRooms = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            int actualQuantityRooms = dao.getAll().size();
//            assertEquals(expectedQuantityRooms, actualQuantityRooms);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class updateTest {
//
//        @Test
//        @DisplayName("update building and number room id=1 should write new fields and getById(1) return this fields")
//        void testUpdateRooms() {
//            Room expectedRoom = new Room(FIRST_ID, TEST_BUILDING,
//                    TEST_ROOM_NUMBER);
//            dao.update(expectedRoom);
//            Room actualRoom = dao.getById(FIRST_ID).get();
//            assertEquals(expectedRoom, actualRoom);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class deleteTest {
//
//        @Test
//        @DisplayName("delete room id=1 should delete one record and number records table should equals 2")
//        void testUpdateRooms() {
//            int expectedQuantityRooms = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//            Room room = new Room(FIRST_ID, FIRST_ROOM_BUILDING,FIRST_ROOM_NUMBER);
//            dao.delete(room);
//            int actualQuantityRooms = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            assertEquals(expectedQuantityRooms, actualQuantityRooms);
//        }
//    }
}
