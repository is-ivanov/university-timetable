package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.StudentRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class StudentDtoAssembler implements RepresentationModelAssembler<Student, StudentDto> {

    private final StudentDtoMapper mapper;

    @Override
    public StudentDto toModel(Student student) {

        StudentDto studentDto = mapper.toDto(student);

        studentDto.add(
            linkTo(methodOn(StudentRestController.class).getStudent(student.getId()))
                .withSelfRel(),
            linkTo(methodOn(StudentRestController.class)
                .getLessonsForStudent(student.getId(), null, null))
                .withRel("lessons for student"),
            LinkBuilder.STUDENTS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return studentDto;
    }

    @Override
    public CollectionModel<StudentDto> toCollectionModel(Iterable<? extends Student> entities) {

        CollectionModel<StudentDto> modelStudents =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        modelStudents.add(
            LinkBuilder.STUDENTS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return modelStudents;
    }
}
