package ua.com.foxminded.university.ui.restcontroller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RootRestController {

    @GetMapping("/api")
    public ResponseEntity<RepresentationModel<?>> root() {
        RepresentationModel<?> model = new RepresentationModel<>();

        model.add(linkTo(methodOn(RootRestController.class).root()).withSelfRel());
        model.add(linkTo(FacultyRestController.class).withRel("faculties"));
        model.add(linkTo(RoomRestController.class).withRel("rooms"));

        return ResponseEntity.ok(model);
    }
}
