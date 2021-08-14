package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;

@Slf4j
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
        log.debug("Getting data for teacher.html");
        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
            model.addAttribute("isShowInactiveTeachers", true);
        }
        log.debug("Get faculties for selector");
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (departmentId != null && departmentId > 0) {
            log.debug("Get teachers by departmentId ({})", departmentId);
            model.addAttribute("teachers", teacherService.getAllByDepartment(departmentId));
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get teachers by facultyId ({})", facultyId);
            model.addAttribute("teachers", teacherService.getAllByFaculty(facultyId));
        }
        if (facultyId != null && facultyId > 0) {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
        } else {
            log.debug("Get all departments for selector");
            model.addAttribute("departments", departmentService.getAll());
        }
        log.debug("adding selected faculty and department into model");
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("departmentIdSelect", departmentId);
        log.info("The required data is loaded into the model");
        return "teacher";
    }

    @GetMapping(value = "/faculty")
    @ResponseBody
    public List<Department> getDepartments(@RequestParam Integer facultyId) {
        log.debug("Getting departments for selector");
        if (facultyId == 0) {
            log.debug("Get all departments for selector");
            return departmentService.getAll();
        } else {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            return departmentService.getAllByFaculty(facultyId);
        }
    }
}