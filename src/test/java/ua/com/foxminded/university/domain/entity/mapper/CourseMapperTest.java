package ua.com.foxminded.university.domain.entity.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.domain.entity.Course;

@ExtendWith(MockitoExtension.class)
class CourseMapperTest {

    private static final String ID = "course_id";
    private static final String NAME = "course_name";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_NAME = "Course Test Name";

    private CourseMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new CourseMapper();
    }

    @Test
    @DisplayName("test should return expected Course")
    void testMapRowShouldReturnExtendedCourse() throws SQLException {

        Course expectedCourse = new Course(EXPECTED_ID, EXPECTED_NAME);
        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(NAME)).thenReturn(EXPECTED_NAME);
        int rowNum = 1;

        Course actualCourse = mapper.mapRow(resultSetMock, rowNum);

        assertEquals(expectedCourse, actualCourse);
    }

}
