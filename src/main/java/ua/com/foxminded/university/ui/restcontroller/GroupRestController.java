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
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.ui.restcontroller.link.GroupDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.link.StudentDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(MappingConstants.API_GROUPS)
@Validated
@Tag(name = "Group Controller", description = "Management groups")
public class GroupRestController extends AbstractController<GroupDto, Group> {

    private final GroupService groupService;
    private final GroupDtoMapper mapper;
    private final GroupDtoAssembler assembler;
    private final StudentService studentService;
    private final StudentDtoAssembler studentAssembler;
    private final PagedResourcesAssembler<Group> pagedAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<GroupDto> getGroups() {
        log.debug("Getting all groups");
        CollectionModel<GroupDto> groups = getAllInternal();
        groups.add(LinkBuilder.GROUPS_SELF_LINK);
        return groups;
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<GroupDto> getGroupsPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all groups with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<GroupDto> getGroupsPaginated(@PageableDefault(sort = "name")
                                                       Pageable pageable) {
        return getGroupsPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<GroupDto> getGroupsSorted(Pageable pageable) {
        return getGroupsPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public GroupDto getGroup(@PathVariable("id") int groupId) {
        log.debug("Getting group by id({})", groupId);
        return getByIdInternal(groupId);
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto,
                                                HttpServletRequest request) {
        log.debug("Creating {}", groupDto);
        return createInternal(groupDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public GroupDto updateGroup(@Valid @RequestBody GroupDto groupDto,
                                @PathVariable("id") int groupId,
                                HttpServletRequest request) {
        GroupDto updatedGroupDto = updateInternal(groupId, groupDto, request);
        log.debug("Group id({}) is updated", groupId);
        return updatedGroupDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable("id") int groupId) {
        log.debug("Deleting group with id({})", groupId);
        deleteInternal(groupId);
    }

    @GetMapping(MappingConstants.ID_STUDENTS_FREE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<StudentDto> getFreeStudentsFromGroup(@PathVariable("id") int groupId,
                                                                @RequestParam("time_start")
                                                                @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                    LocalDateTime from,
                                                                @RequestParam("time_end")
                                                                @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                    LocalDateTime to) {

        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, from, to);
        List<Student> freeStudentsFromGroup =
            studentService.getFreeStudentsFromGroup(groupId, from, to);

        CollectionModel<StudentDto> modelStudents =
            studentAssembler.toCollectionModel(freeStudentsFromGroup);

        modelStudents.add(
            linkTo(methodOn(GroupRestController.class)
                .getFreeStudentsFromGroup(groupId, from, to))
                .withSelfRel()
        );
        return modelStudents;
    }

    @Override
    protected Service<Group> getService() {
        return groupService;
    }

    @Override
    protected RepresentationModelAssembler<Group, GroupDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Group> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Group, GroupDto> getMapper() {
        return mapper;
    }
}
