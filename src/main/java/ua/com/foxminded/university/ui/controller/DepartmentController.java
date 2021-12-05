package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;

    @GetMapping
    public String showDepartments(@RequestParam(required = false) Integer facultyId,
                                  Model model) {
        log.debug("Getting data for department.html. Get faculties for selector");
        List<Faculty> allFaculties = facultyService.getAllSortedByNameAsc();
        model.addAttribute("faculties", allFaculties);
        Faculty facultySelected = null;
        List<DepartmentDto> departments;
        if (facultyId != null && facultyId > 0) {
            log.debug("get departments by facultyId ({})", facultyId);
            departments = departmentService.getAllByFaculty(facultyId);
            log.debug("get selected faculty");
            facultySelected = allFaculties.stream()
                .filter(faculty -> faculty.getId().equals(facultyId))
                .findFirst().orElse(null);
        } else {
            log.debug("get all departments");
            departments = departmentService.getAll();
        }
        log.debug("adding departments and selected faculty into model");
        model.addAttribute("departments", departments);
        model.addAttribute("facultySelected", facultySelected);
        model.addAttribute("newDepartment", new Department());
        log.info("The list of departments and selected faculty is loaded into the model");
        return "department";
    }

    @PostMapping
    public String createDepartment(@ModelAttribute Department department,
                                   HttpServletRequest request) {
        log.debug("Creating {}", department);
        departmentService.add(department);
        log.info("{} is created", department);
        return defineRedirect(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public DepartmentDto getDepartment(@PathVariable("id") int departmentId) {
        log.debug("Getting department id({})", departmentId);
        DepartmentDto department = departmentService.getById(departmentId);
        log.info("Found {}", department);
        return department;
    }

    @PutMapping("/{id}")
    public String updateDepartment(@ModelAttribute Department department,
                                   @PathVariable("id") int departmentId,
                                   HttpServletRequest request) {
        log.debug("Updating department id({})", departmentId);
        departmentService.update(department);
        log.info("Department id({}) is updated", departmentId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteDepartment(@PathVariable("id") int departmentId,
                                   HttpServletRequest request) {
        log.debug("Deleting department id({})", departmentId);
        departmentService.delete(departmentId);
        log.info("Department id({}) is deleted", departmentId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/teachers")
    @ResponseBody
    public List<TeacherDto> getTeachersByDepartment(@PathVariable("id") int departmentId) {
        log.debug("Getting teacherDtos by department id({})", departmentId);
        List<TeacherDto> teachers = teacherService.getAllByDepartment(departmentId);
        log.info("Found {} teachers", teachers.size());
        return teachers;
    }
}
