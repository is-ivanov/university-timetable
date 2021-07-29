package ua.com.foxminded.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

import java.util.Comparator;
import java.util.List;

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
    public String groups(@RequestParam(value = "facultyId", required = false) Integer facultyId,
                         @RequestParam(value = "isShowInactive", required =
                             false) String isShowInactive,
                         Model model) {
        if (isShowInactive != null && isShowInactive.equals("on")) {
            model.addAttribute("isShowInactive", true);
        }
        List<Faculty> allFaculties = facultyService.getAll();
        allFaculties.sort(Comparator.comparing(Faculty::getName));
        model.addAttribute("faculties", allFaculties);
        Faculty facultySelected = null;
        if (facultyId != null) {
            List<Group> groups = groupService.getAllByFacultyId(facultyId);
            model.addAttribute("groups", groups);
            facultySelected = allFaculties.stream()
                .filter(faculty -> faculty.getId() == facultyId)
                .findFirst().orElse(null);
        }
        model.addAttribute("facultySelected", facultySelected);
        return "group";
    }
}