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
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;
import ua.com.foxminded.university.ui.restcontroller.link.FacultyDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_FACULTIES)
public class FacultyRestController extends AbstractController<FacultyDto, Faculty> {

    private final FacultyService facultyService;
    private final FacultyDtoMapper mapper;
    private final FacultyDtoAssembler assembler;
    private final GroupService groupService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;
    private final PagedResourcesAssembler<Faculty> pagedAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<FacultyDto> getFaculties() {
        log.debug("Getting all faculties");
        return getAllInternal();
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<FacultyDto> getFacultiesPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all faculties with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<FacultyDto> getFacultiesPaginated(@PageableDefault(sort = "name")
                                                      Pageable pageable) {
        return getFacultiesPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<FacultyDto> getFacultiesSorted(Pageable pageable) {
        return getFacultiesPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public FacultyDto getFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        return getByIdInternal(facultyId);
    }

    @PostMapping
    public ResponseEntity<FacultyDto> createFaculty(@Valid @RequestBody FacultyDto facultyDto,
                                                    HttpServletRequest request) {
        log.debug("Creating {}", facultyDto);
        return createInternal(facultyDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public FacultyDto updateFaculty(@Valid @RequestBody FacultyDto facultyDto,
                                    @PathVariable("id") int facultyId) {
        FacultyDto updatedFacultyDto = updateInternal(facultyId, facultyDto);
        log.debug("Faculty id({}) is updated", facultyId);
        return updatedFacultyDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        deleteInternal(facultyId);
    }

//    @GetMapping(MappingConstants.ID_GROUPS)
//    public List<GroupDto> getGroupsByFaculty(@PathVariable("id") int facultyId) {
//        if (facultyId == 0) {
//            log.debug("Get all groups");
//            return groupService.getAll();
//        } else {
//            log.debug("Getting groups by faculty id({})", facultyId);
//            return groupService.getAllByFacultyId(facultyId);
//        }
//    }

//    @GetMapping(MappingConstants.ID_GROUPS_FREE)
//    public List<Group> getFreeGroupsByFaculty(@PathVariable("id") int facultyId,
//                                              @RequestParam("time_start")
//                                              @DateTimeFormat(pattern = ResponseUtil.DATE_TIME_PATTERN)
//                                                  LocalDateTime startTime,
//                                              @RequestParam("time_end")
//                                              @DateTimeFormat(pattern = ResponseUtil.DATE_TIME_PATTERN)
//                                                  LocalDateTime endTime) {
//        log.debug("Getting active groups by faculty id({}) free from {} to {}",
//            facultyId, startTime, endTime);
//        List<Group> freeGroups = groupService
//            .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
//        log.debug("Found {} groups", freeGroups.size());
//        return freeGroups;
//    }

//    @GetMapping(MappingConstants.ID_DEPARTMENTS)
//    public List<DepartmentDto> getDepartmentsByFaculty(@PathVariable("id") int facultyId) {
//        if (facultyId == 0) {
//            log.debug("Getting all departments");
//            return departmentService.getAll();
//        } else {
//            log.debug("Getting departments by facultyId ({})", facultyId);
//            return departmentService.getAllByFaculty(facultyId);
//        }
//    }

//    @GetMapping(MappingConstants.ID_TEACHERS)
//    public List<Teacher> getTeachersByFaculty(@PathVariable("id") int facultyId) {
//        log.debug("Getting teacherDtos by faculty id({})", facultyId);
//        List<Teacher> teachers = teacherService.getAllByFaculty(facultyId);
//        log.debug("Found {} teachers", teachers.size());
//        return teachers;
//    }

    @Override
    protected Service<Faculty> getService() {
        return facultyService;
    }

    @Override
    protected RepresentationModelAssembler<Faculty, FacultyDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Faculty> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Faculty, FacultyDto> getMapper() {
        return mapper;
    }

}
