package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    public static final int ID1 = 1;
    public static final int ID2 = 2;

    private MockMvc mockMvc;

    @Mock
    private RoomService roomServiceMock;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(roomController)
            .setViewResolvers(viewResolver)
            .build();
    }

    @Test
    @DisplayName("Test showRooms")
    void testShowRooms() throws Exception{
        Room room1 = new Room(ID1, "building1", "123");
        Room room2 = new Room(ID2, "building1", "234");
        List<Room> rooms = Arrays.asList(room1, room2);

        when(roomServiceMock.getAll()).thenReturn(rooms);

        mockMvc.perform(get("/room"))
            .andDo(print())
            .andExpect(matchAll(
               status().isOk(),
               view().name("room"),
               model().attribute("rooms", rooms)
            ));
    }
}