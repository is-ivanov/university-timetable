package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.ui.restcontroller.link.GroupModelAssembler;
import ua.com.foxminded.university.ui.util.Mappings;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Mappings.API_GROUPS)
@Validated
public class GroupRestController {

    private final GroupService groupService;
    private final GroupModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GroupDto>>> getGroups() {
        log.debug("Getting all groups");
        List<GroupDto> groups = groupService.getAll();
        CollectionModel<EntityModel<GroupDto>> entityModels =
            assembler.toCollectionModel(groups);
        entityModels.add(linkTo(GroupRestController.class).withSelfRel());
        return ResponseEntity.ok(entityModels);
    }


}
