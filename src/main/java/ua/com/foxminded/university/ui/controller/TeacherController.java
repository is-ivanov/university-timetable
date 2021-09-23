package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.dao.impl.LessonDaoImpl;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

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
    private final LessonDtoMapper lessonDtoMapper;

//    @GetMapping
//    public String showTeachers(@RequestParam(required = false) Integer facultyId,
//                               @RequestParam(required = false) Integer departmentId,
//                               @RequestParam(required = false) String isShowInactiveTeachers,
//                               Model model) {
//        log.debug("Getting data for teacher.html");
//        if (isShowInactiveTeachers != null && isShowInactiveTeachers.equals("on")) {
//            model.addAttribute("isShowInactiveTeachers", true);
//        }
//        log.debug("Get faculties for selector");
//        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
//        if (departmentId != null && departmentId > 0) {
//            log.debug("Get teachers by departmentId ({})", departmentId);
//            model.addAttribute("teachers",
//                teacherMapper.teachersToTeacherDtos(
//                    teacherService.getAllByDepartment(departmentId)));
//        } else if (facultyId != null && facultyId > 0) {
//            log.debug("Get teachers by facultyId ({})", facultyId);
//            model.addAttribute("teachers",
//                teacherMapper.teachersToTeacherDtos(
//                    teacherService.getAllByFaculty(facultyId)));
//        }
//        if (facultyId != null && facultyId > 0) {
//            log.debug("Get departments for selector by facultyId ({})", facultyId);
//            model.addAttribute("departments", departmentService.getAllByFaculty(facultyId));
//        } else {
//            log.debug("Get all departments for selector");
//            model.addAttribute("departments", departmentService.getAll());
//        }
//        log.debug("adding selected faculty and department into model");
//        model.addAttribute("facultyIdSelect", facultyId);
//        model.addAttribute("departmentIdSelect", departmentId);
//        model.addAttribute("newTeacher", new TeacherDto());
//        log.info("The required data is loaded into the model");
//        return "teacher";
//    }
//
//    @GetMapping("/free")
//    @ResponseBody
//    public List<TeacherDto> getFreeTeachers(@RequestParam("time_start")
//                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//                                                LocalDateTime startTime,
//                                            @RequestParam("time_end")
//                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//                                                LocalDateTime endTime) {
//        log.debug("Getting teachers free from {} to {}", startTime, endTime);
//        List<Teacher> freeTeachers =
//            teacherService.getFreeTeachersOnLessonTime(startTime, endTime);
//        log.info("Found {} active free teachers", freeTeachers.size());
//        return teacherMapper.teachersToTeacherDtos(freeTeachers);
//    }
//
//    @PostMapping
//    public String createTeacher(@ModelAttribute TeacherDto teacherDto,
//                                HttpServletRequest request) {
//        log.debug("Creating teacher [{} {} {}]", teacherDto.getFirstName(),
//            teacherDto.getPatronymic(), teacherDto.getLastName());
//        teacherService.add(teacherMapper.teacherDtoToTeacher(teacherDto));
//        log.info("Teacher [{}, {}, {}] is created", teacherDto.getFirstName(),
//            teacherDto.getPatronymic(), teacherDto.getLastName());
//        return defineRedirect(request);
//    }
//
//    @GetMapping("/{id}")
//    @ResponseBody
//    public TeacherDto getTeacher(@PathVariable("id") int teacherId) {
//        log.debug("Getting teacher id({})", teacherId);
//        Teacher teacher = teacherService.getById(teacherId);
//        log.info("Found teacher [{} {} {}]", teacher.getFirstName(),
//            teacher.getPatronymic(), teacher.getLastName());
//        return teacherMapper.teacherToTeacherDto(teacher);
//    }
//
//    @PutMapping("/{id}")
//    public String updateTeacher(@ModelAttribute TeacherDto teacherDto,
//                                @PathVariable("id") int teacherId,
//                                HttpServletRequest request) {
//        log.debug("Updating teacher id({})", teacherId);
//        teacherService.update(teacherMapper.teacherDtoToTeacher(teacherDto));
//        log.info("Teacher id({}) is updated", teacherId);
//        return defineRedirect(request);
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteTeacher(@PathVariable("id") int teacherId,
//                                HttpServletRequest request) {
//        log.debug("Deleting teacher id({})", teacherId);
//        teacherService.delete(teacherId);
//        log.info("Teacher id({}) is deleted", teacherId);
//        return defineRedirect(request);
//    }
//
//    @GetMapping("/{id}/timetable")
//    @ResponseBody
//    public List<LessonDto> getLessonsForTeacher(@PathVariable("id") int teacherId,
//                                                @RequestParam("start")
//                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                                                    ZonedDateTime startTime,
//                                                @RequestParam("end")
//                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//                                                    ZonedDateTime endTime) {
//        log.debug("Getting lessons for teacher id({}) from {} to {}", teacherId,
//            startTime, endTime);
//        List<Lesson> lessonsForTeacher = lessonService
//            .getAllForTeacherForTimePeriod(teacherId,
//                startTime.toLocalDateTime(), endTime.toLocalDateTime());
//        List<LessonDto> lessonDtos = lessonDtoMapper.lessonsToLessonDtos(lessonsForTeacher);
//        log.info("Found {} lessons", lessonDtos.size());
//        return lessonDtos;
//    }

}