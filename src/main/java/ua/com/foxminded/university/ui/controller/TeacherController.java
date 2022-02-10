package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.restcontroller.link.TeacherDtoAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final TeacherDtoAssembler assembler;

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
            List<Teacher> teachers = teacherService.getAllByDepartment(departmentId);
            model.addAttribute("teachers",
                assembler.toCollectionModel(teachers));
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get teachers by facultyId ({})", facultyId);
            List<Teacher> teachers = teacherService.getAllByFaculty(facultyId);
            model.addAttribute("teachers",
                assembler.toCollectionModel(teachers));
        }
        if (facultyId != null && facultyId > 0) {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
        } else {
            log.debug("Get all departments for selector");
            model.addAttribute("departments", departmentService.findAll());
        }
        log.debug("adding selected faculty and department into model");
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("departmentIdSelect", departmentId);
        model.addAttribute("newTeacher", new Teacher());
        log.debug("The required data is loaded into the model");
        return "teacher";
    }

    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable("id") int teacherId,
                                HttpServletRequest request) {
        log.debug("Deleting teacher id({})", teacherId);
        teacherService.delete(teacherId);
        log.debug("Teacher id({}) is deleted", teacherId);
        return defineRedirect(request);
    }

}