package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/timetable")
public class TimetableController {

    public static final String TIMETABLE_TEMPLATE = "timetable";
    public static final String OBJECT = "object";

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final RoomService roomService;
    private final StudentDtoMapper studentDtoMapper;
    private final TeacherDtoMapper teacherDtoMapper;

    @GetMapping("/students/{id}")
    public String showStudentTimetable(@PathVariable("id") int studentId,
                                       Model model) {
        StudentDto studentDto = studentDtoMapper
            .studentToStudentDto(studentService.getById(studentId));
        model.addAttribute(OBJECT, studentDto);
        addTypeInModel(model, "students");
        return TIMETABLE_TEMPLATE;
    }


    @GetMapping("/teachers/{id}")
    public String showTeacherTimetable(@PathVariable("id") int teacherId,
                                       Model model) {
        TeacherDto teacherDto = teacherDtoMapper
            .teacherToTeacherDto(teacherService.getById(teacherId));
        model.addAttribute(OBJECT, teacherDto);
        addTypeInModel(model, "teachers");
        return TIMETABLE_TEMPLATE;
    }

    @GetMapping("/rooms/{id}")
    public String showRoomTimetable(@PathVariable("id") int roomId, Model model) {
        model.addAttribute(OBJECT, roomService.getById(roomId));
        addTypeInModel(model, "rooms");
        return TIMETABLE_TEMPLATE;
    }

    private void addTypeInModel(Model model, String type) {
        model.addAttribute("type", type);
    }

}
