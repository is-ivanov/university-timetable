package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.DATE_TIME_PATTERN;
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
    private final PageSequenceCreator pageSequenceCreator;

    @GetMapping
    public String showFaculties(Model model,
                                @PageableDefault(sort = "name") Pageable pageable) {
        log.debug("Getting data for faculty.html");
        Page<Faculty> pageFaculties = facultyService.getAllSortedPaginated(pageable);
        model.addAttribute("faculties", pageFaculties.getContent());
        model.addAttribute("page", pageFaculties);
        model.addAttribute("uri", URI_FACULTIES);
        model.addAttribute("newFaculty", new Faculty());
        model.addAttribute("pages", pageSequenceCreator
            .createPageSequence(pageFaculties.getTotalPages(),
                pageFaculties.getNumber() + 1));
        log.debug("The required data is loaded into the model");
        return "faculty";
    }

    @PostMapping
    public String createFaculty(@ModelAttribute @Valid Faculty faculty,
                                BindingResult result,
                                RedirectAttributes attributes,
                                HttpServletRequest request) {
        if (result.hasErrors()) {
            attributes.addAttribute("hasErrors", true);
            return "faculty";
        }
        log.debug("Creating {}", faculty);
        facultyService.save(faculty);
        log.debug("{} is created", faculty);
        return defineRedirect(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Faculty getFaculty(@PathVariable("id") int facultyId) {
        log.debug("Getting faculty by id({})", facultyId);
        Faculty faculty = facultyService.getById(facultyId);
        log.debug("Found {}", faculty);
        return faculty;
    }

    @PutMapping("/{id}")
    public String updateFaculty(@ModelAttribute Faculty faculty,
                                @PathVariable("id") int facultyId,
                                HttpServletRequest request) {
        log.debug("Updating faculty with id({})", faculty);
        facultyService.save(faculty);
        log.debug("Faculty id({}) is updated", facultyId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteFaculty(@PathVariable("id") int facultyId,
                                HttpServletRequest request) {
        log.debug("Deleting faculty with id({})", facultyId);
        facultyService.delete(facultyId);
        log.debug("Faculty id({}) is deleted", facultyId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/groups")
    @ResponseBody
    public List<GroupDto> getGroupsByFaculty(@PathVariable("id") int facultyId) {
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

    @GetMapping("/{id}/departments")
    @ResponseBody
    public List<DepartmentDto> getDepartmentsByFaculty(@PathVariable("id") int facultyId) {
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
        List<TeacherDto> teachers = teacherService.getAllByFaculty(facultyId);
        log.debug("Found {} teachers", teachers.size());
        return teachers;
    }

}
