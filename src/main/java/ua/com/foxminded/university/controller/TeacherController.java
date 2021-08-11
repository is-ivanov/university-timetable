package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;

    @GetMapping
    public String showTeachers(@RequestParam(required = false) Integer facultyId,
                               @RequestParam(required = false) Integer departmentId,
                               @RequestParam(required = false) String isShowInactiveTeachers,
                               Model model) {
        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
            model.addAttribute("isShowInactiveTeachers", true);
        }
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (departmentId != null && departmentId > 0) {
            model.addAttribute("teachers", teacherService.getAllByDepartment(departmentId));
        } else if (facultyId != null && facultyId > 0) {
            model.addAttribute("teachers", teacherService.getAllByFaculty(facultyId));
        }
        if (facultyId != null && facultyId > 0) {
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
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
        if (facultyId == 0) {
            return departmentService.getAll();
        } else {
            return departmentService.getAllByFaculty(facultyId);
        }
    }
}