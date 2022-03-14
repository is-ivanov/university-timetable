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
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private RoomRepository roomRepoMock;

    @BeforeEach
    void setUp(){
        roomService = new RoomServiceImpl(roomRepoMock);
    }

    @Test
    @DisplayName("test 'save' when call method then should call Repository once")
    void testSave_CallDaoOnce() {
        Room room = new Room();
        roomService.create(room);
        verify(roomRepoMock).save(room);
    }

    @Nested
    @DisplayName("test 'findById' method")
    class FindByIdTest {


        @Test
        @DisplayName("when Repository return Optional with Room then method should " +
            "return this Room")
        void testReturnExpectedRoom() {
            Room expectedRoom = new Room();
            expectedRoom.setId(ID1);
            expectedRoom.setNumber(NUMBER_ROOM);
            expectedRoom.setBuilding(BUILDING);
            when(roomRepoMock.findById(ID1)).thenReturn(Optional.of(expectedRoom));
            assertThat(roomService.findById(ID1)).isEqualTo(expectedRoom);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should " +
            "return empty Room")
        void testReturnEmptyRoom() {
            when(roomRepoMock.findById(ID1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> roomService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Room with id(1) not found");
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
        when(roomRepoMock.findAll()).thenReturn(expectedRooms);
        assertThat(roomService.findAll()).isEqualTo(expectedRooms);
    }

}