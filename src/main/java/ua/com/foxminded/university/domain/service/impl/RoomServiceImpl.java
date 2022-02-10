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
public class RoomServiceImpl extends AbstractService<Room> implements RoomService {

    private final RoomRepository roomRepo;

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
        List<Room> freeRooms;
        if (busyRooms.isEmpty()) {
            freeRooms = findAll();
        } else {
            List<Integer> busyRoomIds = EntityUtil.extractIdsFromEntities(busyRooms);
            freeRooms =
                roomRepo.findByIdNotInOrderByBuildingAscNumberAsc(busyRoomIds);
        }
        log.debug("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    private List<Room> findBusyRoomsOnTime(LocalDateTime from, LocalDateTime to) {
        log.debug("Getting busy rooms from {} to {}", from, to);
        return roomRepo.findBusyRoomsOnTime(from, to);
    }

}