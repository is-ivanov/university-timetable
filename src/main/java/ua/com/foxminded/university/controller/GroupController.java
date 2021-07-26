package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

@Controller
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;
    private final FacultyService facultyService;

    @Autowired
    public GroupController(GroupService groupService, FacultyService facultyService) {
        this.groupService = groupService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String groups(Model model) {
        model.addAttribute("groups", groupService.getAll());
        model.addAttribute("faculties",facultyService.getAll());
        return "group";
    }
}
