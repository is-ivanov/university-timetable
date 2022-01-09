package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/faculties")
public class FacultyRestController {

    private final FacultyService facultyService;

    @GetMapping
    public List<Faculty> getFaculties() {
        log.debug("Getting all faculties");
        return facultyService.getAll();
    }

    @GetMapping("/{id}")
    public Faculty getFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        return facultyService.getById(facultyId);
    }

    @PostMapping
    public ResponseEntity<?> createFaculty(@RequestBody @Valid Faculty faculty,
                                                HttpServletRequest request) {
        log.debug("Creating {}", faculty);
        facultyService.save(faculty);
        log.debug("{} is created", faculty);
        return ResponseEntity.created("");
    }


}
