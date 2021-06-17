package ua.com.foxminded.university.domain.entity.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;

@ExtendWith(MockitoExtension.class)
class LessonMapperTest {

    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_START = "time_start";
    private static final String TIME_END = "time_end";
    private static final String EXPECTED_TIME_START = "2021-05-14 14:00:00";
    private static final String EXPECTED_TIME_END = "2021-05-14 15:00:00";
    private static final String ID = "id";
    private static final int EXPECTED_ID = 1;
    private static final int ROW_NUM = 1;

    private LessonMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new LessonMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Lesson with empty List<Student>")
    void testMapRowShouldReturnLessonWithoutStudents() throws SQLException {
        Teacher teacher = new Teacher();
        Course course = new Course();
        Room room = new Room();
        Lesson expectedLesson = new Lesson();
        expectedLesson.setTeacher(teacher);
        expectedLesson.setCourse(course);
        expectedLesson.setRoom(room);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(PATTERN_DATE_TIME);
        expectedLesson.setTimeStart(
                LocalDateTime.parse(EXPECTED_TIME_START, formatter));
        expectedLesson
                .setTimeEnd(LocalDateTime.parse(EXPECTED_TIME_END, formatter));
        expectedLesson.setStudents(new ArrayList<Student>());

        doReturn(EXPECTED_TIME_START).when(resultSetMock).getString(TIME_START);
        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
//        when(resultSetMock.getString(TIME_START))
//                .thenReturn(EXPECTED_TIME_START);
        when(resultSetMock.getString(TIME_END)).thenReturn(EXPECTED_TIME_END);

        Lesson actualLesson = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedLesson, actualLesson);

    }

}
