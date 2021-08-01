package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final GroupService groupService;

    @Autowired
    public StudentController(StudentService studentService,
                             FacultyService facultyService,
                             GroupService groupService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.groupService = groupService;
    }

    @GetMapping
    public String showStudents(@RequestParam(value = "facultyId", required = false)
                                   Integer facultyId,
                               @RequestParam(value = "groupId", required = false)
                                   Integer groupId,
                               @RequestParam(value = "isShowInactiveGroups",
                                   required = false) String isShowInactiveGroup,
                               @RequestParam(value = "isShowInactiveStudents",
                                   required = false) String isShowInactiveStudents,
                               Model model) {
        if (isShowInactiveGroup != null && isShowInactiveGroup.equals("on")) {
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