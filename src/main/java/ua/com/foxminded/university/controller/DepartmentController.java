package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final FacultyService facultyService;

    @Autowired
    public DepartmentController(DepartmentService groupService,
                                FacultyService facultyService) {
        this.departmentService = groupService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String groups(@RequestParam(value = "facultyId", required = false) Integer facultyId,
                         Model model) {
        List<Faculty> allFaculties = facultyService.getAllSortedAscByName();
        model.addAttribute("faculties", allFaculties);
        Faculty facultySelected = null;
        if (facultyId != null) {
            List<Department> departments = departmentService.getAllByFacultyId(facultyId);
            model.addAttribute("departments", departments);
            facultySelected = allFaculties.stream()
                .filter(faculty -> faculty.getId() == facultyId)
                .findFirst().orElse(null);
        }
        model.addAttribute("facultySelected", facultySelected);
        return "department";
    }
}