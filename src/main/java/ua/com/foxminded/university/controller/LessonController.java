package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/lesson")
public class LessonController {

    public static final String GET_ALL_TEACHERS_FOR_SELECTOR = "Get all teachers for selector";

    private final LessonService lessonService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final RoomService roomService;

    @GetMapping
    public String showLessons(Model model) {
        model.addAttribute("lessonFilter", new LessonFilter());
        log.info("The required data is loaded into the model");
        return "lesson";
    }

    @PostMapping("/filter")
    public String showFilteredLessons(@RequestParam(required = false) String isShowInactiveTeachers,
                                      @RequestParam(required = false) String isShowPastLessons,
                                      @ModelAttribute LessonFilter lessonFilter,
                                      Model model) {
        log.debug("Getting data for lesson.html with filter");
        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
            model.addAttribute("isShowInactiveTeachers", true);
        }
        if (isShowPastLessons != null && isShowPastLessons.equals("on")) {
            model.addAttribute("isShowPastLessons", true);
        }
        Integer departmentId = lessonFilter.getDepartmentId();
        Integer facultyId = lessonFilter.getFacultyId();
        List<Teacher> teachers;
        if (departmentId != null && departmentId > 0) {
            log.debug("Get teachers for selector by departmentId ({})",
                departmentId);
            teachers = teacherService.getAllByDepartment(departmentId);
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get teachers for selector by facultyId ({})", facultyId);
            teachers = teacherService.getAllByFaculty(facultyId);
        } else {
            log.debug(GET_ALL_TEACHERS_FOR_SELECTOR);
            teachers = teacherService.getAll();
        }
        model.addAttribute("teachers",
            teacherService.convertListTeachersToDto(teachers));
        if (facultyId != null && facultyId > 0) {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
        } else {
            log.debug("Get all departments for selector");
            model.addAttribute("departments", departmentService.getAll());
        }
        log.debug("Get filtered lessons");
        model.addAttribute("lessons",
            lessonService.convertListLessonsToDto(lessonService.getAllWithFilter(lessonFilter)));
        log.info("The required data is loaded into the model");
        return "lesson";
    }

    @GetMapping("/department")
    @ResponseBody
    public List<TeacherDto> getTeachersByDepartment(@RequestParam Integer departmentId) {
        log.debug("Getting teachers for selector");
        List<Teacher> teachersByDepartment;
        if (departmentId == 0) {
            log.debug(GET_ALL_TEACHERS_FOR_SELECTOR);
            teachersByDepartment = teacherService.getAll();
        } else {
            log.debug("Get teachers for selector by departmentId ({})", departmentId);
            teachersByDepartment = teacherService.getAllByDepartment(departmentId);
        }
        return teacherService.convertListTeachersToDto(teachersByDepartment);
    }

    @GetMapping("/faculty")
    @ResponseBody
    public List<TeacherDto> getTeachersByFaculty(@RequestParam Integer facultyId) {
        log.debug("Getting teachers for selector");
        List<Teacher> teachersByFaculty;
        if (facultyId == 0) {
            log.debug(GET_ALL_TEACHERS_FOR_SELECTOR);
            teachersByFaculty = teacherService.getAll();
        } else {
            log.debug("Get teachers for selector by facultyId ({})", facultyId);
            teachersByFaculty = teacherService.getAllByFaculty(facultyId);
        }
        return teacherService.convertListTeachersToDto(teachersByFaculty);
    }

    @ModelAttribute("faculties")
    public List<Faculty> populateFaculties() {
        return facultyService.getAllSortedByNameAsc();
    }

    @ModelAttribute("departments")
    public List<Department> populateDepartments() {
        return departmentService.getAll();
    }

    @ModelAttribute("teachers")
    public List<TeacherDto> populateTeachers() {
        return teacherService.convertListTeachersToDto(teacherService.getAll());
    }

    @ModelAttribute("courses")
    public List<Course> populateCourses() {
        return courseService.getAll();
    }

    @ModelAttribute("rooms")
    public List<Room> populateRooms() {
        return roomService.getAll();
    }

}