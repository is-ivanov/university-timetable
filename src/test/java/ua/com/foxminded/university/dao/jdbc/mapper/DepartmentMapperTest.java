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

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;

@ExtendWith(MockitoExtension.class)
class DepartmentMapperTest {

    private static final String ID = "department_id";
    private static final String NAME = "department_name";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_NAME = "Department Test Name";
    private static final int ROW_NUM = 1;

    private DepartmentMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() {
        mapper = new DepartmentMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Department")
    void testMapRowShouldReturnExpectedDepartment() throws SQLException {
        Faculty expectedFaculty = new Faculty();
        Department expectedDepartment = new Department(EXPECTED_ID,
                EXPECTED_NAME, expectedFaculty);
        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(NAME)).thenReturn(EXPECTED_NAME);

        Department actualDepartment = mapper.mapRow(resultSetMock, ROW_NUM);

        assertEquals(expectedDepartment, actualDepartment);
    }

}
