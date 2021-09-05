package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomDao extends Dao<Room> {

    List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                        LocalDateTime endTime);
}
