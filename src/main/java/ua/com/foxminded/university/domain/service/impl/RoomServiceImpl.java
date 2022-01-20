package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.RoomRepository;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.domain.util.EntityUtil;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomServiceImpl  extends AbstractService<Room> implements RoomService {

    private static final String MESSAGE_ROOM_NOT_FOUND = "Room id(%d) not found";

    private final RoomRepository roomRepo;

//    @Override
//    public Room save(Room room) {
//        log.debug("Saving {}", room);
//        return roomRepo.save(room);
//    }
//
//    @Override
//    public Room getById(int id) {
//        log.debug("Getting room by id({})", id);
//        Room room = roomRepo.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException(
//                String.format(MESSAGE_ROOM_NOT_FOUND, id)));
//        log.debug("Found {}", room);
//        return room;
//    }
//
//    @Override
//    public List<Room> getAll() {
//        log.debug("Getting all rooms");
//        List<Room> rooms = roomRepo.findAll();
//        log.debug("Found {} rooms", rooms.size());
//        return rooms;
//    }
//
//    @Override
//    public void delete(int id) {
//        log.debug("Deleting room id({})", id);
//        roomRepo.deleteById(id);
//        log.debug("Delete room id({})", id);
//    }

    @Override
    protected JpaRepository<Room, Integer> getRepo() {
        return roomRepo;
    }

    @Override
    protected String getEntityName() {
        return Room.class.getSimpleName();
    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime) {
        log.debug("Getting free rooms from {} to {}", startTime, endTime);
        List<Room> busyRooms = findBusyRoomsOnTime(startTime, endTime);
        List<Integer> busyRoomIds = EntityUtil.extractIdsFromEntities(busyRooms);
        List<Room> freeRooms =
            roomRepo.findByIdNotInOrderByBuildingAscNumberAsc(busyRoomIds);
        log.debug("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

//    @Override
//    public Page<Room> getAllSortedPaginated(Pageable pageable) {
//        log.debug("Getting sorted page {} from list of rooms", pageable.getPageNumber());
//        Page<Room> pageRooms = roomRepo.findAll(pageable);
//        log.debug("Found {} rooms on page {}", pageRooms.getContent().size(),
//            pageRooms.getNumber());
//        return pageRooms;
//    }

    private List<Room> findBusyRoomsOnTime(LocalDateTime from, LocalDateTime to) {
        log.debug("Getting busy rooms from {} to {}", from, to);
        return roomRepo.findBusyRoomsOnTime(from, to);
    }

//    private List<Integer> getIdsFromRooms(List<Room> rooms) {
//        return rooms.stream()
//            .map(Room::getId)
//            .collect(Collectors.toList());
//    }

}