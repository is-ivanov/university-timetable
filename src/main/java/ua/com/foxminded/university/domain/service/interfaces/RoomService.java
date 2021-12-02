package ua.com.foxminded.university.domain.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService extends Service<Room, Room> {

    List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                        LocalDateTime endTime);

    Page<Room> getAllSortedPaginated(Pageable pageable);
}
