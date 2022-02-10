package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService extends Service<Room> {

    List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                        LocalDateTime endTime);

}
