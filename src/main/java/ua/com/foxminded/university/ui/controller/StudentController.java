package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final GroupService groupService;
    private final LessonService lessonService;
    private final StudentDtoMapper studentMapper;
    private final LessonDtoMapper lessonDtoMapper;

    @GetMapping
    public String showStudents(@RequestParam(required = false) Integer facultyId,
                               @RequestParam(required = false) Integer groupId,
                               @RequestParam(required = false) String isShowInactiveGroups,
                               @RequestParam(required = false) String isShowInactiveStudents,
                               Model model) {
        log.debug("Getting data for student.html");
        if (isShowInactiveGroups != null && isShowInactiveGroups.equals("on")) {
            model.addAttribute("isShowInactiveGroups", true);
        }
        if (isShowInactiveStudents != null && isShowInactiveStudents.equals("on")) {
            model.addAttribute("isShowInactiveStudents", true);
        }
        log.debug("Get faculties for selector");
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        if (groupId != null && groupId > 0) {
            log.debug("Get students by groupId ({})", groupId);
            model.addAttribute("students",
                studentMapper.studentsToStudentDtos(
                    studentService.getStudentsByGroup(groupId)));
        } else if (facultyId != null && facultyId > 0) {
            log.debug("Get students by facultyId ({})", facultyId);
            model.addAttribute("students",
                studentMapper.studentsToStudentDtos(
                    studentService.getStudentsByFaculty(facultyId)));
        }
        if (facultyId != null && facultyId > 0) {
            log.debug("Get groups for selector by facultyId ({})", facultyId);
            model.addAttribute("groups", groupService.getAllByFacultyId(facultyId));
        } else {
            log.debug("Get all groups for selector");
            model.addAttribute("groups", groupService.getAll());
        }
        log.debug("adding selected faculty and group into model");
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("groupIdSelect", groupId);
        model.addAttribute("newStudent", new StudentDto());
        log.info("The required data is loaded into the model");
        return "student";
    }

//    @GetMapping(value = "/groups")
//    @ResponseBody
//    public List<Group> getGroups(@RequestParam Integer facultyId) {
//        log.debug("Getting groups for selector");
//        if (facultyId == 0) {
//            log.debug("Get all groups for selector");
//            return groupService.getAll();
//        } else {
//            log.debug("Get groups for selector by facultyId ({})", facultyId);
//            return groupService.getAllByFacultyId(facultyId);
//        }
//    }
//
    @PostMapping
    public String createStudent(@ModelAttribute StudentDto studentDto,
                                HttpServletRequest request) {
        log.debug("Creating student [{} {} {}]", studentDto.getFirstName(),
            studentDto.getPatronymic(), studentDto.getLastName());
        studentService.add(studentMapper.studentDtoToStudent(studentDto));
        log.info("Student [{}, {}, {}] is created", studentDto.getFirstName(),
            studentDto.getPatronymic(), studentDto.getLastName());
        return defineRedirect(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public StudentDto getStudent(@PathVariable("id") int studentId) {
        log.debug("Getting student id({})", studentId);
        Student student = studentService.getById(studentId);
        log.info("Found student [{} {} {}]", student.getFirstName(),
            student.getPatronymic(), student.getLastName());
        return studentMapper.studentToStudentDto(student);
    }

    @PutMapping("/{id}")
    public String updateStudent(@ModelAttribute StudentDto studentDto,
                                @PathVariable("id") int studentId,
                                HttpServletRequest request) {
        log.debug("Updating student id({})", studentId);
        studentService.update(studentMapper.studentDtoToStudent(studentDto));
        log.info("Student id({}) is updated", studentId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable("id") int studentId,
                                HttpServletRequest request) {
        log.debug("Deleting student id({})", studentId);
        studentService.delete(studentId);
        log.info("Student id({}) is deleted", studentId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/timetable")
    @ResponseBody
    public List<LessonDto> getLessonsForStudent(@PathVariable("id") int studentId,
                                                @RequestParam("start")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    ZonedDateTime startTime,
                                                @RequestParam("end")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    ZonedDateTime endTime) {
        log.debug("Getting lessons for student id({}) from {} to {}", studentId,
            startTime, endTime);
        List<Lesson> lessonsForStudent = lessonService
            .getAllForStudentForTimePeriod(studentId,
                startTime.toLocalDateTime(), endTime.toLocalDateTime());
        List<LessonDto> lessonDtos = lessonDtoMapper.lessonsToLessonDtos(lessonsForStudent);
        log.info("Found {} lessons", lessonDtos.size());
        return lessonDtos;
    }

}