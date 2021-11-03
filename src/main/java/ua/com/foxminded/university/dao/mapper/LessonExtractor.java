package ua.com.foxminded.university.dao.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LessonExtractor implements ResultSetExtractor<List<Lesson>> {

    private static final String LESSON_ID = "lesson_id";
    private static final int ROW_NUM = 0;

    private final LessonMapper lessonMapper;
    private final StudentMapper studentMapper;

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
