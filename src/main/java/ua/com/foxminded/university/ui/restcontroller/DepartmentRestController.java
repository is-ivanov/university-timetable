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
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.restcontroller.link.DepartmentDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.link.TeacherDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_DEPARTMENTS)
public class DepartmentRestController extends AbstractController<DepartmentDto, Department> {

    private final DepartmentService departmentService;
    private final DepartmentDtoMapper mapper;
    private final DepartmentDtoAssembler assembler;
    private final PagedResourcesAssembler<Department> pagedAssembler;
    private final TeacherService teacherService;
    private final TeacherDtoAssembler teacherAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<DepartmentDto> getDepartments() {
        log.debug("Getting all departments");
        CollectionModel<DepartmentDto> departments = getAllInternal();
        departments.add(LinkBuilder.DEPARTMENTS_SELF_LINK);
        return departments;
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<DepartmentDto> getDepartmentsPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all courses with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<DepartmentDto> getDepartmentsPaginated(@PageableDefault(sort = "name")
                                                                 Pageable pageable) {
        return getDepartmentsPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<DepartmentDto> getDepartmentsSorted(Pageable pageable) {
        return getDepartmentsPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public DepartmentDto getDepartment(@PathVariable("id") int departmentId) {
        log.debug("Getting department by id({})", departmentId);
        return getByIdInternal(departmentId);
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto,
                                                          HttpServletRequest request) {
        log.debug("Creating {}", departmentDto);
        return createInternal(departmentDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public DepartmentDto updateDepartment(@Valid @RequestBody DepartmentDto departmentDto,
                                          @PathVariable("id") int departmentId,
                                          HttpServletRequest request) {
        DepartmentDto updatedDepartmentDto = updateInternal(departmentId, departmentDto, request);
        log.debug("Department id({}) is updated", departmentId);
        return updatedDepartmentDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable("id") int departmentId) {
        log.debug("Deleting department with id({})", departmentId);
        deleteInternal(departmentId);
    }

    @GetMapping(MappingConstants.ID_TEACHERS)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<TeacherDto> getTeachersByDepartment(@PathVariable("id") int departmentId) {
        log.debug("Getting teacherDtos by department id({})", departmentId);
        List<Teacher> teachers = teacherService.getAllByDepartment(departmentId);
        CollectionModel<TeacherDto> teacherDtos = teacherAssembler.toCollectionModel(teachers);
        teacherDtos.add(linkTo(methodOn(DepartmentRestController.class)
            .getTeachersByDepartment(departmentId))
            .withSelfRel()
        );
        return teacherDtos;
    }

    @Override
    protected Service<Department> getService() {
        return departmentService;
    }

    @Override
    protected RepresentationModelAssembler<Department, DepartmentDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Department> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Department, DepartmentDto> getMapper() {
        return mapper;
    }
}
