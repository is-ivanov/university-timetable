package ua.com.foxminded.university.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.dao.mapper.RoomMapper;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DAOException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
@PropertySource("classpath:sql_query.properties")
public class RoomDaoImpl implements RoomDao {

    private static final String QUERY_ADD = "room.add";
    private static final String QUERY_GET_ALL = "room.getAll";
    private static final String QUERY_GET_BY_ID = "room.getById";
    private static final String QUERY_UPDATE = "room.update";
    private static final String QUERY_DELETE = "room.delete";
    private static final String MESSAGE_ROOM_NOT_FOUND = "Room id(%d) not found";
    private static final String MESSAGE_UPDATE_ROOM_NOT_FOUND = "Can't update because room id(%d) not found";
    private static final String MESSAGE_DELETE_ROOM_NOT_FOUND = "Can't delete because room id(%d) not found";

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
            throw new DAOException(e.getMessage(), e);
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
            throw new DAOException(String.format(MESSAGE_ROOM_NOT_FOUND, id), e);
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
            throw new DAOException(String.format(MESSAGE_UPDATE_ROOM_NOT_FOUND,
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
            throw new DAOException(String.format(MESSAGE_DELETE_ROOM_NOT_FOUND,
                room.getId()));
        } else {
            log.info("Delete {}", room);
        }
    }

}