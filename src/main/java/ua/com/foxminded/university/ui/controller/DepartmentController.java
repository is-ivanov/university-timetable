package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.ui.restcontroller.link.DepartmentDtoAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final FacultyService facultyService;
    private final DepartmentDtoAssembler assembler;

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
                .filter(faculty -> faculty.getId().equals(facultyId))
                .findFirst().orElse(null);
        } else {
            log.debug("get all departments");
            departments = departmentService.findAll();
        }
        log.debug("adding departments and selected faculty into model");
        model.addAttribute("departments", assembler.toCollectionModel(departments));
        model.addAttribute("facultySelected", facultySelected);
        model.addAttribute("newDepartment", new Department());
        log.debug("The list of departments and selected faculty is loaded into the model");
        return "department";
    }


    @DeleteMapping("/{id}")
    public String deleteDepartment(@PathVariable("id") int departmentId,
                                   HttpServletRequest request) {
        log.debug("Deleting department id({})", departmentId);
        departmentService.delete(departmentId);
        log.debug("Department id({}) is deleted", departmentId);
        return defineRedirect(request);
    }
}
