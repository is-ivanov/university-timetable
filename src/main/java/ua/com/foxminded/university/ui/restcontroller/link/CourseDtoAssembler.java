package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.CourseDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.mapper.CourseDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.CourseRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder.COURSES_LINK;
import static ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder.COURSES_SELF_LINK;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class CourseDtoAssembler implements RepresentationModelAssembler<Course, CourseDto> {

    private final CourseDtoMapper mapper;

    @Override
    public CourseDto toModel(Course course) {

        CourseDto courseDto = mapper.toDto(course);

        courseDto.add(
            linkTo(methodOn(CourseRestController.class).getCourse(course.getId())).withSelfRel(),
            COURSES_LINK
        );
        return courseDto;
    }

    @Override
    public CollectionModel<CourseDto> toCollectionModel(Iterable<? extends Course> entities) {

        CollectionModel<CourseDto> courseDtos =
            RepresentationModelAssembler.super.toCollectionModel(entities);
        courseDtos.add(COURSES_SELF_LINK);

        return courseDtos;
    }
}
