package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.GroupRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class GroupDtoAssembler implements RepresentationModelAssembler<Group, GroupDto> {

    private final GroupDtoMapper mapper;

    @Override
    public GroupDto toModel(Group group) {

        GroupDto groupDto = mapper.toDto(group);

        groupDto.add(
            linkTo(methodOn(GroupRestController.class).getGroup(group.getId()))
                .withSelfRel(),

            LinkBuilder.GROUPS_LINK,
            LinkBuilder.ROOT_LINK
        );

        return groupDto;
    }

    @Override
    public CollectionModel<GroupDto> toCollectionModel(Iterable<? extends Group> entities) {

        CollectionModel<GroupDto> groupDtos =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        groupDtos.add(
            LinkBuilder.GROUPS_LINK,
            LinkBuilder.ROOT_LINK
        );

        return groupDtos;
    }
}
