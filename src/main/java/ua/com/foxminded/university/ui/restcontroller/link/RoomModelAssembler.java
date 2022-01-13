package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.ui.restcontroller.RoomRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class RoomModelAssembler implements RepresentationModelAssembler<Room,
    EntityModel<Room>> {

    @SuppressWarnings("NullableProblems")
    @Override
    public EntityModel<Room> toModel(Room room) {
        return EntityModel.of(room,
            linkTo(RoomRestController.class).withRel("rooms"));
    }
}
