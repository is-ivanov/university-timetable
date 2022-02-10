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

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class CourseDtoAssembler implements RepresentationModelAssembler<Course, CourseDto> {

    private final CourseDtoMapper mapper;

    @Override
    public CourseDto toModel(Course course) {

        CourseDto courseDto = mapper.toDto(course);

        Class<CourseRestController> classController = CourseRestController.class;

        courseDto.add(
            linkTo(methodOn(classController).getCourse(course.getId())).withSelfRel(),
            LinkBuilder.COURSES_LINK,
            LinkBuilder.ROOT_LINK
        );
        return courseDto;
    }

    @Override
    public CollectionModel<CourseDto> toCollectionModel(Iterable<? extends Course> entities) {

        CollectionModel<CourseDto> modelCourses =
            RepresentationModelAssembler.super.toCollectionModel(entities);
        modelCourses.add(
            LinkBuilder.COURSES_SELF_LINK,
            LinkBuilder.ROOT_LINK
        );

        return modelCourses;
    }
}
