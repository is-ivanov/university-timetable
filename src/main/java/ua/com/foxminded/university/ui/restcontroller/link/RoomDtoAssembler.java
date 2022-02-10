package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.RoomDto;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.mapper.RoomDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.RoomRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class RoomDtoAssembler implements RepresentationModelAssembler<Room, RoomDto> {

    private final RoomDtoMapper mapper;

    @Override
    public RoomDto toModel(Room room) {

        RoomDto roomDto = mapper.toDto(room);

        roomDto.add(
            linkTo(methodOn(RoomRestController.class).getRoom(room.getId())).withSelfRel(),
            linkTo(methodOn(RoomRestController.class).getLessonsForRoom(room.getId(), null, null))
                .withRel("lessons for room"),
            LinkBuilder.ROOMS_LINK,
            LinkBuilder.ROOT_LINK
        );

        return roomDto;
    }

    @Override
    public CollectionModel<RoomDto> toCollectionModel(Iterable<? extends Room> entities) {

        CollectionModel<RoomDto> modelRooms =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        modelRooms.add(
            LinkBuilder.ROOMS_LINK,
            LinkBuilder.ROOT_LINK
        );

        return modelRooms;
    }
}
