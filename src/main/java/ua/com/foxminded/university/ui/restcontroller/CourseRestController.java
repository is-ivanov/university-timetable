package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
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

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public CourseDto getCourse(@PathVariable("id") int courseId) {
        log.debug("Getting course by id({})", courseId);
        return getById(courseId);
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
