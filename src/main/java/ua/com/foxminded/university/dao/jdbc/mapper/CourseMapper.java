package ua.com.foxminded.university.dao.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Course;

public class CourseMapper implements RowMapper<Course> {

    private static final String ID = "course_id";
    private static final String NAME = "course_name";

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt(ID));
        course.setName(rs.getString(NAME));
        return course;
    }

}
