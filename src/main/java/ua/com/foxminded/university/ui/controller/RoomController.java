package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import javax.servlet.http.HttpServletRequest;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/rooms")
public class RoomController {

    public static final String URI_ROOMS = "/rooms";

    private final RoomService roomService;
    private final PageSequenceCreator pageSequenceCreator;

    @GetMapping
    public String showRooms(Model model,
                            @PageableDefault(sort = "number") Pageable pageable) {
        log.debug("Getting data for room.html");
        Page<Room> pageRooms = roomService.findAll(pageable);
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

    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") int roomId,
                             HttpServletRequest request) {
        log.debug("Deleting room with id({})", roomId);
        roomService.delete(roomId);
        log.debug("Room id({}) is deleted", roomId);
        return defineRedirect(request);
    }

}