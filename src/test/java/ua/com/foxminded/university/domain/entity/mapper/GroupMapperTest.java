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

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;

@ExtendWith(MockitoExtension.class)
class GroupMapperTest {

    private static final String ID = "id";
    private static final String NAME = "group_name";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_NAME = "Group Test Name";
    private static final int ROW_NUM = 1;

    private GroupMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new GroupMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Group")
    void testMapRowShoulReturnExpectedGroup() throws SQLException {
        Faculty faculty = new Faculty();
        Group expectedGroup = new Group(EXPECTED_ID, EXPECTED_NAME,
                faculty);

        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(NAME)).thenReturn(EXPECTED_NAME);

        Group actualGroup = mapper.mapRow(resultSetMock, ROW_NUM);
        assertEquals(expectedGroup, actualGroup);
    }

}
