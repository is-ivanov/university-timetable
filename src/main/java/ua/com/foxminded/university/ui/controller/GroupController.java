package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

    public static final String REDIRECT_GROUPS = "redirect:";

    private final GroupService groupService;
    private final FacultyService facultyService;

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
                              @RequestParam(required = false) String uri) {
        log.debug("Creating {}", group);
        groupService.add(group);
        log.info("{} is created", group);
        return defineRedirect(uri);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Group showGroup(@PathVariable("id") int groupId) {
        log.debug("Getting group id({})", groupId);
        Group group = groupService.getById(groupId);
        log.info("Found {}", group);
        return group;
    }

    @PutMapping("/{id}")
    public String updateGroup(@ModelAttribute Group group,
                              @PathVariable("id") int groupId,
                              @RequestParam(required = false) String uri) {
        log.debug("Updating group id({})", groupId);
        groupService.update(group);
        log.info("Group id({}) is updated", groupId);
        return defineRedirect(uri);
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable("id") int groupId,
                              @RequestParam(required = false) String uri) {
        log.debug("Deleting group id({})", groupId);
        groupService.delete(groupId);
        log.info("Group id({}) is deleted", groupId);
        return defineRedirect(uri);
    }

    private String defineRedirect(String uri) {
        return REDIRECT_GROUPS + uri;
    }
}