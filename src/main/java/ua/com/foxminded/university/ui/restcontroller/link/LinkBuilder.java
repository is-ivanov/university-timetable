package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.CourseRestController;
import ua.com.foxminded.university.ui.restcontroller.FacultyRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public final class LinkBuilder {

    public static final WebMvcLinkBuilder COURSE_LINK_BUILDER = linkTo(CourseRestController.class);
    public static final Link COURSES_LINK = COURSE_LINK_BUILDER.withRel("courses");
    public static final Link COURSES_SELF_LINK = COURSE_LINK_BUILDER.withSelfRel();
    public static final WebMvcLinkBuilder FACULTY_LINK_BUILDER = linkTo(FacultyRestController.class);
    public static final Link FACULTIES_LINK = FACULTY_LINK_BUILDER.withRel("faculties");
    public static final Link FACULTIES_SELF_LINK = FACULTY_LINK_BUILDER.withSelfRel();

    private LinkBuilder() {
    }
}
