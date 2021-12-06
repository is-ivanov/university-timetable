package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.dao.interfaces.RoomRepository;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/room-test-data.sql")
class RoomRepositoryJpaTest extends IntegrationTestBase {

    private static final String TABLE_NAME = "rooms";
    private static final String TEST_BUILDING = "testBuilding";
    private static final String TEST_ROOM_NUMBER = "testRoom";
    private static final String MESSAGE_DELETE_EXCEPTION =
        "Can't delete because room id(4) not found";

    @Autowired
    private RoomRepository dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {


        @Test
        @DisplayName("add test room should CountRowsTable = 4")
        void testAddRoom() {
            int expectedRowsInTable = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            Room room = new Room(TEST_BUILDING, TEST_ROOM_NUMBER);

            dao.add(room);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertThat(actualRowsInTable).isEqualTo(expectedRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {


        @Test
        @DisplayName("with id = 1 should return room (1, 'building-1', '1457a')")
        void testGetByIdRoom() throws DaoException {

            Room actualRoom = dao.getById(ID1).get();
            assertThat(actualRoom.getId()).isEqualTo(ID1);
            assertThat(actualRoom.getBuilding()).isEqualTo(BUILDING_FIRST_ROOM);
            assertThat(actualRoom.getNumber()).isEqualTo(NUMBER_FIRST_ROOM);
        }

        @Test
        @DisplayName("with id=4 should return empty Optional")
        void testGetByIdRoomEmptyOptional() {
            Optional<Room> roomOptional = dao.getById(4);
            assertThat(roomOptional).isEmpty();
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
            List<Room> rooms = dao.getAll();
            assertThat(rooms).hasSize(expectedQuantityRooms);
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
            Room actualRoom = dao.getById(ID1).get();
            assertThat(actualRoom).isEqualTo(expectedRoom);
        }

        @Test
        @DisplayName("with room id=4 should write new room")
        void testUpdateNonExistingRoom_CreateNewRoom() {
            Room expectedRoom = new Room(4, TEST_BUILDING,
                TEST_ROOM_NUMBER);
            dao.update(expectedRoom);
            Room actualRoom = dao.getById(4).get();
            assertThat(actualRoom).isEqualTo(expectedRoom);
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
            Room room = dao.getById(ID1).get();
            dao.delete(room);
            List<Room> rooms = dao.getAll();
            assertThat(rooms).hasSize(expectedQuantityRooms);
            assertThat(rooms).extracting(Room::getNumber)
                .doesNotContain(NUMBER_FIRST_ROOM);
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
            assertThat(actualQuantityRooms).isEqualTo(expectedQuantityRooms);
        }

        @Test
        @DisplayName("with room id=4 should throw DaoException with " +
            "expected message")
        void testDeleteNonExistingRoom_ThrowDaoException() {
            assertThatThrownBy(() -> dao.delete(4))
                .isInstanceOf(DaoException.class)
                .hasMessageContaining(MESSAGE_DELETE_EXCEPTION);
        }
    }

    @Nested
    @DisplayName("test 'countAll' method")
    class CountAllTest {
        @Test
        @DisplayName("should return expected value")
        void shouldReturn3() {
            int expectedQuantityRooms = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertThat(dao.countAll()).isEqualTo(expectedQuantityRooms);
        }
    }

    @Nested
    @DisplayName("test 'getAllSortedPaginated' method")
    class GetAllSortedPaginatedTest {
        final long totalRooms = 3L;

        @Test
        @DisplayName("when size 1 and first page without sorted then should return " +
            "page with one room sorted by room number")
        void whenSize1AndFirstPageThenReturnFirstOneRoom() {
            int pageNumber = 0;
            int pageSize = 1;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Room> page = dao.getAllSortedPaginated(pageable);

            Room actualRoom = page.getContent().get(0);
            assertThat(page.getTotalElements()).isEqualTo(totalRooms);
            assertThat(page.getSize()).isEqualTo(pageSize);
            assertThat(actualRoom.getId()).isEqualTo(ID3);
            assertThat(actualRoom.getBuilding()).isEqualTo(BUILDING_THIRD_ROOM);
            assertThat(actualRoom.getNumber()).isEqualTo(NUMBER_THIRD_ROOM);
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
            assertThat(page.getTotalElements()).isEqualTo(totalRooms);
            assertThat(page.getSize()).isEqualTo(pageSize);
            assertThat(page.getNumber()).isEqualTo(pageNumber);
            assertThat(page.getTotalPages()).isEqualTo(2);
            List<Room> rooms = page.getContent();
            Room firstRoom = rooms.get(0);
            Room secondRoom = rooms.get(1);
            assertThat(firstRoom.getId()).isEqualTo(ID1);
            assertThat(firstRoom.getBuilding()).isEqualTo(BUILDING_FIRST_ROOM);
            assertThat(firstRoom.getNumber()).isEqualTo(NUMBER_FIRST_ROOM);
            assertThat(secondRoom.getId()).isEqualTo(ID2);
            assertThat(secondRoom.getBuilding()).isEqualTo(BUILDING_SECOND_ROOM);
            assertThat(secondRoom.getNumber()).isEqualTo(NUMBER_SECOND_ROOM);
        }
    }
}