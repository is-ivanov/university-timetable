package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.FacultyRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class FacultyDtoAssembler implements RepresentationModelAssembler<Faculty, FacultyDto> {

    private final FacultyDtoMapper mapper;
    @Override
    public FacultyDto toModel(Faculty faculty) {
        FacultyDto facultyDto = mapper.toFacultyDto(faculty);
        facultyDto.add(
            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId())).withSelfRel(),
            linkTo(FacultyRestController.class).withRel("faculties"));
        return facultyDto;
    }
//    @Override
//    public void addLinks(EntityModel<FacultyDto> resource) {
//
//    }
//
//    @Override
//    public void addLinks(CollectionModel<EntityModel<FacultyDto>> resources) {
//
//    }

//    @SuppressWarnings("NullableProblems")
//    @Override
//    public FacultyDto toModel(Faculty faculty) {
//        return EntityModel.of(faculty,
//            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId())).withSelfRel(),
//            linkTo(FacultyRestController.class).withRel("faculties"));
//    }


}
