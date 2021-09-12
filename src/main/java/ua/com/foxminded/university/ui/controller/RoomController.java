package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/rooms")
public class RoomController {

    public static final String URI_ROOMS = "/rooms";

    private final RoomService roomService;

    @GetMapping
    public String showRooms(Model model) {
        log.debug("Getting data for room.html");
        model.addAttribute("rooms", roomService.getAll());
        model.addAttribute("newRoom", new Room());
        log.info("The list of rooms is loaded into the model");
        return defineRedirect(URI_ROOMS);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Room showRoom(@PathVariable("id") int roomId) {
        log.debug("Getting room by id({})", roomId);
        Room room = roomService.getById(roomId);
        log.info("Found {}", room);
        return room;
    }

    @GetMapping("/free")
    @ResponseBody
    public List<Room> getFreeRooms(@RequestParam("time_start")
                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                       LocalDateTime startTime,
                                   @RequestParam("time_end")

                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                       LocalDateTime endTime) {
        log.debug("Getting rooms free from {} to {}", startTime, endTime);
        List<Room> freeRooms = roomService.getFreeRoomsOnLessonTime(startTime, endTime);
        log.info("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @PostMapping
    public String createRoom(@ModelAttribute Room room) {
        log.debug("Creating {}", room);
        roomService.add(room);
        log.info("{} is created", room);
        return defineRedirect(URI_ROOMS);
    }

    @PutMapping("/{id}")
    public String updateRoom(@ModelAttribute Room room,
                             @PathVariable("id") int roomId) {
        log.debug("Updating room id({})", roomId);
        roomService.update(room);
        log.info("Room id({}) is updated", roomId);
        return defineRedirect(URI_ROOMS);
    }

    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") int roomId) {
        log.debug("Deleting room with id({})", roomId);
        roomService.delete(roomId);
        log.info("Room id({}) is deleted", roomId);
        return defineRedirect(URI_ROOMS);
    }
}