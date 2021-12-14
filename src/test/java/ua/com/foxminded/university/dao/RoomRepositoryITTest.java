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
import static ua.com.foxminded.university.TestObjects.*;

@Sql("/sql/hibernate/room-test-data.sql")
class RoomRepositoryITTest extends BaseRepositoryIT {

    @Autowired
    private RoomRepository repo;

    @Nested
    @DisplayName("test 'findFreeRoomsOnLessonTime' method")
    class FindFreeRoomsOnLessonTimeTest {
        @Test
        @DisplayName("when new lesson starts at 14:30 13-06-2021 then return " +
            "list with one room (room_id1)")
        void testReturnOneRoom() {
            LocalDateTime startTime = LocalDateTime.of(2021, 6, 13, 14, 30);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Room> freeRooms = repo.findFreeRoomsOnLessonTime(startTime, endTime);

            assertThat(freeRooms).hasSize(1);
            Room freeRoom = freeRooms.get(0);
            assertThat(freeRoom.getId()).isEqualTo(ROOM_ID1);
            assertThat(freeRoom.getNumber()).isEqualTo(NUMBER_FIRST_ROOM);
        }

        @Test
        @DisplayName("when new lesson starts at 15:45 12-09-2021 then return " +
            "list with two rooms (id1 and id2)")
        void testReturnOneTeacher() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 15, 45);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Room> freeRooms =
                repo.findFreeRoomsOnLessonTime(startTime, endTime);

            assertThat(freeRooms).hasSize(2);
            assertThat(freeRooms).extracting(Room::getId)
                .containsOnly(ROOM_ID1, ROOM_ID2);

        }
    }
}