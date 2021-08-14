package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final GroupService groupService;

    @GetMapping
    public String showStudents(@RequestParam(required = false) Integer facultyId,
                               @RequestParam(required = false) Integer groupId,
                               @RequestParam(required = false) String isShowInactiveGroups,
                               @RequestParam(required = false) String isShowInactiveStudents,
                               Model model) {
        log.debug("Getting data for student.html");
        if (isShowInactiveGroups != null && isShowInactiveGroups.equals("on")) {
            model.addAttribute("isShowInactiveGroups", true);
        }
        if (isShowInactiveStudents != null && isShowInactiveStudents.equals("on")) {
            model.addAttribute("isShowInactiveStudents", true);
        }
        log.debug("Get faculties for selector");
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (groupId != null && groupId > 0) {
            log.debug("Get students by groupId ({})", groupId);
            model.addAttribute("students",
                studentService.getStudentsByGroup(groupId));
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get students by facultyId ({})", facultyId);
            model.addAttribute("students",
                studentService.getStudentsByFaculty(facultyId));
        }
        if (facultyId != null && facultyId > 0) {
            log.debug("Get groups for selector by facultyId ({})", facultyId);
            model.addAttribute("groups", groupService.getAllByFacultyId(facultyId));
        } else {
            log.debug("Get all groups for selector");
            model.addAttribute("groups", groupService.getAll());
        }
        log.debug("adding selected faculty and group into model");
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("groupIdSelect", groupId);
        log.info("The required data is loaded into the model");
        return "student";
    }

    @GetMapping(value = "/faculty")
    @ResponseBody
    public List<Group> getGroups(@RequestParam Integer facultyId) {
        log.debug("Getting groups for selector");
        if (facultyId == 0) {
            log.debug("Get all groups for selector");
            return groupService.getAll();
        } else {
            log.debug("Get groups for selector by facultyId ({})", facultyId);
            return groupService.getAllByFacultyId(facultyId);
        }
    }
}