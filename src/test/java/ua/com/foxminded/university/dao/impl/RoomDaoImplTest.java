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

import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/room-test-data.sql" })
class RoomDaoImplTest {

    private static final String TABLE_NAME = "rooms";
    private static final String TEST_BUILDING = "testBuilding";
    private static final String TEST_ROOM_NUMBER = "testRoom";
    private static final int FIRST_ID = 1;
    private static final String FIRST_ROOM_BUILDING = "building-1";
    private static final String FIRST_ROOM_NUMBER = "1457a";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    RoomDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {


        @Test
        @DisplayName("add test room should CountRowsTable = 4")
        void testAddRoom() {
            Room room = new Room();
            room.setBuilding(TEST_BUILDING);
            room.setNumber(TEST_ROOM_NUMBER);

            dao.add(room);
            int expectedRowsInTable = 4;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {


        @Test
        @DisplayName("with id = 1 should return room (1, 'building-1', '1457a')")
        void testGetByIdRoom() {
            Room expectedRoom = new Room();
            expectedRoom.setId(FIRST_ID);
            expectedRoom.setBuilding(FIRST_ROOM_BUILDING);
            expectedRoom.setNumber(FIRST_ROOM_NUMBER);
            Room actualRoom = dao.getById(FIRST_ID).get();
            assertEquals(expectedRoom, actualRoom);
        }

        @Test
        @DisplayName("with id=4 should return 'Room not found: 4' and empty Optional")
        void testGetByIdRoomException() {

            Optional<Room> opt = dao.getById(4);
            assertFalse(opt.isPresent());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllRooms() {
            int expectedQuantityRooms = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityRooms = dao.getAll().size();
            assertEquals(expectedQuantityRooms, actualQuantityRooms);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update building and number room id=1 should write new fields and getById(1) return this fields")
        void testUpdateRooms() {
            Room expectedRoom = new Room(FIRST_ID, TEST_BUILDING,
                    TEST_ROOM_NUMBER);
            dao.update(expectedRoom);
            Room actualRoom = dao.getById(FIRST_ID).get();
            assertEquals(expectedRoom, actualRoom);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete room id=1 should delete one record and number records table should equals 2")
        void testDeleteRooms() {
            int expectedQuantityRooms = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Room room = new Room(FIRST_ID, FIRST_ROOM_BUILDING,FIRST_ROOM_NUMBER);
            dao.delete(room);
            int actualQuantityRooms = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityRooms, actualQuantityRooms);
        }
    }

}
