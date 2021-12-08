package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentDtoMapper {

    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    DepartmentDto toDepartmentDto(Department department);

    @InheritInverseConfiguration(name = "toDepartmentDto")
    Department toDepartment(DepartmentDto departmentDto);

    List<DepartmentDto> toDepartmentDtos(List<Department> departments);
}
