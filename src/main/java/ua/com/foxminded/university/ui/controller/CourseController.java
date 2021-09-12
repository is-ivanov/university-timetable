package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;

import static ua.com.foxminded.university.ui.Util.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/courses")
public class CourseController {

    public static final String URI_COURSES = "/courses";

    private final CourseService courseService;

    @GetMapping
    public String showCourses(Model model) {
        log.debug("Show courses");
        model.addAttribute("courses", courseService.getAll());
        model.addAttribute("newCourse", new Course());
        log.info("The list of courses is loaded into model");
        return "course";
    }

    @PostMapping
    public String createCourse(@ModelAttribute Course course) {
        log.debug("Creating {}", course);
        courseService.add(course);
        log.info("{} is created", course);
        return defineRedirect(URI_COURSES);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Course getCourse(@PathVariable("id") int courseId) {
        log.debug("Getting course id({}})", courseId);
        Course course = courseService.getById(courseId);
        log.info("Found {}", course);
        return course;
    }

    @PutMapping("/{id}")
    public String updateCourse(@ModelAttribute Course course,
                               @PathVariable("id") int courseId) {
        log.debug("Updating course id({})", courseId);
        courseService.update(course);
        log.info("Course id({}) is updated", courseId);
        return defineRedirect(URI_COURSES);
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") int courseId) {
        log.debug("Deleting course id({})", courseId);
        courseService.delete(courseId);
        log.info("Course id({}) is deleted", courseId);
        return defineRedirect(URI_COURSES);
    }
}