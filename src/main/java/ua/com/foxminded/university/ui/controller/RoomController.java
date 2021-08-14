package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String showRooms(Model model) {
        log.debug("Getting data for room.html");
        model.addAttribute("rooms", roomService.getAll());
        log.info("The list of rooms is loaded into the model");
        return "room";
    }
}