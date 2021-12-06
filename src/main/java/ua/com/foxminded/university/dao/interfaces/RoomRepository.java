package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends Repository<Room> {

    List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                        LocalDateTime endTime);

    int countAll();

    Page<Room> getAllSortedPaginated(Pageable pageable);
}
