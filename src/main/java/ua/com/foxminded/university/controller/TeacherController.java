package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;

    @Autowired
    public TeacherController(TeacherService teacherService,
                             FacultyService facultyService,
                             DepartmentService departmentService) {
        this.teacherService = teacherService;
        this.facultyService = facultyService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showDepartments(@RequestParam(value = "facultyId", required = false)
                                   Integer facultyId,
                               @RequestParam(value = "departmentId", required = false)
                                   Integer departmentId,
                               @RequestParam(value = "isShowInactiveTeachers",
                                   required = false) String isShowInactiveTeachers,
                               Model model) {
        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
            model.addAttribute("isShowInactiveTeachers", true);
        }
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (departmentId != null) {
            model.addAttribute("students",
                teacherService.getStudentsByGroup(groupId));
        } else if (facultyId != null) {
            model.addAttribute("students",
                teacherService.getStudentsByFaculty(facultyId));
        }
        if (facultyId != null) {
            model.addAttribute("departments", departmentService.getAllByFacultyId(facultyId));
        } else {
            model.addAttribute("departments", departmentService.getAll());
        }
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("departmentIdSelect", departmentId);
        return "teacher";
    }

    @GetMapping(value = "/faculty")
    @ResponseBody
    public List getDepartments(@RequestParam Integer facultyId) {
        return departmentService.getAllByFacultyId(facultyId);
    }
}