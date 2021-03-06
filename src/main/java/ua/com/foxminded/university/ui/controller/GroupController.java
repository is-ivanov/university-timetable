package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.Util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final FacultyService facultyService;
    private final StudentService studentService;

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
        List<GroupDto> groups;
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
        log.debug("The list of groups and selected faculty is loaded into the model");
        return "group";
    }

    @PostMapping
    public ResponseEntity<String> createGroup(@ModelAttribute @Valid Group group,
                                              HttpServletRequest request) {
        log.debug("Creating {}", group);
        groupService.save(group);
        log.debug("{} is created", group);
        return getResponseEntityWithRedirectUrl(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public GroupDto getGroup(@PathVariable("id") int groupId) {
        log.debug("Getting group id({})", groupId);
        GroupDto group = groupService.getById(groupId);
        log.debug("Found {}", group);
        return group;
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(@ModelAttribute @Valid Group group,
                                              @PathVariable("id") int groupId,
                                              HttpServletRequest request) {
        log.debug("Updating group id({})", groupId);
        groupService.save(group);
        log.debug("Group id({}) is updated", groupId);
        return getResponseEntityWithRedirectUrl(request);
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable("id") int groupId,
                              HttpServletRequest request) {
        log.debug("Deleting group id({})", groupId);
        groupService.delete(groupId);
        log.debug("Group id({}) is deleted", groupId);
        return defineRedirect(request);
    }

    @GetMapping("/{id}/students/free")
    @ResponseBody
    public List<StudentDto> getFreeStudentsFromGroup(@PathVariable("id") int groupId,
                                                     @RequestParam("time_start")
                                                     @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                         LocalDateTime startTime,
                                                     @RequestParam("time_end")
                                                     @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                         LocalDateTime endTime) {
        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, startTime, endTime);
        List<StudentDto> freeStudentsFromGroup =
            studentService.getFreeStudentsFromGroup(groupId, startTime, endTime);
        log.debug("Found {} students", freeStudentsFromGroup.size());
        return freeStudentsFromGroup;
    }

}