package ua.com.foxminded.university.dao.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

@Component
public class StudentMapper implements RowMapper<Student> {

    private static final String STUDENT_ID = "student_id";
    private static final String FIRST_NAME = "student_first_name";
    private static final String LAST_NAME = "student_last_name";
    private static final String PATRONYMIC = "student_patronymic";
    private static final String ACTIVE = "student_active";
    private static final String GROUP_ID = "group_id";
    private static final String GROUP_NAME = "group_name";
    private static final String GROUP_ACTIVE = "group_active";
    private static final String FACULTY_ID = "faculty_id";
    private static final String FACULTY_NAME = "faculty_name";

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt(STUDENT_ID));
        student.setFirstName(rs.getString(FIRST_NAME));
        student.setLastName(rs.getString(LAST_NAME));
        student.setPatronymic(rs.getString(PATRONYMIC));
        student.setActive(rs.getBoolean(ACTIVE));
        student.setGroup(createGroup(rs));
        return student;
    }

    private Group createGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt(GROUP_ID));
        group.setName(rs.getString(GROUP_NAME));
        group.setFaculty(createFaculty(rs));
        group.setActive(rs.getBoolean(GROUP_ACTIVE));
        return group;
    }

    private Faculty createFaculty(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setId(rs.getInt(FACULTY_ID));
        faculty.setName(rs.getString(FACULTY_NAME));
        return faculty;
    }

}
