package ua.com.foxminded.university.dao.impl;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/room-test-data.sql"})
class RoomDaoImplTest {

    private static final String TABLE_NAME = "rooms";
    private static final String TEST_BUILDING = "testBuilding";
    private static final String TEST_ROOM_NUMBER = "testRoom";
    private static final int ID4 = 4;
    private static final String BUILDING_THIRD_ROOM = "building-2";
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
    class AddTest {


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
    class GetByIdTest {


        @Test
        @DisplayName("with id = 1 should return room (1, 'building-1', '1457a')")
        void testGetByIdRoom() throws DaoException {
            Room expectedRoom = new Room();
            expectedRoom.setId(ID1);
            expectedRoom.setBuilding(BUILDING_FIRST_ROOM);
            expectedRoom.setNumber(NUMBER_FIRST_ROOM);
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
    class GetAllTest {

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
    class UpdateTest {

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
    @DisplayName("test 'delete(room)' method argument 'room'")
    class DeleteRoomTest {

        @Test
        @DisplayName("with room id=1 should delete one record and number " +
            "records table should equals 2")
        void testDeleteExistingRooms_ReduceNumberRowsInTable() {
            int expectedQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Room room = new Room(ID1, BUILDING_FIRST_ROOM, NUMBER_FIRST_ROOM);
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
            Room room = new Room(ID4, TEST_BUILDING, TEST_ROOM_NUMBER);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, room);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.delete(room));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete(roomId)' method argument 'roomId'")
    class DeleteRoomIdTest {

        @Test
        @DisplayName("with room id=1 should delete one record and number " +
            "records table should equals 2")
        void testDeleteExistingRooms_ReduceNumberRowsInTable() {
            int expectedQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            dao.delete(ID1);
            int actualQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityRooms, actualQuantityRooms);
        }

        @Test
        @DisplayName("with room id=4 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingRoom_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(RoomDaoImpl.class);
            String expectedLog = String.format("Can't delete room id(%d)", ID4);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.delete(ID4));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'countAll' method")
    class CountAllTest {
        @Test
        @DisplayName("should return expected ")
        void shouldReturn3() {
            int expectedQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertThat(dao.countAll(), is(equalTo(expectedQuantityRooms)));
        }
    }

    @Nested
    @DisplayName("test 'getAllSortedPaginated' method")
    class GetAllSortedPaginatedTest {
        long totalRooms = 3L;

        @Test
        @DisplayName("when size 1 and first page without sorted then should return " +
            "page with one room sorted by room number")
        void whenSize1AndFirstPageThenReturnFirstOneRoom() {
            int pageNumber = 0;
            int pageSize = 1;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Room> page = dao.getAllSortedPaginated(pageable);

            Room actualRoom = page.getContent().get(0);
            assertThat(page.getTotalElements(), is(equalTo(totalRooms)));
            assertThat(page.getSize(), is(equalTo(pageSize)));
            assertThat(actualRoom.getId(), is(equalTo(3)));
            assertThat(actualRoom.getBuilding(), is(equalTo(BUILDING_THIRD_ROOM)));
            assertThat(actualRoom.getNumber(), is(equalTo("145")));
        }

        @Test
        @DisplayName("when pageable with page size = 2 sort by ID then should " +
            "return sorted by ID page with 2 rooms")
        void whenPageableWithSortByIdThenReturnSortedByIdPage() {
            int pageNumber = 0;
            int pageSize = 2;

            Sort sort = Sort.by(Sort.Direction.ASC, "room_id");
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<Room> page = dao.getAllSortedPaginated(pageable);
            assertThat(page.getTotalElements(), is(equalTo(totalRooms)));
            assertThat(page.getSize(), is(equalTo(pageSize)));
            assertThat(page.getNumber(), is(equalTo(pageNumber)));
            assertThat(page.getTotalPages(), is(equalTo(2)));
            List<Room> rooms = page.getContent();
            Room firstRoom = rooms.get(0);
            Room secondRoom = rooms.get(1);
            assertThat(firstRoom.getId(), is(equalTo(ID1)));
            assertThat(firstRoom.getBuilding(), is(equalTo(BUILDING_FIRST_ROOM)));
            assertThat(firstRoom.getNumber(), is(equalTo(NUMBER_FIRST_ROOM)));
            assertThat(secondRoom.getId(), is(equalTo(ID2)));
            assertThat(secondRoom.getBuilding(), is(equalTo(BUILDING_FIRST_ROOM)));
            assertThat(secondRoom.getNumber(), is(equalTo(NUMBER_SECOND_ROOM)));
        }
    }
}
