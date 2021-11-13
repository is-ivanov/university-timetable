package ua.com.foxminded.university.dao.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonExtractorTest {

    private static final int ROW_NUM = 0;
    private static final String ID = "lesson_id";

    private LessonExtractor lessonExtractor;

    @Mock
    private ResultSet resultSetMock;

    @Mock
    private LessonMapper lessonMapper;

    @Mock
    private StudentMapper studentMapper;

    @BeforeEach
    void setUp() {
        lessonExtractor = new LessonExtractor(lessonMapper, studentMapper);
    }

    @Test
    @DisplayName("when RS.next return one true then should call LessonMapper and StudentMapper once")
    void testNumberCallsLessonMapperStudentMapper() throws SQLException {
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Lesson lesson = new Lesson();
        Set<Student> students = new HashSet<>();
        lesson.setStudents(students);
        when(lessonMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(lesson);
        Student student = new Student();
        when(studentMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(student);
        lessonExtractor.extractData(resultSetMock);
        verify(lessonMapper, times(1)).mapRow(resultSetMock, ROW_NUM);
        verify(studentMapper, times(1)).mapRow(resultSetMock, ROW_NUM);

    }

    @Test
    @DisplayName("when RS contains 2 rows with equals lesson ID then should call LessonMapper once and StudentMapper twice")
    void testNumberCallsLessonMapperOnceStudentMapperTwice()
            throws SQLException {
        when(resultSetMock.next()).thenReturn(true).thenReturn(true)
                .thenReturn(false);
        when(resultSetMock.getInt(ID)).thenReturn(1).thenReturn(1);
        Lesson lesson = new Lesson();
        lesson.setId(1);
        Set<Student> students = new HashSet<>();
        lesson.setStudents(students);
        when(lessonMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(lesson);
        Student student = new Student();
        when(studentMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(student);
        lessonExtractor.extractData(resultSetMock);
        verify(lessonMapper, times(1)).mapRow(resultSetMock, ROW_NUM);
        verify(studentMapper, times(2)).mapRow(resultSetMock, ROW_NUM);

    }

}
