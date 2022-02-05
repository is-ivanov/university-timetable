package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
            LinkBuilder.ROOMS_LINK
        );

        return roomDto;
    }

    @Override
    public CollectionModel<RoomDto> toCollectionModel(Iterable<? extends Room> entities) {

        CollectionModel<RoomDto> roomDtos =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        roomDtos.add(LinkBuilder.ROOMS_SELF_LINK);

        return roomDtos;
    }
}
