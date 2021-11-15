package ua.com.foxminded.university.dao.jdbc.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.dao.jdbc.mapper.RoomMapper;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DaoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class RoomDaoImpl implements RoomDao {

    public static final String QUERY_ADD = "room.add";
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

    private final JdbcTemplate jdbcTemplate;
    private final Environment env;

    @Override
    public void add(Room room) {
        log.debug("Adding {}", room);
        try {
            jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                room.getBuilding(), room.getNumber());
        } catch (DataAccessException e) {
            log.error("An error occurred while adding the {}", room, e);
            throw new DaoException(e.getMessage(), e);
        }
        log.info("{} added successfully", room);
    }

    @Override
    public Optional<Room> getById(int id) {
        log.debug("Getting room by id({})", id);
        Room result;
        try {
            result = jdbcTemplate.queryForObject(
                env.getRequiredProperty(QUERY_GET_BY_ID), new RoomMapper(),
                id);
        } catch (DataAccessException e) {
            log.error("Room id({}) not found", id, e);
            throw new DaoException(String.format(MESSAGE_ROOM_NOT_FOUND, id), e);
        }
        log.info("Found {}", result);
        return Optional.ofNullable(result);
    }

    @Override
    public List<Room> getAll() {
        log.debug("Getting all rooms");
        List<Room> rooms = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_ALL), new RoomMapper());
        log.info("Found {} rooms", rooms.size());
        return rooms;
    }

    @Override
    public void update(Room room) {
        log.debug("Updating {}", room);
        int numberUpdatedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_UPDATE),
            room.getBuilding(), room.getNumber(), room.getId());
        if (numberUpdatedRows == 0) {
            log.warn("Can't update {}", room);
            throw new DaoException(String.format(MESSAGE_UPDATE_ROOM_NOT_FOUND,
                room.getId()));
        } else {
            log.info("Update {}", room);
        }
    }

    @Override
    public void delete(Room room) {
        log.debug("Deleting {}", room);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), room.getId());
        if (numberDeletedRows == 0) {
            log.warn("Can't delete {}", room);
            throw new DaoException(String.format(MESSAGE_DELETE_ROOM_NOT_FOUND,
                room.getId()));
        } else {
            log.info("Delete {}", room);
        }
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting room id({})", id);
        int numberDeletedRows = jdbcTemplate.update(
            env.getRequiredProperty(QUERY_DELETE), id);
        if (numberDeletedRows == 0) {
            log.warn("Can't delete room id({})", id);
            throw new DaoException(String.format(MESSAGE_DELETE_ROOM_NOT_FOUND,
                id));
        } else {
            log.info("Delete room id({})", id);
        }
    }

    @Override
    public List<Room> getFreeRoomsOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime) {
        log.debug("Getting free rooms from {} to {}", startTime, endTime);
        List<Room> freeRooms = jdbcTemplate.query(
            env.getRequiredProperty(QUERY_GET_FREE_ROOMS), new RoomMapper(),
            startTime, endTime);
        log.info("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @Override
    public int countAll() {
        log.debug("Count all rooms in database");
        Integer result = jdbcTemplate.queryForObject(
            env.getRequiredProperty(QUERY_COUNT_ALL), Integer.class);
        log.info("{} rooms", result);
        return (result != null ? result : 0);
    }

    @Override
    public Page<Room> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of rooms", pageable.getPageNumber());
        Order order;
        if (!pageable.getSort().isEmpty()) {
            order = pageable.getSort().toList().get(0);
        } else {
            order = Order.by(ROOM_NUMBER);
        }
        String query = String.format(env.getRequiredProperty(QUERY_GET_ALL_SORTED_PAGINATED),
            order.getProperty(), order.getDirection().name(),
            pageable.getOffset(), pageable.getPageSize());
        List<Room> rooms = jdbcTemplate.query(query, new RoomMapper());
        log.info("Found {} rooms", rooms.size());
        return new PageImpl<>(rooms, pageable, countAll());
    }

}