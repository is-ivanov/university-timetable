package ua.com.foxminded.university.dao.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Faculty;

public class FacultyMapper implements RowMapper<Faculty> {

    private static final String ID = "faculty_id";
    private static final String NAME = "faculty_name";

    @Override
    public Faculty mapRow(ResultSet rs, int rowNum) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(ID));
        faculty.setName(rs.getString(NAME));

        return faculty;
    }
}
