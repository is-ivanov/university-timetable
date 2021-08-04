package ua.com.foxminded.university.dao.mapper;

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

    private static final String LESSON_ID = "lesson_id";
    private static final int ROW_NUM = 0;

    private final LessonMapper lessonMapper;
    private final StudentMapper studentMapper;

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
            int id = rs.getInt(LESSON_ID);
            if (currentLesson == null) {
                currentLesson = lessonMapper.mapRow(rs, ROW_NUM);
            } else if (currentLesson.getId() != id) {
                lessons.add(currentLesson);
                currentLesson = lessonMapper.mapRow(rs, ROW_NUM);
            }
            if (currentLesson != null) {
                currentLesson.getStudents().add(studentMapper.mapRow(rs, ROW_NUM));
            }
        }
        if (currentLesson != null) {
            lessons.add(currentLesson);
        }
        return lessons;
    }


}
