package ua.com.foxminded.university.domain.entity.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import ua.com.foxminded.university.domain.entity.Lesson;

@Component
public class LessonExtractor implements ResultSetExtractor<List<Lesson>> {

    private static final String ID = "id";

    private LessonMapper lessonMapper;
    private StudentMapper studentMapper;

    @Autowired
    public LessonExtractor(LessonMapper lessonMapper,
            StudentMapper studentMapper) {
        this.lessonMapper = lessonMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Lesson> extractData(ResultSet rs)
            throws SQLException, DataAccessException {
        List<Lesson> lessons = new ArrayList<>();
        Lesson currentLesson = null;
        while (rs.next()) {
            int id = rs.getInt(ID);
            if (currentLesson == null) {
                currentLesson = lessonMapper.mapRow(rs, 0);
            } else if (currentLesson.getId() != id) {
                lessons.add(currentLesson);
                currentLesson = lessonMapper.mapRow(rs, 0);
            }
            currentLesson.getStudents().add(studentMapper.mapRow(rs, 0));
        }
        if (currentLesson != null) {
            lessons.add(currentLesson);
        }
        return lessons;
    }


}
