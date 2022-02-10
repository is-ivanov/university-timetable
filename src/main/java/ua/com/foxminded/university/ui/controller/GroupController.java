package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.ui.restcontroller.link.GroupDtoAssembler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final FacultyService facultyService;
    private final GroupDtoAssembler assembler;

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
            groups = groupService.findAll();
        }

        log.debug("adding groups and selected faculty into model");
        model.addAttribute("groups", assembler.toCollectionModel(groups));
        model.addAttribute("facultyIdSelect", facultyId);
        model.addAttribute("newGroup", new Group());
        log.debug("The list of groups and selected faculty is loaded into the model");
        return "group";
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable("id") int groupId,
                              HttpServletRequest request) {
        log.debug("Deleting group id({})", groupId);
        groupService.delete(groupId);
        log.debug("Group id({}) is deleted", groupId);
        return defineRedirect(request);
    }

}