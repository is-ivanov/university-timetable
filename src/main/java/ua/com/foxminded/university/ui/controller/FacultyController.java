package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/faculties")
public class FacultyController {

    public static final String REDIRECT_FACULTIES = "redirect:/faculties";

    private final FacultyService facultyService;

    @GetMapping
    public String showFaculties(Model model) {
        log.debug("Getting data for faculty.html");
        model.addAttribute("faculties", facultyService.getAll());
        model.addAttribute("newFaculty", new Faculty());
        log.info("The list of faculties is loaded into the model");
        return "faculty";
    }

    @PostMapping
    public String createFaculty(@ModelAttribute Faculty faculty) {
        log.debug("Creating {}", faculty);
        facultyService.add(faculty);
        log.info("{} is create", faculty);
        return REDIRECT_FACULTIES;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Faculty showFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        Faculty faculty = facultyService.getById(facultyId);
        log.info("Found {}", faculty);
        return faculty;
    }

    @PutMapping("/{id}")
    public String updateFaculty(@ModelAttribute Faculty faculty,
        @PathVariable("id") int facultyId) {
        log.debug("Updating faculty id({})", faculty);
        facultyService.update(faculty);
        log.info("Faculty id({}) is updated", facultyId);
        return REDIRECT_FACULTIES;
    }

    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        log.info("Delete faculty with id ({})", facultyId);
        return REDIRECT_FACULTIES;
    }

}
