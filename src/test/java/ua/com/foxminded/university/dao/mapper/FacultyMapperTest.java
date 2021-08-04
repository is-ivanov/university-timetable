package ua.com.foxminded.university.dao.mapper;

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

import ua.com.foxminded.university.domain.entity.Faculty;

@ExtendWith(MockitoExtension.class)
class FacultyMapperTest {

    private static final String ID = "faculty_id";
    private static final String NAME = "faculty_name";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_NAME = "Faculty Test Name";
    private static final int ROW_NUM = 1;

    private FacultyMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new FacultyMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Faculty")
    void testMapRowShoulReturnExpectedFaculty() throws SQLException {
        Faculty expectedFaculty = new Faculty(EXPECTED_ID, EXPECTED_NAME);
        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(NAME)).thenReturn(EXPECTED_NAME);

        Faculty actualFaculty = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedFaculty, actualFaculty);
    }

}
