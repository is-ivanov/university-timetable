package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;

@Mapper
public interface DepartmentDtoMapper extends DtoMapper<Department, DepartmentDto> {

    @Override
    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    DepartmentDto toDto(Department entity);

    @Override
    @InheritInverseConfiguration(name = "toDto")
    Department toEntity(DepartmentDto dto);

}
