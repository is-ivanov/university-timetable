package ua.com.foxminded.university.dao.jdbc.mapper;

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
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

@ExtendWith(MockitoExtension.class)
class StudentMapperTest {

    private static final String ID = "student_id";
    private static final String FIRST_NAME = "student_first_name";
    private static final String LAST_NAME = "student_last_name";
    private static final String PATRONYMIC = "student_patronymic";
    private static final String ACTIVE = "student_active";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_FIRST_NAME = "Ivan";
    private static final String EXPECTED_LAST_NAME = "Ivanov";
    private static final String EXPECTED_PATRONYMIC = "Ivanovich";
    private static final boolean EXPECTED_ACTIVE = true;
    private static final int ROW_NUM = 1;

    private StudentMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() {
        mapper = new StudentMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Student")
    void testMapRowShouldReturnExpectedStudent() throws SQLException {
        Faculty expectedFaculty = new Faculty();
        Group expectedGroup = new Group();
        expectedGroup.setFaculty(expectedFaculty);
        Student expectedStudent = new Student();
        expectedStudent.setId(EXPECTED_ID);
        expectedStudent.setFirstName(EXPECTED_FIRST_NAME);
        expectedStudent.setLastName(EXPECTED_LAST_NAME);
        expectedStudent.setPatronymic(EXPECTED_PATRONYMIC);
        expectedStudent.setActive(EXPECTED_ACTIVE);
        expectedStudent.setGroup(expectedGroup);

        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(FIRST_NAME))
                .thenReturn(EXPECTED_FIRST_NAME);
        when(resultSetMock.getString(LAST_NAME)).thenReturn(EXPECTED_LAST_NAME);
        when(resultSetMock.getString(PATRONYMIC))
                .thenReturn(EXPECTED_PATRONYMIC);
        when(resultSetMock.getBoolean(ACTIVE)).thenReturn(EXPECTED_ACTIVE);
        Student actualStudent = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedStudent, actualStudent);
    }

}
