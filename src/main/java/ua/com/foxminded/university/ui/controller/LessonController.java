package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;
import static ua.com.foxminded.university.ui.Util.getResponseEntityWithRedirectUrl;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/lessons")
public class LessonController {

    public static final String GET_ALL_TEACHERS_FOR_SELECTOR = "Get all teachers for selector";
    public static final String LESSONS = "all_lessons";

    private final LessonService lessonService;
    private final FacultyService facultyService;
    private final TeacherService teacherService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final RoomService roomService;
    private final GroupService groupService;
    private final LessonDtoMapper lessonDtoMapper;

    @GetMapping
    public String showLessons(Model model) {
        model.addAttribute("lessonFilter", new LessonFilter());
        log.debug("The required data is loaded into the model. Loading page");
        return LESSONS;
    }

    @GetMapping("/filter")
    public String showFilteredLessons(@RequestParam(required = false) String isShowInactiveTeachers,
                                      @RequestParam(required = false) String isShowPastLessons,
                                      @ModelAttribute LessonFilter lessonFilter,
                                      Model model) {
        log.debug("Getting data for all_lessons.html with filter");
        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
            model.addAttribute("isShowInactiveTeachers", true);
        }
        if (isShowPastLessons != null && isShowPastLessons.equals("on")) {
            model.addAttribute("isShowPastLessons", true);
        }
        Integer departmentId = lessonFilter.getDepartmentId();
        Integer facultyId = lessonFilter.getFacultyId();
        List<TeacherDto> teachers = getTeachersByFacultyOrDepartment(departmentId, facultyId);
        model.addAttribute("teachers", teachers);
        model.addAttribute("departments", getDepartmentsByFaculty(facultyId));

        log.debug("Get filtered lessons");
        model.addAttribute("lessons",
            lessonService.getAllWithFilter(lessonFilter));
        model.addAttribute("newLesson", new LessonDto());
        log.debug("The required data is loaded into the model");
        return LESSONS;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LessonDto getLessonWithStudents(@PathVariable("id") int lessonId) {
        log.debug("Getting lesson id({})", lessonId);
        LessonDto lessonDto = lessonService.getById(lessonId);
        log.debug("Found lesson [teacher {}, course {}, room {}]",
            lessonDto.getTeacherFullName(), lessonDto.getCourseName(),
            lessonDto.getBuildingAndRoom());
        return lessonDto;
    }

    @GetMapping("/{id}/students")
    public String showLessonWithStudents(@PathVariable("id") int lessonId,
                                         Model model) {
        log.debug("Getting data for lesson.html for lesson id({})", lessonId);
        LessonDto lesson = lessonService.getById(lessonId);
        model.addAttribute("lesson", lesson);

        LocalDateTime timeStart = lesson.getTimeStart();
        LocalDateTime timeEnd = lesson.getTimeEnd();
        int teacherId = lesson.getTeacherId();
        TeacherDto teacher = teacherService.getById(teacherId);
        int roomId = lesson.getRoomId();
        Room room = roomService.getById(roomId);

        log.debug("Loading free teachers in model");
        List<TeacherDto> freeTeachers = teacherService
            .getFreeTeachersOnLessonTime(timeStart, timeEnd);
        freeTeachers.add(teacher);
        model.addAttribute("teachers", freeTeachers);

        log.debug("Loading free rooms in model");
        List<Room> freeRooms = roomService
            .getFreeRoomsOnLessonTime(timeStart, timeEnd);
        freeRooms.add(room);
        model.addAttribute("rooms", freeRooms);

        log.debug("Loading active groups in model");
        model.addAttribute("groups",
            groupService.getFreeGroupsOnLessonTime(timeStart, timeEnd));

        log.debug("The required data is loaded into the model. Loading page lesson id({})",
            lessonId);
        return "lesson";
    }

    @PostMapping
    public ResponseEntity<String> createLesson(@ModelAttribute @Valid LessonDto lessonDto,
                                               HttpServletRequest request) {
        log.debug("Creating lesson {}", lessonDto);
        lessonService.save(lessonDtoMapper.toLesson(lessonDto));
        log.debug("Lesson {} is created", lessonDto);
        return getResponseEntityWithRedirectUrl(request);
    }

    @PostMapping("/{id}/students")
    public String addStudentToLesson(@PathVariable("id") int lessonId,
                                     @RequestParam int studentId,
                                     HttpServletRequest request) {
        log.debug("Adding student id({}) to lesson id({})", studentId, lessonId);
        lessonService.addStudentToLesson(lessonId, studentId);
        log.debug("Student id({}) added to lesson id({}) successfully", studentId, lessonId);
        return defineRedirect(request);
    }

    @PostMapping("/{id}/groups")
    public String addStudentsFromGroupToLesson(@PathVariable("id") int lessonId,
                                               @RequestParam int groupId,
                                               HttpServletRequest request) {
        log.debug("Adding all students from group id({}) to lesson id({})",
            groupId, lessonId);
        lessonService.addStudentsFromGroupToLesson(groupId, lessonId);
        log.debug("Student from group id({}) is added to lesson id({})", groupId,
            lessonId);
        return defineRedirect(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLesson(@ModelAttribute @Valid LessonDto lessonDto,
                                               @PathVariable("id") int lessonId,
                                               HttpServletRequest request) {
        log.debug("Updating lesson id({})", lessonId);
        lessonDto.setId(lessonId);
        lessonService.update(lessonDto);
        log.debug("Lesson id({}) updated successfully", lessonId);
        return getResponseEntityWithRedirectUrl(request);
    }

    @DeleteMapping("/{id}/students")
    public String removeStudentFromLesson(@PathVariable("id") int lessonId,
                                          @RequestParam Integer[] studentIds,
                                          HttpServletRequest request) {
        if (studentIds.length == 1) {
            log.debug("Remove student id({}) from lesson id({})", studentIds, lessonId);
            lessonService.removeStudentFromLesson(lessonId, studentIds[0]);
        } else {
            log.debug("Remove students id({}) from lesson id({})", studentIds, lessonId);
            lessonService.removeStudentsFromLesson(lessonId, studentIds);
        }
        log.debug("Students id({}) successfully removed from lesson id({})",
            studentIds, lessonId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable("id") int lessonId,
                               HttpServletRequest request) {
        log.debug("Deleting lesson id({})", lessonId);
        lessonService.delete(lessonId);
        log.debug("Lesson id({}) is deleted", lessonId);
        return defineRedirect(request);
    }

    @ModelAttribute("faculties")
    public List<Faculty> getFaculties() {
        log.debug("Loading list faculties for selector");
        return facultyService.getAllSortedByNameAsc();
    }

    @ModelAttribute("departments")
    public List<DepartmentDto> getDepartments() {
        log.debug("Loading list departments for selector");
        return departmentService.getAll();
    }

    @ModelAttribute("teachers")
    public List<TeacherDto> getTeachers() {
        log.debug("Loading list teachers for selector");
        return teacherService.getAll();
    }

    @ModelAttribute("courses")
    public List<Course> getCourses() {
        log.debug("Loading list courses for selector");
        return courseService.getAll();
    }

    @ModelAttribute("rooms")
    public List<Room> getRooms() {
        log.debug("Loading list rooms for selector");
        return roomService.getAll();
    }

    private List<DepartmentDto> getDepartmentsByFaculty(Integer facultyId) {
        if (facultyId != null && facultyId > 0) {
            log.debug("Get departments for selector by facultyId ({})", facultyId);
            return departmentService.getAllByFaculty(facultyId);
        } else {
            log.debug("Get all departments for selector");
            return departmentService.getAll();
        }
    }

    private List<TeacherDto> getTeachersByFacultyOrDepartment(Integer departmentId, Integer facultyId) {
        List<TeacherDto> teachers;
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
        return teachers;
    }
}