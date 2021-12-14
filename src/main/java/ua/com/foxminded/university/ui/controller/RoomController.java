package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.DATE_TIME_PATTERN;
import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/rooms")
public class RoomController {

    public static final String URI_ROOMS = "/rooms";

    private final RoomService roomService;
    private final LessonService lessonService;
    private final PageSequenceCreator pageSequenceCreator;

    @GetMapping
    public String showRooms(Model model,
                            @PageableDefault(sort = "room_number") Pageable pageable) {
        log.debug("Getting data for room.html");
        Page<Room> pageRooms = roomService.getAllSortedPaginated(pageable);
        model.addAttribute("rooms", pageRooms.getContent());
        model.addAttribute("page", pageRooms);
        model.addAttribute("uri", URI_ROOMS);
        model.addAttribute("newRoom", new Room());
        model.addAttribute("pages", pageSequenceCreator
            .createPageSequence(pageRooms.getTotalPages(),
                pageRooms.getNumber() + 1));
        log.debug("The list of rooms is loaded into the model");
        return "room";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Room showRoom(@PathVariable("id") int roomId) {
        log.debug("Getting room by id({})", roomId);
        Room room = roomService.getById(roomId);
        log.debug("Found {}", room);
        return room;
    }

    @GetMapping("/free")
    @ResponseBody
    public List<Room> getFreeRooms(@RequestParam("time_start")
                                   @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                       LocalDateTime startTime,
                                   @RequestParam("time_end")
                                   @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                       LocalDateTime endTime) {
        log.debug("Getting rooms free from {} to {}", startTime, endTime);
        List<Room> freeRooms = roomService.getFreeRoomsOnLessonTime(startTime, endTime);
        log.debug("Found {} free rooms", freeRooms.size());
        return freeRooms;
    }

    @PostMapping
    public String createRoom(@ModelAttribute Room room,
                             HttpServletRequest request) {
        log.debug("Creating {}", room);
        roomService.save(room);
        log.debug("{} is created", room);
        return defineRedirect(request);
    }

    @PutMapping("/{id}")
    public String updateRoom(@ModelAttribute Room room,
                             @PathVariable("id") int roomId,
                             HttpServletRequest request) {
        log.debug("Updating room id({})", roomId);
        roomService.save(room);
        log.debug("Room id({}) is updated", roomId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") int roomId,
                             HttpServletRequest request) {
        log.debug("Deleting room with id({})", roomId);
        roomService.delete(roomId);
        log.debug("Room id({}) is deleted", roomId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/timetable")
    @ResponseBody
    public List<LessonDto> getLessonsForRoom(@PathVariable("id") int roomId,
                                             @RequestParam("start")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 ZonedDateTime startTime,
                                             @RequestParam("end")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                 ZonedDateTime endTime) {
        log.debug("Getting lessons for room id({}) from {} to {}", roomId,
            startTime, endTime);
        List<LessonDto> lessonsForTeacher = lessonService
            .getAllForRoomForTimePeriod(roomId,
                startTime.toLocalDateTime(), endTime.toLocalDateTime());
        log.debug("Found {} lessons", lessonsForTeacher.size());
        return lessonsForTeacher;
    }
}