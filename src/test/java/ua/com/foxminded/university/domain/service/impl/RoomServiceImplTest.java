package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    private RoomServiceImpl roomService;

    @Mock
    private RoomDao roomDaoMock;

    @BeforeEach
    void setUp(){
        roomService = new RoomServiceImpl(roomDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Room room = new Room();
        roomService.add(room);
        verify(roomDaoMock).add(room);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Room then method should " +
            "return this Room")
        void testReturnExpectedRoom() throws Exception {
            Room expectedRoom = new Room();
            expectedRoom.setId(1);
            expectedRoom.setNumber("123b");
            expectedRoom.setBuilding("453C");
            when(roomDaoMock.getById(1)).thenReturn(Optional.of(expectedRoom));
            assertEquals(expectedRoom, roomService.getById(1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Room")
        void testReturnEmptyRoom() throws Exception {
            Optional<Room> optional = Optional.empty();
            when(roomDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Room(), roomService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao throw DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception {
            when(roomDaoMock.getById(anyInt())).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> roomService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List rooms method should " +
        "return this List")
    void testGetAll_ReturnListRooms() {
        Room room1 = new Room();
        room1.setId(1);
        Room room2 = new Room();
        room1.setId(2);
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(room1);
        expectedRooms.add(room2);
        when(roomDaoMock.getAll()).thenReturn(expectedRooms);
        assertEquals(expectedRooms, roomService.getAll());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}