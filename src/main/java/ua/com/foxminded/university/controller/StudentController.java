package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

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
        if (isShowInactiveGroups != null && isShowInactiveGroups.equals("on")) {
            model.addAttribute("isShowInactiveGroups", true);
        }
        if (isShowInactiveStudents != null && isShowInactiveStudents.equals("on")) {
            model.addAttribute("isShowInactiveStudents", true);
        }
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (groupId != null) {
            model.addAttribute("students",
                studentService.getStudentsByGroup(groupId));
        } else if (facultyId != null) {
            model.addAttribute("students",
                studentService.getStudentsByFaculty(facultyId));
        }
        if (facultyId != null) {
            model.addAttribute("groups", groupService.getAllByFacultyId(facultyId));
        } else {
            model.addAttribute("groups", groupService.getAll());
        }
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("groupIdSelect", groupId);
        return "student";
    }

    @GetMapping(value = "/faculty")
    @ResponseBody
    public List getGroups(@RequestParam Integer facultyId) {
        return groupService.getAllByFacultyId(facultyId);
    }
}