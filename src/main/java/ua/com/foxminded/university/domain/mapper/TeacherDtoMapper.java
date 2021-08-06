package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.util.List;

@Mapper (componentModel = "spring")
public interface TeacherDtoMapper {

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "isActive", source = "active")
    TeacherDto teacherToTeacherDto(Teacher teacher);

    List<TeacherDto> teachersToTeacherDtos(List<Teacher> teachers);

}
