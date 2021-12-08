package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherDtoMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    TeacherDto toTeacherDto(Teacher teacher);


    @Mapping(target = "department")
    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "department.name", source = "departmentName")
    Teacher toTeacher(TeacherDto teacherDto);

    List<TeacherDto> toTeacherDtos(List<Teacher> teachers);

}
