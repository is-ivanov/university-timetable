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
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.ui.restcontroller.link.GroupDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(MappingConstants.API_GROUPS)
@Validated
public class GroupRestController {

    private final GroupService groupService;
    private final GroupDtoAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Group>>> getGroups() {
        log.debug("Getting all groups");
        List<Group> groups = groupService.findAll();
//        CollectionModel<EntityModel<Group>> entityModels =
//            assembler.toCollectionModel(groups);
//        entityModels.add(linkTo(GroupRestController.class).withSelfRel());
//        return ResponseEntity.ok(entityModels);
        return ResponseEntity.ok(null);
    }


}
