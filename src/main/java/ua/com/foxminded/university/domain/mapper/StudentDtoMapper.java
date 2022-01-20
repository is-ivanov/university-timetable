package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Student;

import java.util.List;

@Mapper
public interface StudentDtoMapper extends DtoMapper<Student, StudentDto> {

    @Override
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    StudentDto toDto(Student entity);

    @Override
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "group")
    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "group.name", source = "groupName")
    Student toEntity(StudentDto dto);

    @Override
    List<StudentDto> toDtos(Iterable<Student> entities);

    @Override
    List<Student> toEntities(Iterable<StudentDto> dtos);

}
