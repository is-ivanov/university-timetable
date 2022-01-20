package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final TeacherDtoMapper teacherMapper;
    private final LessonService lessonService;

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
            model.addAttribute("teachers",
                teacherService.getAllByDepartment(departmentId));
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get teachers by facultyId ({})", facultyId);
            model.addAttribute("teachers",
                teacherService.getAllByFaculty(facultyId));
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

    @GetMapping("/free")
    @ResponseBody
    public List<Teacher> getFreeTeachers(@RequestParam("time_start")
                                            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                LocalDateTime startTime,
                                            @RequestParam("time_end")
                                            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                LocalDateTime endTime) {
        log.debug("Getting teachers free from {} to {}", startTime, endTime);
        List<Teacher> freeTeachers =
            teacherService.getFreeTeachersOnLessonTime(startTime, endTime);
        log.debug("Found {} active free teachers", freeTeachers.size());
        return freeTeachers;
    }

    @PostMapping
    public ResponseEntity<String> createTeacher(@ModelAttribute @Valid TeacherDto teacherDto,
                                                HttpServletRequest request) {
        log.debug("Creating teacher [{} {} {}]", teacherDto.getFirstName(),
            teacherDto.getPatronymic(), teacherDto.getLastName());
        teacherService.create(teacherMapper.toTeacher(teacherDto));
        log.debug("Teacher [{}, {}, {}] is created", teacherDto.getFirstName(),
            teacherDto.getPatronymic(), teacherDto.getLastName());
        return getResponseEntityWithRedirectUrl(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Teacher getTeacher(@PathVariable("id") int teacherId) {
        log.debug("Getting teacher id({})", teacherId);
        Teacher teacher = teacherService.findById(teacherId);
        log.debug("Found teacher [{} {} {}]", teacher.getFirstName(),
            teacher.getPatronymic(), teacher.getLastName());
        return teacher;
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTeacher(@ModelAttribute @Valid TeacherDto teacherDto,
                                                @PathVariable("id") int teacherId,
                                                HttpServletRequest request) {
        log.debug("Updating teacher id({})", teacherId);
//        teacherService.save(teacherMapper.toTeacher(teacherDto));
        log.debug("Teacher id({}) is updated", teacherId);
        return getResponseEntityWithRedirectUrl(request);
    }

    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable("id") int teacherId,
                                HttpServletRequest request) {
        log.debug("Deleting teacher id({})", teacherId);
        teacherService.delete(teacherId);
        log.debug("Teacher id({}) is deleted", teacherId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/timetable")
    @ResponseBody
    public List<LessonDto> getLessonsForTeacher(@PathVariable("id") int teacherId,
                                                @RequestParam("start")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    ZonedDateTime startTime,
                                                @RequestParam("end")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    ZonedDateTime endTime) {
        log.debug("Getting lessons for teacher id({}) from {} to {}", teacherId,
            startTime, endTime);
        List<LessonDto> lessonsForTeacher = lessonService
            .getAllForTeacherForTimePeriod(teacherId,
                startTime.toLocalDateTime(), endTime.toLocalDateTime());
        log.debug("Found {} lessons", lessonsForTeacher.size());
        return lessonsForTeacher;
    }

}