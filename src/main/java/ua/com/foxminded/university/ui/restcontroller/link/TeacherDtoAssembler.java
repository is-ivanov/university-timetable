package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.TeacherRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class TeacherDtoAssembler implements RepresentationModelAssembler<Teacher, TeacherDto> {

    private final TeacherDtoMapper mapper;

    @Override
    public TeacherDto toModel(Teacher teacher) {

        TeacherDto teacherDto = mapper.toDto(teacher);

        teacherDto.add(
            linkTo(methodOn(TeacherRestController.class).getTeacher(teacher.getId()))
                .withSelfRel(),
            linkTo(methodOn(TeacherRestController.class)
                .getLessonsForTeacher(teacher.getId(), null, null))
                .withRel("lessons for teacher"),
            LinkBuilder.TEACHERS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return teacherDto;
    }

    @Override
    public CollectionModel<TeacherDto> toCollectionModel(Iterable<? extends Teacher> entities) {

        CollectionModel<TeacherDto> modelTeachers =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        modelTeachers.add(
            LinkBuilder.TEACHERS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return modelTeachers;
    }
}
