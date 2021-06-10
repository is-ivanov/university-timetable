package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;

public class DepartmentMapper implements RowMapper<Department> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ID_DEAN = "dean_id";
    private static final String FIRST_NAME_DEAN = "first_name";
    private static final String PATRONYMIC_DEAN = "patronymic";
    private static final String LAST_NAME_DEAN = "last_name";

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {

        return null;
    }

    private Teacher createHead(ResultSet rs) {
        return null;
    }

    private Faculty createFaculty(ResultSet rs) {
        return null;
    }
}
