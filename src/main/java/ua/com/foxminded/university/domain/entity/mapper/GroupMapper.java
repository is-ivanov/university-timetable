package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;

public class GroupMapper implements RowMapper<Group> {

    private static final String ID = "id";
    private static final String GROUP_NAME = "group_name";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt(ID));
        group.setName(rs.getString(GROUP_NAME));
        group.setFaculty(createFaculty(rs));
        return group;
    }

    private Faculty createFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(FACULTY_ID));
        faculty.setName(rs.getString(FACULTY_NAME));
        return faculty;
    }
}
