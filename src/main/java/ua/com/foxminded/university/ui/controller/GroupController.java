package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

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
        log.info("The list of groups and selected faculty is loaded into the model");
        return "group";
    }
}