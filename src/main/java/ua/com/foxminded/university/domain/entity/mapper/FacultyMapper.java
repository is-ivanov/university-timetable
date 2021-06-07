package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;

public class FacultyMapper implements RowMapper<Faculty> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ID_DEAN = "dean_id";
    private static final String FIRST_NAME_DEAN = "first_name";
    private static final String PATRONYMIC_DEAN = "patronymic";
    private static final String LAST_NAME_DEAN = "last_name";

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
        dean.setId(rs.getInt(ID_DEAN));
        dean.setFirstName(rs.getString(FIRST_NAME_DEAN));
        dean.setPatronymic(rs.getString(PATRONYMIC_DEAN));
        dean.setLastName(rs.getString(LAST_NAME_DEAN));
        return dean;
    }

}
