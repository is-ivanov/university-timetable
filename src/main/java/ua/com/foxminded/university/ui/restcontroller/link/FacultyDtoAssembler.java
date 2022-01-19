package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.FacultyRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder.FACULTIES_LINK;
import static ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder.FACULTIES_SELF_LINK;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class FacultyDtoAssembler implements RepresentationModelAssembler<Faculty, FacultyDto> {

    private final FacultyDtoMapper mapper;


    @Override
    public FacultyDto toModel(Faculty faculty) {

        FacultyDto facultyDto = mapper.toDto(faculty);

        facultyDto.add(
            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId())).withSelfRel(),
            FACULTIES_LINK);

        return facultyDto;
    }

    @Override
    public CollectionModel<FacultyDto> toCollectionModel(Iterable<? extends Faculty> entities) {
        CollectionModel<FacultyDto> facultyDtos =
            RepresentationModelAssembler.super.toCollectionModel(entities);
        facultyDtos.add(FACULTIES_SELF_LINK);

        return facultyDtos;
    }
}
