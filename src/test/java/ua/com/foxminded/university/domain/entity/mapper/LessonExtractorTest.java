package ua.com.foxminded.university.domain.entity.mapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    private final int ROW_NUM = 1;

    private LessonExtractor lessonExtractor;

    @Mock
    private ResultSet resultSetMock;

    @Mock
    private LessonMapper lessonMapper;

    @Mock
    private StudentMapper studentMapper;

    @BeforeEach
    void setUp() throws Exception {
        lessonExtractor = new LessonExtractor(lessonMapper, studentMapper);
    }

    @Test
    @DisplayName("when RS.next return one true then should call LessonMapper and StudentMapper once")
    void testNumberCallsLessonMapperStudentMapper() throws SQLException {
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        Lesson lesson = new Lesson();
        when(lessonMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(lesson);
        Student student = new Student();
        when(studentMapper.mapRow(resultSetMock, ROW_NUM)).thenReturn(student);
        lessonExtractor.extractData(resultSetMock);
        verify(lessonMapper, times(1)).mapRow(resultSetMock, ROW_NUM);
        verify(studentMapper, times(1)).mapRow(resultSetMock, ROW_NUM);

    }

}
