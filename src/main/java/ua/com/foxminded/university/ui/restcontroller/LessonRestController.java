package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.ui.restcontroller.link.LessonDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_LESSONS)
public class LessonRestController extends AbstractController<LessonDto, Lesson> {

    private final LessonService lessonService;
    private final LessonDtoMapper mapper;
    private final LessonDtoAssembler assembler;
    private final PagedResourcesAssembler<Lesson> pagedAssembler;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<LessonDto> getLessons() {
        log.debug("Getting all lessons");
        CollectionModel<LessonDto> lessons = getAllInternal();
        lessons.add(LinkBuilder.LESSONS_SELF_LINK);
        return lessons;
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<LessonDto> getLessonsPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all lessons with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<LessonDto> getLessonsPaginated(@PageableDefault(sort = "timeStart")
                                                         Pageable pageable) {
        return getLessonsPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<LessonDto> getLessonsSorted(Pageable pageable) {
        return getLessonsPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public LessonDto getLesson(@PathVariable("id") int lessonId) {
        log.debug("Getting lesson by id({})", lessonId);
        return getByIdInternal(lessonId);
    }

    @GetMapping(MappingConstants.FILTER)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<LessonDto> getFilteredLessons(@ModelAttribute
                                                             LessonFilter lessonFilter) {
        log.debug("Get filtered lessons");
        Iterable<Lesson> filteredLessons =
            lessonService.getAllWithFilter(lessonFilter);
        CollectionModel<LessonDto> modelFilteredLessons =
            assembler.toCollectionModel(filteredLessons);
        modelFilteredLessons.add(
            linkTo(methodOn(LessonRestController.class).getFilteredLessons(lessonFilter))
                .withSelfRel()
        );
        return modelFilteredLessons;
    }

    @PostMapping
    public ResponseEntity<LessonDto> createLesson(@Valid @RequestBody LessonDto lessonDto,
                                                  HttpServletRequest request) {
        log.debug("Creating {}", lessonDto);
        return createInternal(lessonDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public LessonDto updateLesson(@Valid @RequestBody LessonDto lessonDto,
                                  @PathVariable("id") int lessonId,
                                  HttpServletRequest request) {
        LessonDto updatedLessonDto = updateInternal(lessonId, lessonDto, request);
        log.debug("Lesson id({}) is updated", lessonId);
        return updatedLessonDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable("id") int lessonId) {
        log.debug("Deleting lesson with id({})", lessonId);
        deleteInternal(lessonId);
    }

    @PutMapping(MappingConstants.ID_STUDENTS_STUDENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public LessonDto addStudentToLesson(@PathVariable("id") int lessonId,
                                        @PathVariable int studentId) {
        log.debug("Adding student id({}) to lesson id({})", studentId, lessonId);
        Lesson lesson = lessonService.addStudentToLesson(lessonId, studentId);
        return assembler.toModel(lesson);
    }

    @PutMapping(MappingConstants.ID_GROUPS_GROUP_ID)
    @ResponseStatus(HttpStatus.OK)
    public LessonDto addStudentsFromGroupToLesson(@PathVariable("id") int lessonId,
                                                  @PathVariable int groupId) {
        log.debug("Adding all students from group id({}) to lesson id({})",
            groupId, lessonId);
        Lesson lesson = lessonService.addStudentsFromGroupToLesson(groupId, lessonId);
        log.debug("Students from group id({}) is added to lesson id({})", groupId,
            lessonId);
        return assembler.toModel(lesson);
    }

    @DeleteMapping(MappingConstants.ID_STUDENTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudentFromLesson(@PathVariable("id") int lessonId,
                                        @RequestBody Integer[] studentIds) {
        log.debug("Remove students id({}) from lesson id({})", studentIds, lessonId);
        if (studentIds.length == 1) {
            lessonService.removeStudentFromLesson(lessonId, studentIds[0]);
        } else {
            lessonService.removeStudentsFromLesson(lessonId, studentIds);
        }
        log.debug("Students id({}) successfully removed from lesson id({})",
            studentIds, lessonId);
    }

    @Override
    protected Service<Lesson> getService() {
        return lessonService;
    }

    @Override
    protected RepresentationModelAssembler<Lesson, LessonDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Lesson> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Lesson, LessonDto> getMapper() {
        return mapper;
    }
}
