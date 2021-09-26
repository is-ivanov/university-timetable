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
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/room-test-data.sql"})
class RoomDaoImplTest {

    private static final String TABLE_NAME = "rooms";
    private static final String TEST_BUILDING = "testBuilding";
    private static final String TEST_ROOM_NUMBER = "testRoom";
    private static final int ID1 = 1;
    private static final int ID4 = 4;
    private static final String FIRST_ROOM_BUILDING = "building-1";
    private static final String FIRST_ROOM_NUMBER = "1457a";
    private static final String MESSAGE_EXCEPTION = "Room id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because room id(4) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because room id(4) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoomDaoImpl dao;

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
        void testGetByIdRoom() throws DaoException {
            Room expectedRoom = new Room();
            expectedRoom.setId(ID1);
            expectedRoom.setBuilding(FIRST_ROOM_BUILDING);
            expectedRoom.setNumber(FIRST_ROOM_NUMBER);
            Room actualRoom = dao.getById(ID1).orElse(null);
            assertEquals(expectedRoom, actualRoom);
        }

        @Test
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdRoomException() throws DaoException {
            DaoException exception = assertThrows(DaoException.class,
                () -> dao.getById(ID4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
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
        @DisplayName("with room id=1 should write new fields and getById(1) " +
            "return expected room")
        void testUpdateExistingRooms_WriteNewFields() throws DaoException {
            Room expectedRoom = new Room(ID1, TEST_BUILDING,
                TEST_ROOM_NUMBER);
            dao.update(expectedRoom);
            Room actualRoom = dao.getById(ID1).orElse(null);
            assertEquals(expectedRoom, actualRoom);
        }

        @Test
        @DisplayName("with room id=4 should write new log.warn with " +
            "expected message")
        void testUpdateNonExistingRoom_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(RoomDaoImpl.class);
            Room room = new Room(ID4, TEST_BUILDING, TEST_ROOM_NUMBER);
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, room);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.update(room));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("with room id=1 should delete one record and number " +
            "records table should equals 2")
        void testDeleteExistingRooms_ReduceNumberRowsInTable() {
            int expectedQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Room room = new Room(ID1, FIRST_ROOM_BUILDING, FIRST_ROOM_NUMBER);
            dao.delete(room);
            int actualQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityRooms, actualQuantityRooms);
        }

        @Test
        @DisplayName("with room id=4 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingRoom_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(RoomDaoImpl.class);
            Room room = new Room(ID4, TEST_BUILDING,TEST_ROOM_NUMBER);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, room);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.delete(room));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

}
