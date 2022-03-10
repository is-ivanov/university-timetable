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
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.ui.restcontroller.link.LessonDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.link.StudentDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_STUDENTS)
@Tag(name = "Student Controller", description = "Management students")
public class StudentRestController extends AbstractController<StudentDto, Student> {

    private final StudentService studentService;
    private final StudentDtoMapper mapper;
    private final StudentDtoAssembler assembler;
    private final LessonService lessonService;
    private final LessonDtoAssembler lessonAssembler;
    private final PagedResourcesAssembler<Student> pagedAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<StudentDto> getStudents() {
        log.debug("Getting all students");
        CollectionModel<StudentDto> students = getAllInternal();
        students.add(LinkBuilder.STUDENTS_SELF_LINK);
        return students;
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<StudentDto> getStudentsPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all students with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<StudentDto> getStudentsPaginated(@PageableDefault
                                                           (sort = "lastName")
                                                           Pageable pageable) {
        return getStudentsPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<StudentDto> getStudentsSorted(Pageable pageable) {
        return getStudentsPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public StudentDto getStudent(@PathVariable("id") int studentId) {
        log.debug("Getting student by id({})", studentId);
        return getByIdInternal(studentId);
    }

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody
                                                        StudentDto studentDto,
                                                    HttpServletRequest request) {
        log.debug("Creating {}", studentDto);
        return createInternal(studentDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public StudentDto updateStudent(@Valid @RequestBody StudentDto studentDto,
                                    @PathVariable("id") int studentId,
                                    HttpServletRequest request) {
        StudentDto updatedStudentDto = updateInternal(studentId, studentDto, request);
        log.debug("Student id({}) is updated", studentId);
        return updatedStudentDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable("id") int studentId) {
        log.debug("Deleting student with id({})", studentId);
        deleteInternal(studentId);
    }

    @GetMapping(MappingConstants.ID_TIMETABLE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<LessonDto> getLessonsForStudent(@PathVariable("id") int studentId,
                                                           @RequestParam("start")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                               ZonedDateTime from,
                                                           @RequestParam("end")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                               ZonedDateTime to) {
        log.debug("Getting lessons for student id({}) from {} to {}", studentId,
            from, to);
        List<Lesson> lessonsForStudent = lessonService
            .getAllForStudentForTimePeriod(studentId,
                from.toLocalDateTime(), to.toLocalDateTime());
        CollectionModel<LessonDto> modelLessons = lessonAssembler.toCollectionModel(lessonsForStudent);
        modelLessons.add(
            linkTo(methodOn(StudentRestController.class)
                .getLessonsForStudent(studentId, from, to))
                .withSelfRel()
        );
        return modelLessons;
    }

    @Override
    protected Service<Student> getService() {
        return studentService;
    }

    @Override
    protected RepresentationModelAssembler<Student, StudentDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Student> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Student, StudentDto> getMapper() {
        return mapper;
    }

}
