package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;

public class StudentMapper implements RowMapper<Student> {

    private static final String ID = "id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PATRONYMIC = "patronymic";
    private static final String GROUP_ID = "group_id";
    private static final String GROUP_NAME = "group_name";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";
    private static final String DEAN_ID = "dean_id";
    private static final String FIRST_NAME_DEAN = "dean_first_name";
    private static final String PATRONYMIC_DEAN = "dean_patronymic";
    private static final String LAST_NAME_DEAN = "dean_last_name";

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt(ID));
        student.setFirstName(rs.getString(FIRST_NAME));
        student.setLastName(rs.getString(LAST_NAME));
        student.setPatronymic(rs.getString(PATRONYMIC));
        student.setGroup(createGroup(rs));

        return student;
    }

    private Group createGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt(GROUP_ID));
        group.setName(rs.getString(GROUP_NAME));
        group.setFaculty(createFaculty(rs));
        return group;
    }

    private Faculty createFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(FACULTY_ID));
        faculty.setName(rs.getString(FACULTY_NAME));
        faculty.setDean(createDean(rs));
        return faculty;
    }

    private Teacher createDean(ResultSet rs) throws SQLException {
        Teacher dean = new Teacher();
        Integer id = rs.getInt(DEAN_ID);
        if (rs.wasNull()) {
            id = null;
        }
        dean.setId(id);
        dean.setFirstName(rs.getString(FIRST_NAME_DEAN));
        dean.setPatronymic(rs.getString(PATRONYMIC_DEAN));
        dean.setLastName(rs.getString(LAST_NAME_DEAN));
        return dean;
    }
}
