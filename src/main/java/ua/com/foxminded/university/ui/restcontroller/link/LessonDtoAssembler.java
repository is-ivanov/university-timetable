package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.LessonRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class LessonDtoAssembler implements RepresentationModelAssembler<Lesson, LessonDto> {

    private final LessonDtoMapper mapper;

    @Override
    public LessonDto toModel(Lesson lesson) {

        LessonDto lessonDto = mapper.toDto(lesson);

        lessonDto.add(
            linkTo(methodOn(LessonRestController.class).getLesson(lesson.getId()))
                .withSelfRel(),
            LinkBuilder.LESSONS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return lessonDto;
    }

    @Override
    public CollectionModel<LessonDto> toCollectionModel(Iterable<? extends Lesson> entities) {

        CollectionModel<LessonDto> modelLessons =
            RepresentationModelAssembler.super.toCollectionModel(entities);

        modelLessons.add(
            LinkBuilder.LESSONS_LINK,
            linkTo(methodOn(LessonRestController.class).getFilteredLessons(null))
                .withRel("filtered lessons"),
            LinkBuilder.ROOT_LINK
        );

        return modelLessons;
    }
}
