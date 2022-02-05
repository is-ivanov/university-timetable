package ua.com.foxminded.university.ui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.service.interfaces.CourseService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import javax.servlet.http.HttpServletRequest;

import static ua.com.foxminded.university.ui.util.ResponseUtil.defineRedirect;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/courses")
public class CourseController {

    public static final String URI_COURSES = "/courses";

    private final CourseService courseService;
    private final PageSequenceCreator pageSequenceCreator;

    @GetMapping
    public String showCourses(Model model,
                              @PageableDefault(sort = "name") Pageable pageable) {
        log.debug("Getting data for course.html");
        Page<Course> pageCourses = courseService.findAll(pageable);
        model.addAttribute("courses", pageCourses.getContent());
        model.addAttribute("page", pageCourses);
        model.addAttribute("uri", URI_COURSES);
        model.addAttribute("newCourse", new Course());
        model.addAttribute("pages", pageSequenceCreator
            .createPageSequence(pageCourses.getTotalPages(),
                pageCourses.getNumber() + 1));
        log.debug("The list of courses is loaded into model");
        return "course";
    }

    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable("id") int courseId,
                               HttpServletRequest request) {
        log.debug("Deleting course id({})", courseId);
        courseService.delete(courseId);
        log.debug("Course id({}) is deleted", courseId);
        return defineRedirect(request);
    }

}