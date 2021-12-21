package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.util.List;

@Mapper(componentModel = "spring", uses = StudentDtoMapper.class)
public interface LessonDtoMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFullName", source = "teacher.fullName")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "buildingAndRoom",
        expression = "java(lesson.getRoom().getBuildingAndRoom())")
    LessonDto toLessonDto(Lesson lesson);

    @InheritInverseConfiguration(name = "toLessonDto")
    Lesson toLesson(LessonDto lessonDto);

    List<LessonDto> toLessonDtos(List<Lesson> lessons);

    List<LessonDto> toLessonDtos(Iterable<Lesson> lessons);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLessonFromDto(LessonDto lessonDto, @MappingTarget Lesson lesson);

}
