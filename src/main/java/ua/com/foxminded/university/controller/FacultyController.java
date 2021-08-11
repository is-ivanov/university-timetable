package ua.com.foxminded.university.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    public String faculties(Model model) {
        log.debug("Getting all faculties");
        model.addAttribute("faculties", facultyService.getAll());
        log.info("The list of faculties is loaded into the model");
        return "faculty";
    }
}
