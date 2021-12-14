package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.RoomRepository;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private static final String MESSAGE_ROOM_NOT_FOUND = "Room id(%d) not found";

    private final RoomRepository roomRepository;

    @Override
    public void add(Room room) {
        log.debug("Adding {}", room);
        roomRepository.save(room);
        log.debug("{} added successfully", room);
    }

    @Override
    public Room getById(int id) {
        log.debug("Getting room by id({})", id);
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_ROOM_NOT_FOUND, id)));
        log.debug("Found {}", room);
        return room;
    }

    @Override
    public List<Room> getAll() {
        log.debug("Getting all rooms");
        List<Room> rooms = roomRepository.findAll();
        log.debug("Found {} rooms", rooms.size());
        return rooms;
    }

    @Override
    public void update(Room room) {
        log.debug("Updating {}", room);
        roomRepository.save(room);
        log.debug("Update {}", room);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting room id({})", id);
        roomRepository.deleteById(id);
        log.debug("Delete room id({})", id);
    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime) {
        log.debug("Getting free rooms from {} to {}", startTime, endTime);
        List<Room> freeRooms = roomRepository.findFreeRoomsOnLessonTime(startTime, endTime);
        log.debug("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @Override
    public Page<Room> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of rooms", pageable.getPageNumber());
        Page<Room> pageRooms = roomRepository.findAll(pageable);
        log.debug("Found {} rooms on page {}", pageRooms.getContent().size(),
            pageRooms.getNumber());
        return pageRooms;
    }
}