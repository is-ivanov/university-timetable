package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Qualifier("jpaRoomDaoImpl")
    private final RoomDao roomDao;

    @Override
    public void add(Room room) {
        log.debug("Adding {}", room);
        roomDao.add(room);
        log.info("{} added successfully", room);
    }

    @Override
    public Room getById(int id) {
        log.debug("Getting room by id({})", id);
        Room room = roomDao.getById(id).orElse(new Room());
        log.info("Found {}", room);
        return room;
    }

    @Override
    public List<Room> getAll() {
        log.debug("Getting all rooms");
        List<Room> rooms = roomDao.getAll();
        log.info("Found {} rooms", rooms.size());
        return rooms;
    }

    @Override
    public void update(Room room) {
        log.debug("Updating {}", room);
        roomDao.update(room);
        log.info("Update {}", room);
    }

    @Override
    public void delete(Room room) {
        log.debug("Deleting {}", room);
        roomDao.delete(room);
        log.info("Delete {}", room);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting room id({})", id);
        roomDao.delete(id);
        log.info("Delete room id({})", id);
    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime) {
        log.debug("Getting free rooms from {} to {}", startTime, endTime);
        List<Room> freeRooms = roomDao.getFreeRoomsOnLessonTime(startTime, endTime);
        log.info("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @Override
    public Page<Room> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of rooms", pageable.getPageNumber());
        Page<Room> pageRooms = roomDao.getAllSortedPaginated(pageable);
        log.info("Found {} rooms on page {}", pageRooms.getContent().size(),
            pageRooms.getNumber());
        return pageRooms;
    }
}