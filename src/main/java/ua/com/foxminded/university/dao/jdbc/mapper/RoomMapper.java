package ua.com.foxminded.university.dao.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Room;

public class RoomMapper implements RowMapper<Room> {

    private static final String ID = "room_id";
    private static final String BUILDING = "building";
    private static final String NUMBER = "room_number";

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt(ID));
        room.setBuilding(rs.getString(BUILDING));
        room.setNumber(rs.getString(NUMBER));
        return room;
    }

}
