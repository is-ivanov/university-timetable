package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.restcontroller.link.FacultyModelAssembler;
import ua.com.foxminded.university.ui.util.Mappings;
import ua.com.foxminded.university.ui.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Mappings.API_FACULTIES)
@Validated
public class FacultyRestController {

    private final FacultyService facultyService;
    private final FacultyDtoMapper mapper;
    private final FacultyModelAssembler assembler;
    private final GroupService groupService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<FacultyDto>>> getFaculties() {
        log.debug("Getting all faculties");
        List<FacultyDto> faculties = facultyService.getAll();
        CollectionModel<EntityModel<FacultyDto>> entityModels =
            assembler.toCollectionModel(faculties);
        entityModels.add(linkTo(FacultyRestController.class).withSelfRel());
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping(Mappings.ID)
    public ResponseEntity<EntityModel<FacultyDto>> getFaculty(@PathVariable("id")
                                                                  int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        FacultyDto faculty = facultyService.getById(facultyId);

        return ResponseEntity.ok(assembler.toModel(faculty));
    }

    @PostMapping
    public ResponseEntity<EntityModel<FacultyDto>> createFaculty(@Valid @RequestBody
                                                                     FacultyDto faculty,
                                                                 HttpServletRequest request) {

        FacultyDto result = facultyService.save(mapper.toFaculty(faculty));
        log.debug("{} is created", faculty);

        EntityModel<FacultyDto> facultyModel = getEntityModel(result, request);
        URI location = facultyModel.getRequiredLink(IanaLinkRelations.SELF).toUri();

        return ResponseEntity.created(location).body(facultyModel);
    }

    @PutMapping(Mappings.ID)
    public ResponseEntity<Object> updateFaculty(@Valid @RequestBody
                                                    FacultyDto facultyDto,
                                                @PathVariable("id") int facultyId,
                                                HttpServletRequest request) {

        Faculty faculty = mapper.toFaculty(facultyDto);
        faculty.setId(facultyId);
        FacultyDto result = facultyService.save(faculty);
        log.debug("Faculty id({}) is updated", facultyId);

        EntityModel<FacultyDto> facultyModel = getEntityModel(result, request);
        return ResponseEntity.ok(facultyModel);
    }

    @DeleteMapping(Mappings.ID)
    public ResponseEntity<Object> deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        return ResponseEntity.noContent().build();
    }

    //TODO update methods
    @GetMapping(Mappings.ID_GROUPS)
    public List<GroupDto> getGroupsByFaculty(@PathVariable("id") int facultyId) {
        if (facultyId == 0) {
            log.debug("Get all groups");
            return groupService.getAll();
        } else {
            log.debug("Getting groups by faculty id({})", facultyId);
            return groupService.getAllByFacultyId(facultyId);
        }
    }

    @GetMapping(Mappings.ID_GROUPS_FREE)
    public List<GroupDto> getFreeGroupsByFaculty(@PathVariable("id") int facultyId,
                                                 @RequestParam("time_start")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime startTime,
                                                 @RequestParam("time_end")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime endTime) {
        log.debug("Getting active groups by faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<GroupDto> freeGroups = groupService
            .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        log.debug("Found {} groups", freeGroups.size());
        return freeGroups;
    }

    @GetMapping(Mappings.ID_DEPARTMENTS)
    public List<DepartmentDto> getDepartmentsByFaculty(@PathVariable("id") int facultyId) {
        if (facultyId == 0) {
            log.debug("Getting all departments");
            return departmentService.getAll();
        } else {
            log.debug("Getting departments by facultyId ({})", facultyId);
            return departmentService.getAllByFaculty(facultyId);
        }
    }

    @GetMapping(Mappings.ID_TEACHERS)
    public List<TeacherDto> getTeachersByFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting teacherDtos by faculty id({})", facultyId);
        List<TeacherDto> teachers = teacherService.getAllByFaculty(facultyId);
        log.debug("Found {} teachers", teachers.size());
        return teachers;
    }


    private EntityModel<FacultyDto> getEntityModel(FacultyDto result,
                                                   HttpServletRequest request) {
        EntityModel<FacultyDto> facultyModel = assembler.toModel(result);
        addRedirectUrl(request, facultyModel);
        return facultyModel;
    }

    private void addRedirectUrl(HttpServletRequest request,
                                EntityModel<FacultyDto> facultyModel) {
        String redirectUrl = ResponseUtil.getRedirectUrl(request);
        if (redirectUrl != null) {
            facultyModel.add(Link.of(redirectUrl, "redirect"));
        }
    }
}
