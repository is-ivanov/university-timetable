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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.createTestRooms;
import static ua.com.foxminded.university.ui.controller.RoomController.URI_ROOMS;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    public static final String URI_ROOMS_ID = "/rooms/{id}";
    public static final String ROOM_NUMBER = "number";

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    private MockMvc mockMvc;

    @Mock
    private RoomService roomServiceMock;

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

            when(roomServiceMock.findAll(pageable))
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
            when(roomServiceMock.findAll(pageable))
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
                .findAll(pageableCaptor.capture());

            Pageable pageableCaptured = pageableCaptor.getValue();

            assertThat(pageableCaptured.getPageNumber(), is(equalTo(currentPage)));
            assertThat(pageableCaptured.getPageSize(), is(equalTo(defaultSize)));
            assertThat(pageableCaptured.getSort(), is(equalTo(defaultSort)));
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

}