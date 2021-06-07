package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;

public class FacultyMapper implements RowMapper<Faculty> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DEAN_ID = "dean_id";
    private static final String DEAN_FIRST_NAME = "first_name";
    private static final String DEAN_PATRONYMIC = "patronymic";
    private static final String DEAN_LAST_NAME = "last_name";

    @Override
    public Faculty mapRow(ResultSet rs, int rowNum) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(ID));
        faculty.setName(rs.getString(NAME));

        Teacher dean = createDean(rs);

        faculty.setDean(dean);
        return faculty;
    }

    private Teacher createDean(ResultSet rs) throws SQLException {
        Teacher dean = new Teacher();
        dean.setId(rs.getInt(DEAN_ID));
        dean.setFirstName(rs.getString(DEAN_FIRST_NAME));
        dean.setPatronymic(rs.getString(DEAN_PATRONYMIC));
        dean.setLastName(rs.getString(DEAN_LAST_NAME));
        return dean;
    }

}
