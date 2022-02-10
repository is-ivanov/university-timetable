package ua.com.foxminded.university.ui.restcontroller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;
import ua.com.foxminded.university.ui.restcontroller.DepartmentRestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SuppressWarnings("NullableProblems")
@Component
@RequiredArgsConstructor
public class DepartmentDtoAssembler implements RepresentationModelAssembler<Department, DepartmentDto> {

    public static final String LINK_TEACHERS = "teachers from this department";

    private final DepartmentDtoMapper mapper;

    @Override
    public DepartmentDto toModel(Department department) {

        DepartmentDto departmentDto = mapper.toDto(department);

        Class<DepartmentRestController> classController = DepartmentRestController.class;
        departmentDto.add(
            linkTo(methodOn(classController).getDepartment(department.getId()))
                .withSelfRel(),
            linkTo(methodOn(classController).getTeachersByDepartment(department.getId()))
                .withRel(LINK_TEACHERS),
            LinkBuilder.DEPARTMENTS_LINK,
            LinkBuilder.ROOT_LINK
        );
        return departmentDto;
    }

    @Override
    public CollectionModel<DepartmentDto> toCollectionModel(Iterable<? extends Department> entities) {

        CollectionModel<DepartmentDto> modelDepartments =
            RepresentationModelAssembler.super.toCollectionModel(entities);
        modelDepartments.add(
            LinkBuilder.DEPARTMENTS_LINK,
            LinkBuilder.ROOT_LINK
        );

        return modelDepartments;
    }
}
