package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.domain.entity.Room;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class JpaRoomDaoImpl implements RoomDao {

    public static final String QUERY_GET_ALL = "room.getAll";
    public static final String QUERY_GET_ALL_SORTED_PAGINATED = "room.getAllSortedPaginated";
    public static final String QUERY_GET_BY_ID = "room.getById";
    public static final String QUERY_UPDATE = "room.update";
    public static final String QUERY_DELETE = "room.delete";
    public static final String QUERY_GET_FREE_ROOMS = "room.getFreeRoomsOnLessonTime";
    public static final String QUERY_COUNT_ALL = "room.countAll";
    public static final String MESSAGE_ROOM_NOT_FOUND = "Room id(%d) not found";
    public static final String MESSAGE_UPDATE_ROOM_NOT_FOUND = "Can't update because room id(%d) not found";
    public static final String MESSAGE_DELETE_ROOM_NOT_FOUND = "Can't delete because room id(%d) not found";
    public static final String ROOM_NUMBER = "room_number";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Room room) {
        log.debug("Saving {}", room);
        entityManager.persist(room);
        log.info("{} saved successfully", room);
    }

    @Override
    public Optional<Room> getById(int id) {
        log.debug("Getting room by id({})", id);
        Room room = entityManager.find(Room.class, id);
        log.info("Found {}", room);
        return Optional.ofNullable(room);
    }

    @Override
    public List<Room> getAll() {
        return null;
    }

    @Override
    public void update(Room room) {

    }

    @Override
    public void delete(Room room) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }

    @Override
    public int countAll() {
        return 0;
    }

    @Override
    public Page<Room> getAllSortedPaginated(Pageable pageable) {
        return null;
    }
}
