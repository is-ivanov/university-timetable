package ua.com.foxminded.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.mapper.RoomMapper;
import ua.com.foxminded.university.exception.DAOException;

@Component
@PropertySource("classpath:sql_query.properties")
public class RoomDaoImpl implements RoomDao {

    private static final String QUERY_ADD = "room.add";
    private static final String QUERY_GET_ALL = "room.getAll";
    private static final String QUERY_GET_BY_ID = "room.getById";
    private static final String QUERY_UPDATE = "room.update";
    private static final String QUERY_DELETE = "room.delete";
    private static final String MESSAGE_ROOM_NOT_FOUND = "Room not found: ";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RoomDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private Environment env;

    @Override
    public void add(Room room) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_ADD),
                room.getBuilding(), room.getNumber());
    }

    @Override
    public Optional<Room> getById(int id) throws DAOException {
        Room result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    env.getRequiredProperty(QUERY_GET_BY_ID), new RoomMapper(),
                    id);
        } catch (DataAccessException e) {
            throw new DAOException(MESSAGE_ROOM_NOT_FOUND + id, e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Room> getAll() {
        return jdbcTemplate.query(env.getRequiredProperty(QUERY_GET_ALL),
                new RoomMapper());
    }

    @Override
    public void update(Room room) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_UPDATE),
                room.getBuilding(), room.getNumber(), room.getId());
    }

    @Override
    public void delete(Room room) {
        jdbcTemplate.update(env.getRequiredProperty(QUERY_DELETE),
                room.getId());
    }

}
