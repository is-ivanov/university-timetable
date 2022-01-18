package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.exception.MyPageNotFoundException;
import ua.com.foxminded.university.ui.restcontroller.link.FacultyDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;
import ua.com.foxminded.university.ui.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(MappingConstants.API_FACULTIES)
@Validated
public class FacultyRestController {

    private final FacultyService facultyService;
    private final FacultyDtoMapper mapper;
    private final FacultyDtoAssembler assembler;
    private final GroupService groupService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;
    private final PagedResourcesAssembler<Faculty> pagedAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<FacultyDto>> getFaculties() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = facultyService.findAll();
        CollectionModel<FacultyDto> entityModels = assembler.toCollectionModel(faculties);
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT_BY})
    public ResponseEntity<PagedModel<FacultyDto>> getAllPaginatedAndSorted(Pageable pageable) {
        return getPaginatedResponse(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    public ResponseEntity<PagedModel<FacultyDto>> getAllPaginated(@PageableDefault(sort = "name")
                                                                          Pageable pageable) {
        return getPaginatedResponse(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT_BY})
    public ResponseEntity<PagedModel<FacultyDto>> getAllSorted(Pageable pageable) {
        return getPaginatedResponse(pageable);
    }

    @GetMapping(MappingConstants.ID)
    public ResponseEntity<FacultyDto> getFaculty(@PathVariable("id")
                                                                  int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        Faculty faculty = facultyService.findById(facultyId);

        return ResponseEntity.ok(assembler.toModel(faculty));
    }

    private ResponseEntity<PagedModel<FacultyDto>> getPaginatedResponse(Pageable pageable) {
        Page<Faculty> pageOfFaculties = facultyService.findAllSortedAndPaginated(pageable);
        int requestPageNumber = pageable.getPageNumber() + 1;
        if (requestPageNumber > pageOfFaculties.getTotalPages()) {
            throw new MyPageNotFoundException(requestPageNumber,
                pageOfFaculties.getTotalPages(), pageable.getPageSize());
        }
        PagedModel<FacultyDto> facultyDtos = pagedAssembler.toModel(pageOfFaculties, assembler);
        return ResponseEntity.ok(facultyDtos);
    }

    @PostMapping
    public ResponseEntity<FacultyDto> createFaculty(@Valid @RequestBody
                                                                     FacultyDto faculty,
                                                                 HttpServletRequest request) {

        Faculty result = facultyService.create(mapper.toFaculty(faculty));
        log.debug("{} is created", result);
        FacultyDto resultModel = assembler.toModel(result);
        URI location = resultModel.getRequiredLink(IanaLinkRelations.SELF).toUri();
        addRedirectUrl(request, resultModel);
        return ResponseEntity.created(location).body(resultModel);
    }

    @PutMapping(MappingConstants.ID)
    public ResponseEntity<Object> updateFaculty(@Valid @RequestBody
                                                    FacultyDto facultyDto,
                                                @PathVariable("id") int facultyId,
                                                HttpServletRequest request) {

        Faculty faculty = mapper.toFaculty(facultyDto);
        faculty.setId(facultyId);
        Faculty result = facultyService.update(facultyId, faculty);
        log.debug("Faculty id({}) is updated", facultyId);

//        EntityModel<FacultyDto> facultyModel = getEntityModel(result, request);
//        return ResponseEntity.ok(facultyModel);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(MappingConstants.ID)
    public ResponseEntity<Object> deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        return ResponseEntity.noContent().build();
    }

//    //TODO update methods
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

    @GetMapping(MappingConstants.ID_GROUPS_FREE)
    public List<Group> getFreeGroupsByFaculty(@PathVariable("id") int facultyId,
                                                 @RequestParam("time_start")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime startTime,
                                                 @RequestParam("time_end")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime endTime) {
        log.debug("Getting active groups by faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Group> freeGroups = groupService
            .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        log.debug("Found {} groups", freeGroups.size());
        return freeGroups;
    }

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

    @GetMapping(MappingConstants.ID_TEACHERS)
    public List<Teacher> getTeachersByFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting teacherDtos by faculty id({})", facultyId);
        List<Teacher> teachers = teacherService.getAllByFaculty(facultyId);
        log.debug("Found {} teachers", teachers.size());
        return teachers;
    }


//    private EntityModel<FacultyDto> getEntityModel(FacultyDto result,
//                                                   HttpServletRequest request) {
//        EntityModel<FacultyDto> facultyModel = assembler.toModel(result);
//        addRedirectUrl(request, facultyModel);
//        return facultyModel;
//    }

    private void addRedirectUrl(HttpServletRequest request,
                                FacultyDto facultyModel) {
        String redirectUrl = ResponseUtil.getRedirectUrl(request);
        if (redirectUrl != null) {
            facultyModel.add(Link.of(redirectUrl, "redirect"));
        }
    }
}
