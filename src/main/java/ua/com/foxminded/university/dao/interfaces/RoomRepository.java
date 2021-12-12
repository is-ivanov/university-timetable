package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r " +
        "FROM Room r " +
        "WHERE r.id NOT IN " +
        "( " +
        "SELECT r2.id " +
        "FROM Room r2 " +
        "LEFT JOIN Lesson l ON r2 = l.room " +
        "WHERE l.timeEnd >= :startTime " +
        "AND l.timeStart <= :endTime " +
        ") " +
        "ORDER BY r.building, r.number")
    List<Room> findFreeRoomsOnLessonTime(LocalDateTime startTime,
                                         LocalDateTime endTime);
}
