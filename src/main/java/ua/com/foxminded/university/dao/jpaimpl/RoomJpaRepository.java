package ua.com.foxminded.university.dao.jpaimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.RoomRepository;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DaoException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@PropertySource("classpath:queries/jpql_query.properties")
public class RoomJpaRepository implements RoomRepository {

    public static final String QUERY_GET_ALL = "room.getAll";
    public static final String QUERY_GET_ALL_SORTED_PAGINATED = "room.getAllSortedPaginated";
    public static final String QUERY_DELETE_BY_ID = "room.deleteById";
    public static final String QUERY_GET_FREE_ROOMS = "room.getFreeRoomsOnLessonTime";
    public static final String QUERY_COUNT_ALL = "room.countAll";
    public static final String MESSAGE_DELETE_ROOM_NOT_FOUND = "Can't delete because room id(%d) not found";
    public static final String ROOM_NUMBER = "room_number";

    private final Environment env;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Room room) {
        log.debug("Saving {}", room);
        entityManager.persist(room);
        log.debug("{} saved successfully", room);
    }

    @Override
    public Optional<Room> getById(int id) {
        log.debug("Getting room by id({})", id);
        Room room = entityManager.find(Room.class, id);
        log.debug("Found {}", room);
        return Optional.ofNullable(room);
    }

    @Override
    public List<Room> getAll() {
        log.debug("Getting all rooms");
        List<Room> rooms = entityManager.
            createQuery(env.getProperty(QUERY_GET_ALL), Room.class)
            .getResultList();
        log.debug("Found {} rooms", rooms.size());
        return rooms;
    }

    @Override
    public void update(Room room) {
        entityManager.merge(room);
        log.debug("Update {}", room);
    }

    @Override
    public void delete(Room room) {
        entityManager.remove(room);
        log.debug("{} deleted", room);
    }

    @Override
    public void delete(int id) {
        int rowsDeleted = entityManager
            .createQuery(env.getProperty(QUERY_DELETE_BY_ID))
            .setParameter("id", id)
            .executeUpdate();
        if (rowsDeleted == 0) {
            log.warn("Can't delete room id({})", id);
            throw new DaoException(String
                .format(MESSAGE_DELETE_ROOM_NOT_FOUND, id));
        } else {
            log.debug("Delete room id({})", id);
        }
    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime) {
        log.debug("Getting free rooms from {} to {}", startTime, endTime);
        List<Room> freeRooms = entityManager.createQuery(
                env.getProperty(QUERY_GET_FREE_ROOMS), Room.class)
            .setParameter("time_start", startTime)
            .setParameter("time_end", endTime)
            .getResultList();
        log.debug("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @Override
    public int countAll() {
        log.debug("Count all rooms in database");
        Query query = entityManager.createQuery(env.getProperty(QUERY_COUNT_ALL));
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public Page<Room> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of rooms", pageable.getPageNumber());
        Sort.Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Sort.Order.by(ROOM_NUMBER);
        }
        String queryString = String.format(
            env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
            order.getProperty(), order.getDirection().name());
        TypedQuery<Room> query = entityManager.createQuery(queryString, Room.class);
        List<Room> rooms = query
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
        log.debug("Found {} rooms", rooms.size());
        return new PageImpl<>(rooms, pageable, countAll());
    }
}
