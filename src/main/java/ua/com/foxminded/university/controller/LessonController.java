package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final RoomService roomService;
    private final TeacherDtoMapper teacherMapper;
    private final LessonDtoMapper lessonMapper;

    @GetMapping
    public String show(Model model) {
        model.addAttribute("lessonFilter", new LessonFilter());
        return "lesson";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam(required = false) String isShowInactiveTeachers,
                         @RequestParam(required = false) String isShowPastLessons,
                         @ModelAttribute LessonFilter lessonFilter,
                         Model model) {
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
            teachers = teacherService.getAllByDepartment(departmentId);
        } else if (facultyId != null && facultyId > 0) {
            teachers = teacherService.getAllByFaculty(facultyId);
        } else {
            teachers = teacherService.getAll();
        }
        model.addAttribute("teachers", convertListTeachersToDto(teachers));
        if (facultyId != null && facultyId > 0) {
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
        } else {
            model.addAttribute("departments", departmentService.getAll());
        }
        model.addAttribute("lessons",
            convertListLessonsToDto(lessonService.getAllWithFilter(lessonFilter)));
        return "lesson";
    }

    @GetMapping("/department")
    @ResponseBody
    public List<TeacherDto> getTeachersByDepartment(@RequestParam Integer departmentId) {
        List<Teacher> teachersByDepartment;
        if (departmentId == 0) {
            teachersByDepartment = teacherService.getAll();
        } else {
            teachersByDepartment = teacherService.getAllByDepartment(departmentId);
        }
        return convertListTeachersToDto(teachersByDepartment);
    }

    @GetMapping("/faculty")
    @ResponseBody
    public List<TeacherDto> getTeachersByFaculty(@RequestParam Integer facultyId) {
        List<Teacher> teachersByFaculty;
        if (facultyId == 0) {
            teachersByFaculty = teacherService.getAll();
        } else {
            teachersByFaculty = teacherService.getAllByFaculty(facultyId);
        }
        return convertListTeachersToDto(teachersByFaculty);
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
        return convertListTeachersToDto(teacherService.getAll());
    }

    @ModelAttribute("courses")
    public List<Course> populateCourses() {
        return courseService.getAll();
    }

    @ModelAttribute("rooms")
    public List<Room> populateRooms() {
        return roomService.getAll();
    }

    private List<TeacherDto> convertListTeachersToDto(List<Teacher> teachers) {
        return teacherMapper.teachersToTeacherDtos(teachers);
    }

    private List<LessonDto> convertListLessonsToDto(List<Lesson> lessons) {
        return lessonMapper.lessonsToLessonDtos(lessons);
    }
}