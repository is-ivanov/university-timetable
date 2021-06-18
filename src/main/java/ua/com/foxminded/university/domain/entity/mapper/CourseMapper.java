package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import ua.com.foxminded.university.domain.entity.Course;

public class CourseMapper implements RowMapper<Course> {

    private static final String NAME = "name";
    private static final String ID = "id";

    @Override
    public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt(ID));
        course.setName(rs.getString(NAME));
        return course;
    }

}
