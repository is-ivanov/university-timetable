package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.FacultyDto;

@Component
public class FacultyModelAssembler implements SimpleRepresentationModelAssembler<FacultyDto> {
    @Override
    public void addLinks(EntityModel<FacultyDto> resource) {

    }

    @Override
    public void addLinks(CollectionModel<EntityModel<FacultyDto>> resources) {

    }

//    @SuppressWarnings("NullableProblems")
//    @Override
//    public EntityModel<FacultyDto> toModel(FacultyDto faculty) {
//        return EntityModel.of(faculty,
//            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId())).withSelfRel(),
//            linkTo(FacultyRestController.class).withRel("faculties"));
//    }


}
