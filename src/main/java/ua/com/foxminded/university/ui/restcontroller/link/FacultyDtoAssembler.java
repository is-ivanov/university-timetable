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

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class FacultyDtoAssembler implements RepresentationModelAssembler<Faculty, FacultyDto> {

    public static final String LINK_GROUPS = "groups from this faculty";
    public static final String LINK_DEPARTMENTS = "departments from this faculty";
    public static final String LINK_TEACHERS = "teachers from this faculty";

    private final FacultyDtoMapper mapper;

    @Override
    public FacultyDto toModel(Faculty faculty) {

        FacultyDto facultyDto = mapper.toDto(faculty);

        facultyDto.add(
            linkTo(methodOn(FacultyRestController.class).getFaculty(faculty.getId()))
                .withSelfRel(),
            LinkBuilder.FACULTIES_LINK,
            linkTo(methodOn(FacultyRestController.class).getGroupsByFaculty(faculty.getId()))
                .withRel(LINK_GROUPS),
            linkTo(methodOn(FacultyRestController.class)
                .getDepartmentsByFaculty(faculty.getId()))
                .withRel(LINK_DEPARTMENTS),
            linkTo(methodOn(FacultyRestController.class)
                .getTeachersByFaculty(faculty.getId()))
                .withRel(LINK_TEACHERS),
            LinkBuilder.ROOT_LINK
        );

        return facultyDto;
    }

    @Override
    public CollectionModel<FacultyDto> toCollectionModel(Iterable<? extends Faculty> entities) {

        CollectionModel<FacultyDto> modelFaculties =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        modelFaculties.add(
            LinkBuilder.FACULTIES_SELF_LINK,
            LinkBuilder.ROOT_LINK
        );

        return modelFaculties;
    }
}
