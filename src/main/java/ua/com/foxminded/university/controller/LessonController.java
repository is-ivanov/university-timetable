package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final TeacherDtoMapper teacherMapper;

    @GetMapping
    public String showLessons(@RequestParam(required = false) Integer facultyId,
                              @RequestParam(required = false) Integer departmentId,
                              Model model) {
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("departmentIdSelect", departmentId);
        List<Teacher> teachers;
        if (facultyId != null) {
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
            teachers = teacherService.getAllByFaculty(facultyId);
        } else {
            model.addAttribute("departments", departmentService.getAll());
            teachers = teacherService.getAll();
        }

        return "lesson";
    }

    @GetMapping("/department")
    @ResponseBody
    public List<TeacherDto> getTeachers(@RequestParam Integer departmentId) {
        List<Teacher> teachersByDepartment =
            teacherService.getAllByDepartment(departmentId);
        return convertListTeachersToDto(teachersByDepartment);
    }

    private List<TeacherDto> convertListTeachersToDto(List<Teacher> teachers) {
        return teacherMapper.teachersToTeacherDtos(teachers);
    }
}