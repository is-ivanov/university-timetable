package ua.com.foxminded.university.ui.restcontroller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.restcontroller.link.LessonDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.link.TeacherDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_TEACHERS)
@Tag(name = "Teacher Controller", description = "Management teachers")
public class TeacherRestController extends AbstractController<TeacherDto, Teacher> {

    private final TeacherService teacherService;
    private final TeacherDtoMapper mapper;
    private final TeacherDtoAssembler assembler;
    private final LessonService lessonService;
    private final LessonDtoAssembler lessonAssembler;
    private final PagedResourcesAssembler<Teacher> pagedAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<TeacherDto> getTeachers() {
        log.debug("Getting all teachers");
        CollectionModel<TeacherDto> teachers = getAllInternal();
        teachers.add(LinkBuilder.TEACHERS_SELF_LINK);
        return teachers;
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<TeacherDto> getTeachersPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all teachers with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<TeacherDto> getTeachersPaginated(@PageableDefault
                                                           (sort = "lastName")
                                                           Pageable pageable) {
        return getTeachersPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<TeacherDto> getTeachersSorted(Pageable pageable) {
        return getTeachersPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public TeacherDto getTeacher(@PathVariable("id") int teacherId) {
        log.debug("Getting teacher by id({})", teacherId);
        return getByIdInternal(teacherId);
    }

    @PostMapping
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody
                                                        TeacherDto teacherDto,
                                                    HttpServletRequest request) {
        log.debug("Creating {}", teacherDto);
        return createInternal(teacherDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public TeacherDto updateTeacher(@Valid @RequestBody TeacherDto teacherDto,
                                    @PathVariable("id") int teacherId,
                                    HttpServletRequest request) {
        TeacherDto updatedTeacherDto = updateInternal(teacherId, teacherDto, request);
        log.debug("Teacher id({}) is updated", teacherId);
        return updatedTeacherDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(@PathVariable("id") int teacherId) {
        log.debug("Deleting teacher with id({})", teacherId);
        deleteInternal(teacherId);
    }

    @GetMapping(MappingConstants.FREE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<TeacherDto> getFreeTeachers(@RequestParam("time_start")
                                                       @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                           LocalDateTime from,
                                                       @RequestParam("time_end")
                                                       @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                           LocalDateTime to) {
        log.debug("Getting teachers free from {} to {}", from, to);
        List<Teacher> freeTeachers =
            teacherService.getFreeTeachersOnLessonTime(from, to);
        log.debug("Found {} active free teachers", freeTeachers.size());
        CollectionModel<TeacherDto> modelTeachers = assembler.toCollectionModel(freeTeachers);
        modelTeachers.add(linkTo(methodOn(TeacherRestController.class)
            .getFreeTeachers(from, to)).withSelfRel());
        return modelTeachers;
    }

    @GetMapping("/{id}/timetable")
    @ResponseBody
    public CollectionModel<LessonDto> getLessonsForTeacher(@PathVariable("id") int teacherId,
                                                           @RequestParam("start")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                               ZonedDateTime from,
                                                           @RequestParam("end")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                               ZonedDateTime to) {
        log.debug("Getting lessons for teacher id({}) from {} to {}", teacherId,
            from, to);
        List<Lesson> lessonsForTeacher = lessonService
            .getAllForTeacherForTimePeriod(teacherId,
                from.toLocalDateTime(), to.toLocalDateTime());
        log.debug("Found {} lessons", lessonsForTeacher.size());
        CollectionModel<LessonDto> modelLessons
            = lessonAssembler.toCollectionModel(lessonsForTeacher);
        modelLessons.add(
            linkTo(methodOn(TeacherRestController.class)
                .getLessonsForTeacher(teacherId, from, to))
                .withSelfRel()
        );
        return modelLessons;
    }

    @Override
    protected Service<Teacher> getService() {
        return teacherService;
    }

    @Override
    protected RepresentationModelAssembler<Teacher, TeacherDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Teacher> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Teacher, TeacherDto> getMapper() {
        return mapper;
    }
}
