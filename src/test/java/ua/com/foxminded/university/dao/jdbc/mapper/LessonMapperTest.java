package ua.com.foxminded.university.dao.jdbc.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.domain.entity.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMapperTest {

    private static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_START = "time_start";
    private static final String TIME_END = "time_end";
    private static final String EXPECTED_STRING_TIME_START = "2021-05-14 14:00:00";
    private static final String EXPECTED_STRING_TIME_END = "2021-05-14 15:00:00";
    private static final String LESSON_ID = "lesson_id";
    private static final String TEACHER_ID = "teacher_id";
    private static final int EXPECTED_ID = 1;
    private static final int ROW_NUM = 1;

    private LessonMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() {
        mapper = new LessonMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Lesson with empty List<Student>")
    void testMapRowShouldReturnLessonWithoutStudents() throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(EXPECTED_ID);
        Department department = new Department();
        Faculty faculty = new Faculty();
        department.setFaculty(faculty);
        teacher.setDepartment(department);
        Course course = new Course();
        Room room = new Room();
        Lesson expectedLesson = new Lesson();
        expectedLesson.setId(EXPECTED_ID);
        expectedLesson.setTeacher(teacher);
        expectedLesson.setCourse(course);
        expectedLesson.setRoom(room);
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(PATTERN_DATE_TIME);
        LocalDateTime expectedTimeStart = LocalDateTime
                .parse(EXPECTED_STRING_TIME_START, formatter);
        LocalDateTime expectedTimeEnd = LocalDateTime
                .parse(EXPECTED_STRING_TIME_END, formatter);
        expectedLesson.setTimeStart(expectedTimeStart);
        expectedLesson.setTimeEnd(expectedTimeEnd);
        expectedLesson.setStudents(new HashSet<>());

        when(resultSetMock.getInt(TEACHER_ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getInt(LESSON_ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getTimestamp(TIME_START))
                .thenReturn(Timestamp.valueOf(expectedTimeStart));
        when(resultSetMock.getTimestamp(TIME_END))
                .thenReturn(Timestamp.valueOf(expectedTimeEnd));

        Lesson actualLesson = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedLesson, actualLesson);

    }

}
