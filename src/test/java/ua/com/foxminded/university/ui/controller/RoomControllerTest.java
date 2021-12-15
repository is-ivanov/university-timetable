package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.RoomController.URI_ROOMS;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    public static final String URI_ROOMS_ID = "/rooms/{id}";
    public static final String ROOM_NUMBER = "number";
    public static final String URI_ROOMS_ROOM_ID_TIMETABLE = "/rooms/{id}/timetable";
    public static final String URI_ROOMS_FREE = "/rooms/free";
    public static final LocalDateTime START_TIME_TIMETABLE =
        LocalDateTime.of(2021, 9, 27, 0, 0, 0);
    public static final LocalDateTime END_TIME_TIMETABLE =
        LocalDateTime.of(2021, 11, 8, 0, 0, 0);
    public static final String START_TIME_TIMETABLE_ISO = "2021-09-27T00:00:00+03:00";
    public static final String END_TIME_TIMETABLE_ISO = "2021-11-08T00:00:00+03:00";

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    @Captor
    ArgumentCaptor<Room> roomCaptor;

    private MockMvc mockMvc;

    @Mock
    private RoomService roomServiceMock;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private PageSequenceCreator pageSequenceCreatorMock;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(roomController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Nested
    @DisplayName("test 'showRooms' method")
    class ShowRoomsTest {

        @Test
        @DisplayName("when GET request without parameters then should use " +
            "@PageableDefault values")
        void getRequestWithoutParameters() throws Exception {
            int totalPages = 1;
            int defaultPage = 0;
            int defaultSize = 10;
            Sort defaultSort = Sort.by(ROOM_NUMBER);

            Pageable pageable = PageRequest.of(defaultPage, defaultSize,
                defaultSort);
            List<Room> testRooms = createTestRooms();
            Page<Room> pageRooms = new PageImpl<>(testRooms, pageable, totalPages);
            List<Integer> pages = Collections.singletonList(1);

            when(roomServiceMock.getAllSortedPaginated(pageable))
                .thenReturn(pageRooms);
            when(pageSequenceCreatorMock.createPageSequence(totalPages, defaultPage + 1))
                .thenReturn(pages);

            mockMvc.perform(get(URI_ROOMS))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("room"),
                    model().attributeExists("rooms", "page", "uri",
                        "newRoom", "pages"),
                    model().attribute("rooms", testRooms),
                    model().attribute("page", pageRooms),
                    model().attribute("pages", pages),
                    model().attribute("uri", URI_ROOMS)
                );
        }

        @Test
        @DisplayName("with GET request with parameter page = 7 then should use " +
            "this value and the rest of the parameters by default")
        void getRequestWithPage7() throws Exception {
            int currentPage = 7;
            int totalPages = 9;
            int defaultSize = 10;
            Sort defaultSort = Sort.by(ROOM_NUMBER);

            Pageable pageable = PageRequest.of(currentPage, defaultSize,
                defaultSort);
            List<Room> testRooms = createTestRooms();
            Page<Room> pageRooms = new PageImpl<>(testRooms, pageable, totalPages);
            when(roomServiceMock.getAllSortedPaginated(pageable))
                .thenReturn(pageRooms);

            mockMvc.perform(get(URI_ROOMS)
                    .param("page", String.valueOf(currentPage)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("room"),
                    model().attribute("uri", URI_ROOMS)
                );

            verify(roomServiceMock, times(1))
                .getAllSortedPaginated(pageableCaptor.capture());

            Pageable pageableCaptured = pageableCaptor.getValue();

            assertThat(pageableCaptured.getPageNumber(), is(equalTo(currentPage)));
            assertThat(pageableCaptured.getPageSize(), is(equalTo(defaultSize)));
            assertThat(pageableCaptured.getSort(), is(equalTo(defaultSort)));
        }
    }

    @Nested
    @DisplayName("test 'showRoom' method")
    class ShowRoomTest {
        @Test
        @DisplayName("when GET request with @PathVariable id then should return " +
            "JSON with room in body")
        void getRequestWithPathVariableIdThenShouldReturnJsonRoom() throws Exception {
            int roomId = ROOM_ID1;
            Room testRoom = createTestRoom();

            when(roomServiceMock.getById(roomId)).thenReturn(testRoom);
            mockMvc.perform(get(URI_ROOMS_ID, roomId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(roomId)),
                    jsonPath("$.building", is(BUILDING_FIRST_ROOM)),
                    jsonPath("$.number", is(NUMBER_FIRST_ROOM))
                );
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

            mockMvc.perform(get(URI_ROOMS_FREE)
                    .param("time_start", TEXT_DATE_FROM)
                    .param("time_end", TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testRooms.size())),
                    jsonPath("$[0].id", is(ROOM_ID1)),
                    jsonPath("$[0].building", is(BUILDING_FIRST_ROOM)),
                    jsonPath("$[0].number", is(NUMBER_FIRST_ROOM)),
                    jsonPath("$[1].id", is(ROOM_ID2)),
                    jsonPath("$[1].building", is(BUILDING_SECOND_ROOM)),
                    jsonPath("$[1].number", is(NUMBER_SECOND_ROOM))
                );
        }
    }

    @Nested
    @DisplayName("test 'createRoom' method")
    class CreateRoomTest {
        @Test
        @DisplayName("when POST request with parameters then should call " +
            "roomService.add and redirect")
        void postRequestWithParametersThenShouldCallRoomService() throws Exception {
            mockMvc.perform(post(URI_ROOMS)
                    .param("building", BUILDING_FIRST_ROOM)
                    .param("number", NUMBER_FIRST_ROOM))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(roomServiceMock, times(1))
                .save(roomCaptor.capture());
            Room expectedRoom = roomCaptor.getValue();
            assertThat(expectedRoom.getBuilding(), is(BUILDING_FIRST_ROOM));
            assertThat(expectedRoom.getNumber(), is(NUMBER_FIRST_ROOM));
            assertThat(expectedRoom.getId(), is(nullValue()));
        }
    }

    @Nested
    @DisplayName("test 'updateRoom' method")
    class UpdateRoomTest {
        @Test
        @DisplayName("when PUT request with all required parameters then should " +
            "call roomService.update and redirect")
        void putRequestWithAllParametersShouldCallRoomServiceAndRedirect() throws Exception {
            Room testRoom = createTestRoom();
            mockMvc.perform(put(URI_ROOMS_ID, testRoom.getId())
                    .param("building", BUILDING_FIRST_ROOM)
                    .param("number", NUMBER_FIRST_ROOM))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(roomServiceMock, times(1)).save(roomCaptor.capture());
            Room newRoom = roomCaptor.getValue();
            assertThat(newRoom.getBuilding(), is(equalTo(BUILDING_FIRST_ROOM)));
            assertThat(newRoom.getNumber(), is(equalTo(NUMBER_FIRST_ROOM)));
        }
    }

    @Nested
    @DisplayName("test 'deleteRoom' method")
    class DeleteRoomTest {
        @Test
        @DisplayName("when DELETE request with @PathVariable id then should call " +
            "roomService.delete and redirect")
        void deleteRequestWithParameterIdShouldCallRoomServiceAndRedirect() throws Exception {
            int roomId = 12;
            mockMvc.perform(delete(URI_ROOMS_ID, roomId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
            verify(roomServiceMock, times(1)).delete(roomId);
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
            List<LessonDto> testLessonDtos = createTestLessonDtos();
            when(lessonServiceMock.getAllForRoomForTimePeriod(roomId,
                START_TIME_TIMETABLE, END_TIME_TIMETABLE))
                .thenReturn(testLessonDtos);
            mockMvc.perform(get(URI_ROOMS_ROOM_ID_TIMETABLE, roomId)
                    .param("start", START_TIME_TIMETABLE_ISO)
                    .param("end", END_TIME_TIMETABLE_ISO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(2)),
                    jsonPath("$[0].id", is(LESSON_ID1)),
                    jsonPath("$[0].courseId", is(COURSE_ID1)),
                    jsonPath("$[0].courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$[0].teacherId", is(TEACHER_ID1)),
                    jsonPath("$[0].teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$[0].roomId", is(ROOM_ID1)),
                    jsonPath("$[0].buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$[0].timeStart", is(TEXT_DATE_START_FIRST_LESSON)),
                    jsonPath("$[0].timeEnd", is(TEXT_DATE_END_FIRST_LESSON)),
                    jsonPath("$[1].id", is(LESSON_ID2)),
                    jsonPath("$[1].courseId", is(COURSE_ID1)),
                    jsonPath("$[1].courseName", is(NAME_FIRST_COURSE)),
                    jsonPath("$[1].teacherId", is(TEACHER_ID1)),
                    jsonPath("$[1].teacherFullName", is(FULL_NAME_FIRST_TEACHER)),
                    jsonPath("$[1].roomId", is(ROOM_ID1)),
                    jsonPath("$[1].buildingAndRoom", is(BUILDING_AND_NUMBER_FIRST_ROOM)),
                    jsonPath("$[1].timeStart", is(TEXT_DATE_START_SECOND_LESSON)),
                    jsonPath("$[1].timeEnd", is(TEXT_DATE_END_SECOND_LESSON))
                );
        }
    }

}