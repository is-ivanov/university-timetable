package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.validator.OnCreate;
import ua.com.foxminded.university.ui.restcontroller.link.FacultyModelAssembler;
import ua.com.foxminded.university.ui.util.Mappings;
import ua.com.foxminded.university.ui.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Mappings.API_FACULTIES)
@Validated
public class FacultyRestController {

    private final FacultyService facultyService;
    private final FacultyDtoMapper mapper;
    private final FacultyModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<FacultyDto>> getFaculties() {
        log.debug("Getting all faculties");
        List<FacultyDto> facultyDtos = facultyService.getAll();
        CollectionModel<EntityModel<FacultyDto>> entityModels =
            assembler.toCollectionModel(facultyDtos);
        return entityModels.add(linkTo(methodOn(FacultyRestController.class).getFaculties()).withSelfRel());
    }

    @GetMapping(Mappings.ID)
    public EntityModel<FacultyDto> getFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        FacultyDto faculty = facultyService.getById(facultyId);

        return assembler.toModel(faculty);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createFaculty(@Valid @RequestBody FacultyDto faculty,
                                                UriComponentsBuilder builder,
                                                HttpServletRequest request) {
        log.debug("Creating {}", faculty);
        Faculty result = facultyService.save(mapper.toFaculty(faculty));
        log.debug("{} is created", faculty);
        return ResponseUtil.getPostResponseRedirectUrl(request, Mappings.API_FACULTY,
            builder, result.getId());
    }


}
