package ua.com.foxminded.university.ui.restcontroller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.com.foxminded.university.ui.restcontroller.link.LinkBuilder;
import ua.com.foxminded.university.ui.util.MappingConstants;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(MappingConstants.API)
public class RootRestController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public RepresentationModel<?> root() {
        RepresentationModel<?> model = new RepresentationModel<>();

        model.add(linkTo(RootRestController.class).withSelfRel());
        model.add(LinkBuilder.COURSES_LINK);
        model.add(LinkBuilder.DEPARTMENTS_LINK);
        model.add(LinkBuilder.FACULTIES_LINK);
        model.add(LinkBuilder.GROUPS_LINK);
        model.add(LinkBuilder.LESSONS_LINK);
        model.add(LinkBuilder.ROOMS_LINK);
        model.add(LinkBuilder.STUDENTS_LINK);
        model.add(LinkBuilder.TEACHERS_LINK);

        return model;
    }
}
