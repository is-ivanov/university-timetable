package ua.com.foxminded.university.ui.restcontroller.link;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import ua.com.foxminded.university.ui.restcontroller.FacultyRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public final class LinkBuilder {

    public static final WebMvcLinkBuilder FACULTY_LINK_BUILDER = linkTo(FacultyRestController.class);
    public static final Link FACULTIES_LINK = FACULTY_LINK_BUILDER.withRel("faculties");
    public static final Link FACULTIES_SELF_LINK = FACULTY_LINK_BUILDER.withSelfRel();

    private LinkBuilder() {
    }
}
