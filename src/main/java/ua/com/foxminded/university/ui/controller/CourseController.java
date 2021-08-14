package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public String courses(Model model) {
        log.debug("Show courses");
        model.addAttribute("courses", courseService.getAll());
        log.info("The list of courses is loaded into model");
        return "course";
    }
}