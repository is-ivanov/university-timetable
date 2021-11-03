package ua.com.foxminded.university.dao.mapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;

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
        List<Student> students = new ArrayList<>();
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
        List<Student> students = new ArrayList<>();
        lesson.setStudents(students);
        when(lessonMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(lesson);
        Student student = new Student();
        when(studentMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(student);
        lessonExtractor.extractData(resultSetMock);
        verify(lessonMapper, times(1)).mapRow(resultSetMock, ROW_NUM);
        verify(studentMapper, times(2)).mapRow(resultSetMock, ROW_NUM);

    }

}
