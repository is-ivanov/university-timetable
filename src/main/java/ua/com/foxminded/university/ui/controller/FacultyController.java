package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/faculties")
public class FacultyController {

    public static final String REDIRECT_FACULTIES = "redirect:/faculties";

    private final FacultyService facultyService;
    private final GroupService groupService;
    private final StudentDtoMapper studentDtoMapper;
    private final StudentService studentService;

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
        log.info("{} is created", faculty);
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
        log.debug("Updating faculty with id({})", faculty);
        facultyService.update(faculty);
        log.info("Faculty id({}) is updated", facultyId);
        return REDIRECT_FACULTIES;
    }

    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        log.info("Faculty id({}) is deleted", facultyId);
        return REDIRECT_FACULTIES;
    }

    @GetMapping("/{id}/groups")
    @ResponseBody
    public List<Group> getGroupsByFaculty(@PathVariable int id) {
        log.debug("Getting groups by faculty id({})", id);
        if (id == 0) {
            log.debug("Get all groups for selector");
            return groupService.getAll();
        } else {
            log.debug("Get groups for selector by faculty id({})", id);
            return groupService.getAllByFacultyId(id);
        }
    }

}
