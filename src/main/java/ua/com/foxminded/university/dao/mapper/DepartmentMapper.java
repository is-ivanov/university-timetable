package ua.com.foxminded.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;

public class DepartmentMapper implements RowMapper<Department> {

    private static final String ID = "department_id";
    private static final String NAME = "department_name";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt(ID));
        department.setName(rs.getString(NAME));
        department.setFaculty(createFaculty(rs));
        return department;
    }

    private Faculty createFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(FACULTY_ID));
        faculty.setName(rs.getString(FACULTY_NAME));
        return faculty;
    }

}
