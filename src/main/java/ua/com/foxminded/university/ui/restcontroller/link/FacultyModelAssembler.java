package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.ui.restcontroller.FacultyRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FacultyModelAssembler implements RepresentationModelAssembler<FacultyDto,
    EntityModel<FacultyDto>> {

    @Override
    public EntityModel<FacultyDto> toModel(FacultyDto faculty) {
        return EntityModel.of(faculty,
            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId())).withSelfRel(),
            linkTo(methodOn(FacultyRestController.class).getFaculties()).withRel("faculties"));
    }
}
