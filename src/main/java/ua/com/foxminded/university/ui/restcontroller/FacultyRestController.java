package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.ui.util.Mappings;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Mappings.API_FACULTIES)
public class FacultyRestController {

    private final FacultyService facultyService;

    @GetMapping
    public List<Faculty> getFaculties() {
        log.debug("Getting all faculties");
        return facultyService.getAll();
    }

    @GetMapping(Mappings.ID)
    public Faculty getFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        return facultyService.getById(facultyId);
    }

    @PostMapping
    public ResponseEntity<Object> createFaculty(@RequestBody @Valid Faculty faculty,
                                                UriComponentsBuilder builder) {
        log.debug("Creating {}", faculty);
        Faculty result = facultyService.save(faculty);

        URI location = builder.path(Mappings.API_FACULTY).buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).body(result);
    }


}
