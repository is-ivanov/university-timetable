package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.springconfig.BaseRepositoryIT;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.NUMBER_SECOND_ROOM;
import static ua.com.foxminded.university.TestObjects.ROOM_ID2;
import static ua.com.foxminded.university.domain.entity.Assertions.assertThat;

@Sql("/sql/hibernate/room-test-data.sql")
class RoomRepositoryTest extends BaseRepositoryIT {

    @Autowired
    private RoomRepository repo;

    @Nested
    @DisplayName("test 'findBusyRoomsOnTime' method")
    class FindBusyRoomsOnTimeTest {
        @Test
        @DisplayName("when time from 14:30 13-06-2021 to 16:00 13-06-2021 then " +
            "return list with one room (room_id2)")
        void testReturnOneRoom() {
            LocalDateTime from = LocalDateTime.of(2021, 6, 13, 14, 30);
            LocalDateTime to = from.plusMinutes(90);

            List<Room> freeRooms = repo.findBusyRoomsOnTime(from, to);

            assertThat(freeRooms).hasSize(1);
            Room freeRoom = freeRooms.get(0);

            assertThat(freeRoom).hasId(ROOM_ID2);
            assertThat(freeRoom).hasNumber(NUMBER_SECOND_ROOM);
        }

        @Test
        @DisplayName("when time from 15:45 12-09-2021 to 17:15 12-09-2021 then " +
            "return empty list")
        void testReturnTwoRooms() {
            LocalDateTime from = LocalDateTime.of(2021, 9, 12, 15, 45);
            LocalDateTime to = from.plusMinutes(90);

            List<Room> freeRooms = repo.findBusyRoomsOnTime(from, to);

            assertThat(freeRooms).isEmpty();
        }
    }
}