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
import ua.com.foxminded.university.domain.dto.CourseDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.mapper.CourseDtoMapper;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.ui.restcontroller.link.CourseDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_COURSES)
public class CourseRestController extends AbstractController<CourseDto, Course> {

    private final CourseService courseService;
    private final CourseDtoMapper mapper;
    private final CourseDtoAssembler assembler;
    private final PagedResourcesAssembler<Course> pagedAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<CourseDto> getCourses() {
        log.debug("Getting all courses");
        return getAllInternal();
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<CourseDto> getCoursesPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all courses with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<CourseDto> getCoursesPaginated(@PageableDefault(sort = "name")
                                                         Pageable pageable) {
        return getCoursesPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<CourseDto> getCoursesSorted(Pageable pageable) {
        return getCoursesPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public CourseDto getCourse(@PathVariable("id") int courseId) {
        log.debug("Getting course by id({})", courseId);
        return getByIdInternal(courseId);
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto,
                                                  HttpServletRequest request) {
        log.debug("Creating {}", courseDto);
        return createInternal(courseDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public CourseDto updateCourse(@Valid @RequestBody CourseDto courseDto,
                                  @PathVariable("id") int courseId,
                                  HttpServletRequest request) {
        CourseDto updatedCourseDto = updateInternal(courseId, courseDto, request);
        log.debug("Course id({}) is updated", courseId);
        return updatedCourseDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable("id") int courseId) {
        log.debug("Deleting course with id({})", courseId);
        deleteInternal(courseId);
    }

    @Override
    protected Service<Course> getService() {
        return courseService;
    }

    @Override
    protected RepresentationModelAssembler<Course, CourseDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Course> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Course, CourseDto> getMapper() {
        return mapper;
    }
}
