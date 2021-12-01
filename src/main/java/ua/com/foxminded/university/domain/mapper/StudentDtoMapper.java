package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Student;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentDtoMapper {

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    StudentDto studentToStudentDto(Student student);

    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "group")
    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "group.name", source = "groupName")
    Student studentDtoToStudent(StudentDto studentDto);

    List<StudentDto> studentsToStudentDtos(List<Student> students);

    List<Student> studentDtosToStudents(List<StudentDto> studentDtos);

}
