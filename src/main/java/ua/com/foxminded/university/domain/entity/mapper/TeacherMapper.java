package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;

public class TeacherMapper implements RowMapper<Teacher> {
    private static final String ID = "id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PATRONYMIC = "patronymic";
    private static final String DEPARTMENT_ID = "department_id";
    private static final String DEPARTMENT_NAME = "department_name";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";

    @Override
    public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt(ID));
        teacher.setFirstName(rs.getString(FIRST_NAME));
        teacher.setLastName(rs.getString(LAST_NAME));
        teacher.setPatronymic(rs.getString(PATRONYMIC));
        teacher.setDepartment(createDepartment(rs));
        return teacher;
    }

    private Department createDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt(DEPARTMENT_ID));
        department.setName(rs.getString(DEPARTMENT_NAME));
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
