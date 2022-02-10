package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;

@Mapper
public interface TeacherDtoMapper extends DtoMapper<Teacher, TeacherDto> {

    @Override
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    TeacherDto toDto(Teacher entity);

    @Override
    @Mapping(target = "department")
    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "department.name", source = "departmentName")
    Teacher toEntity(TeacherDto dto);

}
