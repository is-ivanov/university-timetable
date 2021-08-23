package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/rooms")
public class RoomController {

    public static final String REDIRECT_ROOMS = "redirect:/rooms";

    private final RoomService roomService;

    @GetMapping
    public String showRooms(Model model) {
        log.debug("Getting data for room.html");
        model.addAttribute("rooms", roomService.getAll());
        model.addAttribute("newRoom", new Room());
        log.info("The list of rooms is loaded into the model");
        return "room";
    }

    @PostMapping
    public String createRoom(@ModelAttribute Room room) {
        log.debug("Creating {}", room);
        roomService.add(room);
        log.info("{} is create", room);
        return REDIRECT_ROOMS;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Room showRoom(@PathVariable("id") int roomId) {
        log.debug("Getting room by id({})", roomId);
        Room room = roomService.getById(roomId);
        log.info("Found {}", room);
        return room;
    }

    @PutMapping("/{id}")
    public String updateRoom(@ModelAttribute Room room,
                             @PathVariable("id") int roomId) {
        log.debug("Updating room id({})", roomId);
        roomService.update(room);
        log.info("Room id({}) is updated", roomId);
        return REDIRECT_ROOMS;
    }

    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") int roomId) {
        log.debug("Deleting room with id({})", roomId);
        roomService.delete(roomId);
        log.info("Room id({}) is deleted", roomId);
        return REDIRECT_ROOMS;
    }
}