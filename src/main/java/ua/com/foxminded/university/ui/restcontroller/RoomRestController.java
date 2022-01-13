package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.ui.restcontroller.link.RoomModelAssembler;
import ua.com.foxminded.university.ui.util.Mappings;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Mappings.API_ROOMS)
@Validated
public class RoomRestController {

    private final RoomService roomService;
    private final RoomModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Room>>> getRooms(){
        log.debug("Getting all rooms");
        List<Room> rooms = roomService.getAll();
        CollectionModel<EntityModel<Room>> entityModels =
            assembler.toCollectionModel(rooms);
        entityModels.add(linkTo(RoomRestController.class).withSelfRel());
        return ResponseEntity.ok(entityModels);
    }
}
