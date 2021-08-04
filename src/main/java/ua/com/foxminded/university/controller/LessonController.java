package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;

@Controller
@RequestMapping("/lesson")
public class LessonController {

    @Autowired
    private LessonService lessonService;
    @Autowired
    private FacultyService facultyService;
    @Autowired
    private TeacherService teacherService;

    //TODO constructor

    @GetMapping
    public String showLessons(@RequestParam(required = false) Integer facultyId,
                              Model model) {
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());

        return "lesson";
    }

    @GetMapping("/department")
    @ResponseBody
    public List getTeachers(@RequestParam Integer departmentId) {
        return teacherService.getAllByDepartment(departmentId);
    }
}