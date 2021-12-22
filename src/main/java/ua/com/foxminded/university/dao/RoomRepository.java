package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r " +
           "FROM Room r " +
                "LEFT JOIN Lesson l ON l.room = r " +
           "WHERE l.timeEnd >= :from " +
             "AND l.timeStart <= :to ")
    List<Room> findBusyRoomsOnTime(LocalDateTime from,
                                   LocalDateTime to);

    List<Room> findByIdNotInOrderByBuildingAscNumberAsc(Collection<Integer> ids);

}
