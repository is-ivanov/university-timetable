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

import ua.com.foxminded.university.domain.entity.Room;

@ExtendWith(MockitoExtension.class)
class RoomMapperTest {

    private static final String ID = "room_id";
    private static final String NUMBER = "room_number";
    private static final String BUILDING = "building";
    private static final int EXPECTED_ID = 1;
    private static final String EXPECTED_ROOM_NUMBER = "111b";
    private static final String EXPECTED_BUILDING = "test building";
    private static final int ROW_NUM = 1;

    private RoomMapper mapper;

    @Mock
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() {
        mapper = new RoomMapper();
    }

    @Test
    @DisplayName("test mapRow should return expected Room")
    void testMapRowShouldReturnExpectedRoom() throws SQLException {
        Room expectedRoom = new Room(EXPECTED_ID, EXPECTED_BUILDING,
                EXPECTED_ROOM_NUMBER);
        when(resultSetMock.getInt(ID)).thenReturn(EXPECTED_ID);
        when(resultSetMock.getString(BUILDING)).thenReturn(EXPECTED_BUILDING);
        when(resultSetMock.getString(NUMBER)).thenReturn(EXPECTED_ROOM_NUMBER);

        Room actualRoom = mapper.mapRow(resultSetMock, ROW_NUM);

        assertEquals(expectedRoom, actualRoom);
    }

}
