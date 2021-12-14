package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.RoomRepository;
import ua.com.foxminded.university.domain.entity.Room;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String NUMBER_ROOM = "123b";
    public static final String BUILDING = "453C";

    private RoomServiceImpl roomService;

    @Mock
    private RoomRepository roomRepositoryMock;

    @BeforeEach
    void setUp(){
        roomService = new RoomServiceImpl(roomRepositoryMock);
    }

    @Test
    @DisplayName("test 'add' when call method then should call Repository once")
    void testAdd_CallDaoOnce() {
        Room room = new Room();
        roomService.add(room);
        verify(roomRepositoryMock).save(room);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {


        @Test
        @DisplayName("when Repository return Optional with Room then method should " +
            "return this Room")
        void testReturnExpectedRoom() {
            Room expectedRoom = new Room();
            expectedRoom.setId(ID1);
            expectedRoom.setNumber(NUMBER_ROOM);
            expectedRoom.setBuilding(BUILDING);
            when(roomRepositoryMock.findById(ID1)).thenReturn(Optional.of(expectedRoom));
            assertEquals(expectedRoom, roomService.getById(ID1));
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should " +
            "return empty Room")
        void testReturnEmptyRoom() {
            when(roomRepositoryMock.findById(ID1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> roomService.getById(ID1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Room id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'getAll' when Repository return List rooms method should " +
        "return this List")
    void testGetAll_ReturnListRooms() {
        Room room1 = new Room();
        room1.setId(ID1);
        Room room2 = new Room();
        room1.setId(ID2);
        List<Room> expectedRooms = new ArrayList<>();
        expectedRooms.add(room1);
        expectedRooms.add(room2);
        when(roomRepositoryMock.findAll()).thenReturn(expectedRooms);
        assertEquals(expectedRooms, roomService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "roomDao once")
    void testUpdate_CallDaoOnce() {
        Room room = new Room();
        roomService.update(room);
        verify(roomRepositoryMock).save(room);
    }

}