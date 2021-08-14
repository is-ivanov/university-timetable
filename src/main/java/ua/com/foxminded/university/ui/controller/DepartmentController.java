package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final FacultyService facultyService;

    @GetMapping
    public String showDepartments(@RequestParam(required = false) Integer facultyId,
                                  Model model) {
        log.debug("Getting data for department.html. Get faculties for selector");
        List<Faculty> allFaculties = facultyService.getAllSortedByNameAsc();
        model.addAttribute("faculties", allFaculties);
        Faculty facultySelected = null;
        List<Department> departments;
        if (facultyId != null && facultyId > 0) {
            log.debug("get departments by facultyId ({})", facultyId);
            departments = departmentService.getAllByFaculty(facultyId);
            log.debug("get selected faculty");
            facultySelected = allFaculties.stream()
                .filter(faculty -> faculty.getId() == facultyId)
                .findFirst().orElse(null);
        } else {
            log.debug("get all departments");
            departments = departmentService.getAll();
        }
        log.debug("adding departments and selected faculty into model");
        model.addAttribute("departments", departments);
        model.addAttribute("facultySelected", facultySelected);
        log.info("The list of departments and selected faculty is loaded into the model");
        return "department";
    }
}