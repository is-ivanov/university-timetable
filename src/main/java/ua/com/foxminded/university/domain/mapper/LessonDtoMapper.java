package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.util.List;

@Mapper(uses = StudentDtoMapper.class)
public interface LessonDtoMapper extends DtoMapper<Lesson, LessonDto> {

    @Override
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFullName", expression = "java(entity.getTeacher().getFullName())")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "buildingAndRoom",
        expression = "java(entity.getRoom().getBuildingAndRoom())")
    LessonDto toDto(Lesson entity);

    @Override
    @InheritInverseConfiguration(name = "toDto")
    Lesson toEntity(LessonDto dto);

    @Override
    List<LessonDto> toDtos(Iterable<Lesson> entities);

}
