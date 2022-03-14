package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.RoomAssert;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.springconfig.TestMapperConfig;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.util.MappingConstants.*;

@WebMvcTest(RoomRestController.class)
@Import(TestMapperConfig.class)
class RoomRestControllerTest {

    public static final LocalDateTime START_TIME_TIMETABLE =
        LocalDateTime.of(2021, 9, 27, 0, 0, 0);
    public static final LocalDateTime END_TIME_TIMETABLE =
        LocalDateTime.of(2021, 11, 8, 0, 0, 0);
    public static final String START_TIME_TIMETABLE_ISO = "2021-09-27T00:00:00+03:00";
    public static final String END_TIME_TIMETABLE_ISO = "2021-11-08T00:00:00+03:00";

    @Captor
    ArgumentCaptor<Room> roomCaptor;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomServiceMock;

    @MockBean
    private LessonService lessonServiceMock;

    @Nested
    @DisplayName("test 'getRooms' method")
    class GetRoomsTest {
        @Test
        @DisplayName("when GET request without parameters then should return " +
            "all rooms with status OK")
        void getRequestWithoutParameters() throws Exception {
            List<Room> rooms = createTestRooms();

            when(roomServiceMock.findAll()).thenReturn(rooms);

            mockMvc.perform(get(MappingConstants.API_ROOMS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.rooms", hasSize(2)),
                    jsonPath("$._embedded.rooms[0].id",
                        is(ROOM_ID1)),
                    jsonPath("$._embedded.rooms[0].number",
                        is(NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.rooms[0].building",
                        is(BUILDING_FIRST_ROOM)),
                    jsonPath("$._embedded.rooms[0].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.rooms[0]._links.self.href",
                        is(ROOM1_SELF_LINK)),
                    jsonPath("$._embedded.rooms[1].id",
                        is(ROOM_ID2)),
                    jsonPath("$._embedded.rooms[1].number",
                        is(NUMBER_SECOND_ROOM)),
                    jsonPath("$._embedded.rooms[1].building",
                        is(BUILDING_SECOND_ROOM)),
                    jsonPath("$._embedded.rooms[1].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_SECOND_ROOM)),
                    jsonPath("$._embedded.rooms[1]._links.self.href",
                        is(ROOM2_SELF_LINK)),
                    jsonPath("$._links.self.href", is(ROOMS_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'getRoom' method")
    class GetRoomTest {
        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "JSON with room in body")
        void getRequestWithPathVariableIdThenShouldReturnJsonRoom() throws Exception {
            int roomId = ROOM_ID1;
            Room room = createTestRoom();

            when(roomServiceMock.findById(roomId)).thenReturn(room);

            mockMvc.perform(get(API_ROOMS_ID, roomId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$.id", is(room.getId())),
                    jsonPath("$.building", is(room.getBuilding())),
                    jsonPath("$.number", is(room.getNumber())),
                    jsonPath("$.buildingAndRoom", is(room.getBuildingAndRoom())),
                    jsonPath("$._links.self.href", is(ROOM1_SELF_LINK))
                );
        }
    }

    @Nested
    @DisplayName("test 'createRoom' method")
    class CreateRoomTest {
        @Test
        @DisplayName("when POST request with parameters then should call " +
            "roomService.add")
        void postRequestWithParametersThenShouldCallRoomService() throws Exception {
            String roomBuilding = "5 Avenue, 48";
            String roomNumber = "185";
            String jsonBodyRequest = "{" +
                "\"building\": \"" + roomBuilding + "\"," +
                "\"number\": \"" + roomNumber + "\"" +
                "}";
            Room room = createTestRoom();

            when(roomServiceMock.create(any())).thenReturn(room);

            mockMvc.perform(post(API_ROOMS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.id", is(room.getId())),
                    jsonPath("$.building", is(room.getBuilding())),
                    jsonPath("$.number", is(room.getNumber())),
                    jsonPath("$.buildingAndRoom", is(room.getBuildingAndRoom())),
                    jsonPath("$._links.self.href", is(ROOM1_SELF_LINK))
                );

            verify(roomServiceMock, times(1)).create(roomCaptor.capture());
            Room roomForSaving = roomCaptor.getValue();
            RoomAssert.assertThat(roomForSaving)
                .hasId(null)
                .hasBuilding(roomBuilding)
                .hasNumber(roomNumber)
                .hasBuildingAndRoom(roomBuilding + " - " + roomNumber);
        }
    }

    @Nested
    @DisplayName("test 'updateRoom' method")
    class UpdateRoomTest {
        @Test
        @DisplayName("when PUT request with all required parameters then should " +
            "call roomService.update")
        void putRequestWithAllParametersShouldCallRoomService() throws Exception {
            String roomBuilding = "5 Avenue, 48";
            String roomNumber = "185";
            int roomId = 15;
            String jsonBodyRequest = "{" +
                "\"id\": " + roomId + "," +
                "\"building\": \"" + roomBuilding + "\"," +
                "\"number\": \"" + roomNumber + "\"" +
                "}";
            Room room = createTestRoom();

            when(roomServiceMock.update(eq(roomId), any(Room.class))).thenReturn(room);

            mockMvc.perform(put(API_ROOMS_ID, roomId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBodyRequest))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.id", is(room.getId())),
                    jsonPath("$.building", is(room.getBuilding())),
                    jsonPath("$.number", is(room.getNumber())),
                    jsonPath("$.buildingAndRoom", is(room.getBuildingAndRoom())),
                    jsonPath("$._links.self.href", is(ROOM1_SELF_LINK))
                );

            verify(roomServiceMock, times(1))
                .update(eq(roomId), roomCaptor.capture());

            Room updatedRoom = roomCaptor.getValue();
            RoomAssert.assertThat(updatedRoom)
                .hasId(roomId)
                .hasBuilding(roomBuilding)
                .hasNumber(roomNumber);
        }
    }

    @Nested
    @DisplayName("test 'getFreeRooms' method")
    class GetFreeRoomsTest {
        @Test
        @DisplayName("when GET request with parameters time_start and time_end " +
            "then should return JSON with list expected rooms")
        void getRequestWithParametersReturnJsonWithListRooms() throws Exception {

            List<Room> testRooms = createTestRooms();

            when(roomServiceMock.getFreeRoomsOnLessonTime(DATE_FROM, DATE_TO))
                .thenReturn(testRooms);

            mockMvc.perform(get(API_ROOMS_FREE)
                    .param("time_start", TEXT_DATE_FROM)
                    .param("time_end", TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.rooms", hasSize(testRooms.size())),
                    jsonPath("$._embedded.rooms[0].id", is(ROOM_ID1)),
                    jsonPath("$._embedded.rooms[0].building", is(BUILDING_FIRST_ROOM)),
                    jsonPath("$._embedded.rooms[0].number", is(NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.rooms[1].id", is(ROOM_ID2)),
                    jsonPath("$._embedded.rooms[1].building", is(BUILDING_SECOND_ROOM)),
                    jsonPath("$._embedded.rooms[1].number", is(NUMBER_SECOND_ROOM))
                );
        }
    }

    @Nested
    @DisplayName("test 'getLessonsForRoom' method")
    class GetLessonsForRoomTest {
        @Test
        @DisplayName("when GET request with parameters id, start and end then should " +
            "return JSON with list lessonDtos in body")
        void getRequestWithParametersShouldReturnJsonWithListLessonDtos() throws Exception {
            int roomId = 13;
            List<Lesson> lessons = createTestLessons();

            when(lessonServiceMock.getAllForRoomForTimePeriod(roomId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(lessons);


            mockMvc.perform(get(API_ROOMS_ID_TIMETABLE, roomId)
                    .param("start", START_TIME_TIMETABLE_ISO)
                    .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(TYPE_APPLICATION_HAL_JSON),
                    jsonPath("$._embedded.lessons", hasSize(2)),
                    jsonPath("$._embedded.lessons[0].id", is(LESSON_ID1)),
                    jsonPath("$._embedded.lessons[0].courseId",
                        is(COURSE_ID1)),
                    jsonPath("$._embedded.lessons[0].courseName",
                        is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.lessons[0].teacherId",
                        is(TEACHER_ID1)),
                    jsonPath("$._embedded.lessons[0].teacherFullName",
                        is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.lessons[0].roomId", is(ROOM_ID1)),
                    jsonPath("$._embedded.lessons[0].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.lessons[0].timeStart",
                        is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$._embedded.lessons[0].timeEnd",
                        is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$._embedded.lessons[1].id", is(LESSON_ID2)),
                    jsonPath("$._embedded.lessons[1].courseId", is(COURSE_ID1)),
                    jsonPath("$._embedded.lessons[1].courseName",
                        is(NAME_FIRST_COURSE)),
                    jsonPath("$._embedded.lessons[1].teacherId", is(TEACHER_ID1)),
                    jsonPath("$._embedded.lessons[1].teacherFullName",
                        is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$._embedded.lessons[1].roomId", is(ROOM_ID1)),
                    jsonPath("$._embedded.lessons[1].buildingAndRoom",
                        is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$._embedded.lessons[1].timeStart",
                        is(TEXT_DATE_START_SECOND_LESSON)),
                    jsonPath("$._embedded.lessons[1].timeEnd",
                        is(TEXT_DATE_END_SECOND_LESSON))
                );
        }
    }
}