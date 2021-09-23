package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final FacultyService facultyService;
    private final StudentService studentService;
    private final StudentDtoMapper studentDtoMapper;

    @GetMapping
    public String showGroups(@RequestParam(required = false) Integer facultyId,
                             @RequestParam(required = false) String isShowInactive,
                             Model model) {
        log.debug("Getting data for group.html");
        if (isShowInactive != null && isShowInactive.equals("on")) {
            model.addAttribute("isShowInactive", true);
        }
        log.debug("Get faculties for selector");
        model.addAttribute("faculties", facultyService.getAllSortedByNameAsc());
        List<Group> groups;
        if (facultyId != null && facultyId > 0) {
            log.debug("get groups by facultyId ({})", facultyId);
            groups = groupService.getAllByFacultyId(facultyId);
        } else {
            log.debug("get all groups");
            groups = groupService.getAll();
        }
        log.debug("adding groups and selected faculty into model");
        model.addAttribute("groups", groups);
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("newGroup", new Group());
        log.info("The list of groups and selected faculty is loaded into the model");
        return "group";
    }

    @PostMapping
    public String createGroup(@ModelAttribute Group group,
                              HttpServletRequest request) {
        log.debug("Creating {}", group);
        groupService.add(group);
        log.info("{} is created", group);
        return defineRedirect(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Group getGroup(@PathVariable("id") int groupId) {
        log.debug("Getting group id({})", groupId);
        Group group = groupService.getById(groupId);
        log.info("Found {}", group);
        return group;
    }

    @PutMapping("/{id}")
    public String updateGroup(@ModelAttribute Group group,
                              @PathVariable("id") int groupId,
                              HttpServletRequest request) {
        log.debug("Updating group id({})", groupId);
        groupService.update(group);
        log.info("Group id({}) is updated", groupId);
        return defineRedirect(request);
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable("id") int groupId,
                              HttpServletRequest request) {
        log.debug("Deleting group id({})", groupId);
        groupService.delete(groupId);
        log.info("Group id({}) is deleted", groupId);
        return defineRedirect(request);
    }
//
//    @GetMapping("/{id}/students")
//    @ResponseBody
//    public List<StudentDto> getStudentsFromGroup(@PathVariable("id") int groupId) {
//        log.debug("Getting students from group id({})", groupId);
//        List<StudentDto> studentDtos = studentDtoMapper.studentsToStudentDtos(
//            studentService.getStudentsByGroup(groupId));
//        log.info("Found {} students", studentDtos.size());
//        return studentDtos;
//    }
//
//    @GetMapping("/{id}/students/free")
//    @ResponseBody
//    public List<StudentDto> getFreeStudentsFromGroup(@PathVariable("id") int groupId,
//                                                     @RequestParam("time_start")
//                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//                                                         LocalDateTime startTime,
//                                                     @RequestParam("time_end")
//                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
//                                                         LocalDateTime endTime) {
//        log.debug("Getting active students from group id({}) free from {} to {}",
//            groupId, startTime, endTime);
//        List<StudentDto> freeStudentsFromGroup = studentDtoMapper.studentsToStudentDtos(
//            studentService.getFreeStudentsFromGroup(groupId, startTime, endTime));
//        log.info("Found {} students", freeStudentsFromGroup.size());
//        return freeStudentsFromGroup;
//    }

}