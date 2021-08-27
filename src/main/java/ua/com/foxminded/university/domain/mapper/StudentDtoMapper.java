package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentDtoMapper {

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    @Mapping(target = "isActive", source = "active")
    StudentDto studentToStudentDto(Student student);
}
