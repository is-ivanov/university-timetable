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

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;

@ExtendWith(MockitoExtension.class)
class TeacherMapperTest {

    private static final String ID = "teacher_id";
    private static final String FIRST_NAME = "teacher_first_name";
    private static final String LAST_NAME = "teacher_last_name";
    private static final String PATRONYMIC = "teacher_patronymic";
    private static final String ACTIVE = "teacher_active";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_FIRST_NAME = "Ivan";
    private static final String EXPECTED_LAST_NAME = "Ivanov";
    private static final String EXPECTED_PATRONYMIC = "Ivanovich";
    private static final boolean EXPECTED_ACTIVE = true;
    private static final int ROW_NUM = 1;

    private TeacherMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new TeacherMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Teacher")
    void testMapRowShouldReturnExpectedTeacher() throws SQLException {
        Faculty expectedFaculty = new Faculty();
        Department expectedDepartment = new Department();
        expectedDepartment.setFaculty(expectedFaculty);
        Teacher expectedTeacher = new Teacher();
        expectedTeacher.setId(EXPECTED_ID);
        expectedTeacher.setFirstName(EXPECTED_FIRST_NAME);
        expectedTeacher.setLastName(EXPECTED_LAST_NAME);
        expectedTeacher.setPatronymic(EXPECTED_PATRONYMIC);
        expectedTeacher.setActive(EXPECTED_ACTIVE);
        expectedTeacher.setDepartment(expectedDepartment);

        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(FIRST_NAME))
                .thenReturn(EXPECTED_FIRST_NAME);
        when(resultSetMock.getString(LAST_NAME)).thenReturn(EXPECTED_LAST_NAME);
        when(resultSetMock.getString(PATRONYMIC))
                .thenReturn(EXPECTED_PATRONYMIC);
        when(resultSetMock.getBoolean(ACTIVE)).thenReturn(EXPECTED_ACTIVE);
        Teacher actualTeacher = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedTeacher, actualTeacher);
    }

}
