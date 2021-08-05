package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Teacher;

@Mapper
public interface TeacherDtoMapper {

    TeacherDtoMapper INSTANCE = Mappers.getMapper(TeacherDtoMapper.class);
    String STATUS_ACTIVE = "active";
    String STATUS_INACTIVE = "inactive";

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "status", expression = "java(activeToStatus(teacher))")
    TeacherDto teacherToTeacherDto(Teacher teacher);

    default String activeToStatus(Teacher teacher) {
        if (teacher.isActive()) {
            return STATUS_ACTIVE;
        }
        return STATUS_INACTIVE;
    }
}
