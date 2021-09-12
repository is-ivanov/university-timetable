package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/lessons")
public class LessonController {

    public static final String GET_ALL_TEACHERS_FOR_SELECTOR = "Get all teachers for selector";

    private final LessonService lessonService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final RoomService roomService;
    private final GroupService groupService;
    private final LessonDtoMapper lessonDtoMapper;
    private final TeacherDtoMapper teacherDtoMapper;

    @GetMapping
    public String showLessons(Model model) {
        model.addAttribute("lessonFilter", new LessonFilter());
        log.info("The required data is loaded into the model. Loading page");
        return "lessons";
    }

    @GetMapping("/filter")
    public String showFilteredLessons(@RequestParam(required = false) String isShowInactiveTeachers,
                                      @RequestParam(required = false) String isShowPastLessons,
                                      @ModelAttribute LessonFilter lessonFilter,
                                      Model model) {
        log.debug("Getting data for lessons.html with filter");
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
            teacherDtoMapper.teachersToTeacherDtos(teachers));
        if (facultyId != null && facultyId > 0) {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
        } else {
            log.debug("Get all departments for selector");
            model.addAttribute("departments", departmentService.getAll());
        }
        log.debug("Get filtered lessons");
        model.addAttribute("lessons",
            lessonDtoMapper.lessonsToLessonDtos(lessonService.getAllWithFilter(lessonFilter)));
        model.addAttribute("newLesson", new LessonDto());
        log.info("The required data is loaded into the model");
        return "lessons";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LessonDto showLessonWithStudents(@PathVariable("id") int lessonId) {
        log.debug("Getting lesson id({})", lessonId);
        LessonDto lessonDto = lessonDtoMapper.lessonToLessonDto(lessonService.getById(lessonId));
        log.info("Found lesson [teacher {}, course {}, room {}]",
            lessonDto.getTeacherFullName(), lessonDto.getCourseName(),
            lessonDto.getBuildingAndRoom());
        return lessonDto;
    }

    @GetMapping("/{id}/students")
    public String showLessonWithStudents(@PathVariable("id") int lessonId,
                                         Model model) {
        log.debug("Getting data for lesson.html for lesson id({})", lessonId);
        Lesson lesson = lessonService.getById(lessonId);
        LessonDto lessonDto = lessonDtoMapper.lessonToLessonDto(lesson);
        model.addAttribute("lesson", lessonDto);

        LocalDateTime timeStart = lesson.getTimeStart();
        LocalDateTime timeEnd = lesson.getTimeEnd();
        Teacher teacher = lesson.getTeacher();
        Room room = lesson.getRoom();

        log.debug("Loading free teachers in model");
        List<Teacher> freeTeachers = teacherService
            .getFreeTeachersOnLessonTime(timeStart, timeEnd);
        freeTeachers.add(teacher);
        model.addAttribute("teachers",
            teacherDtoMapper.teachersToTeacherDtos(freeTeachers));

        log.debug("Loading free rooms in model");
        List<Room> freeRooms = roomService
            .getFreeRoomsOnLessonTime(timeStart, timeEnd);
        freeRooms.add(room);
        model.addAttribute("rooms", freeRooms);

        log.debug("Loading active groups in model");
        model.addAttribute("groups",
            groupService.getFreeGroupsOnLessonTime(timeStart, timeEnd));

        log.info("The required data is loaded into the model. Loading page lesson id({})",
            lessonId);
        return "lesson";
    }

    @GetMapping("/departments")
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
        return teacherDtoMapper.teachersToTeacherDtos(teachersByDepartment);
    }

    @GetMapping("/faculties")
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
        return teacherDtoMapper.teachersToTeacherDtos(teachersByFaculty);
    }

    @PostMapping
    public String createLesson(@ModelAttribute LessonDto lessonDto,
                               @RequestParam(required = false) String uri) {
        log.debug("Creating lesson {}", lessonDto);
        lessonService.add(lessonDtoMapper.lessonDtoToLesson(lessonDto));
        log.info("Lesson {} is created", lessonDto);
        return defineRedirect(uri);
    }

    @PostMapping("/{id}/students")
    public String addStudentToLesson(@PathVariable("id") int lessonId,
                                     @RequestParam int studentId,
                                     HttpServletRequest request) {
        log.debug("Adding student id({}) to lesson id({})", studentId, lessonId);
        lessonService.addStudentToLesson(lessonId, studentId);
        log.info("Student id({}) added to lesson id({}) successfully", studentId, lessonId);
        return defineRedirect(request);
    }

    @PostMapping("/{id}/groups")
    public String addStudentsFromGroupToLesson(@PathVariable("id") int lessonId,
                                               @RequestParam int groupId,
                                               HttpServletRequest request) {
        log.debug("Adding all students from group id({}) to lesson id({})",
            groupId, lessonId);
        lessonService.addStudentsFromGroupToLesson(groupId, lessonId);
        log.info("Student from group id({}) is added to lesson id({})", groupId,
            lessonId);
        return defineRedirect(request);
    }

    @PutMapping("/{id}")
    public String updateLesson(@ModelAttribute LessonDto lessonDto,
                               @PathVariable("id") int lessonId,
                               HttpServletRequest request) {
        log.debug("Updating lesson id({})", lessonId);
        lessonService.update(lessonDtoMapper.lessonDtoToLesson(lessonDto));
        log.info("Lesson id({}) updated successfully", lessonId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}/students")
    public String removeStudentFromLesson(@PathVariable("id") int lessonId,
                                          @RequestParam int[] studentIds,
                                          HttpServletRequest request) {
        if (studentIds.length == 1) {
            log.debug("Remove student id({}) from lesson id({})", studentIds, lessonId);
            lessonService.removeStudentFromLesson(lessonId, studentIds[0]);
        } else {
            log.debug("Remove students id({}) from lesson id({})", studentIds, lessonId);
            lessonService.removeStudentsFromLesson(lessonId, studentIds);
        }
        log.info("Students id({}) successfully removed from lesson id({})",
            studentIds, lessonId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable("id") int lessonId,
                               @RequestParam(required = false) String uri) {
        log.debug("Deleting lesson id({})", lessonId);
        lessonService.delete(lessonId);
        log.info("Lesson id({}) is deleted", lessonId);
        return defineRedirect(uri);
    }

    @ModelAttribute("faculties")
    public List<Faculty> populateFaculties() {
        log.debug("Loading list faculties for selector");
        return facultyService.getAllSortedByNameAsc();
    }

    @ModelAttribute("departments")
    public List<Department> populateDepartments() {
        log.debug("Loading list departments for selector");
        return departmentService.getAll();
    }

    @ModelAttribute("teachers")
    public List<TeacherDto> populateTeachers() {
        log.debug("Loading list teachers for selector");
        return teacherDtoMapper.teachersToTeacherDtos(teacherService.getAll());
    }

    @ModelAttribute("courses")
    public List<Course> populateCourses() {
        log.debug("Loading list courses for selector");
        return courseService.getAll();
    }

    @ModelAttribute("rooms")
    public List<Room> populateRooms() {
        log.debug("Loading list rooms for selector");
        return roomService.getAll();
    }

}