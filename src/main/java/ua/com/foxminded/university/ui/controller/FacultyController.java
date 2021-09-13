package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/faculties")
public class FacultyController {

    public static final String URI_FACULTIES = "/faculties";

    private final FacultyService facultyService;
    private final GroupService groupService;
    private final DepartmentService departmentService;
    private final TeacherService teacherService;
    private final TeacherDtoMapper teacherDtoMapper;

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
        return defineRedirect(URI_FACULTIES);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Faculty getFaculty(@PathVariable("id") int facultyId) {
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
        return defineRedirect(URI_FACULTIES);
    }

    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable("id") int facultyId) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        log.info("Faculty id({}) is deleted", facultyId);
        return defineRedirect(URI_FACULTIES);
    }

    @GetMapping("/{id}/groups")
    @ResponseBody
    public List<Group> getGroupsByFaculty(@PathVariable("id") int facultyId) {
        if (facultyId == 0) {
            log.debug("Get all groups");
            return groupService.getAll();
        } else {
            log.debug("Getting groups by faculty id({})", facultyId);
            return groupService.getAllByFacultyId(facultyId);
        }
    }

    @GetMapping("/{id}/groups/free")
    @ResponseBody
    public List<Group> getFreeGroupsByFaculty(@PathVariable("id") int facultyId,
                                              @RequestParam("time_start")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                                  LocalDateTime startTime,
                                              @RequestParam("time_end")
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                                  LocalDateTime endTime) {
        log.debug("Getting active groups by faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Group> freeGroups = groupService
            .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        log.info("Found {} groups", freeGroups.size());
        return freeGroups;
    }

    @GetMapping("/{id}/departments")
    @ResponseBody
    public List<Department> getDepartmentsByFaculty(@PathVariable("id") int facultyId) {
        if (facultyId == 0) {
            log.debug("Getting all departments");
            return departmentService.getAll();
        } else {
            log.debug("Getting departments by facultyId ({})", facultyId);
            return departmentService.getAllByFaculty(facultyId);
        }
    }

    @GetMapping("/{id}/teachers")
    @ResponseBody
    public List<TeacherDto> getTeachersByFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting teacherDtos by faculty id({})", facultyId);
        List<TeacherDto> teachers = teacherDtoMapper
            .teachersToTeacherDtos(teacherService.getAllByFaculty(facultyId));
        log.info("Found {} teachers", teachers.size());
        return teachers;
    }
}
