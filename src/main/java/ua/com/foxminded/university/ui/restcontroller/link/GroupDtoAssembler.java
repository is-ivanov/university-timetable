package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.ui.restcontroller.GroupRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class GroupDtoAssembler implements RepresentationModelAssembler<GroupDto,
    EntityModel<GroupDto>> {

    @SuppressWarnings("NullableProblems")
    @Override
    public EntityModel<GroupDto> toModel(GroupDto group) {
        return EntityModel.of(group,
//            linkTo(methodOn(GroupRestController.class).getFaculty(group.getId())).withSelfRel(),
            linkTo(GroupRestController.class).withRel("groups"));
    }

}
