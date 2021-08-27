package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonDtoMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFullName", source = "teacher.fullName")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "buildingAndRoom",
        expression = "java(String.format(\"%s - %s\", lesson.getRoom().getBuilding(), lesson.getRoom().getNumber()))")
    LessonDto lessonToLessonDto(Lesson lesson);

    List<LessonDto> lessonsToLessonDtos(List<Lesson> lessons);


    Lesson lessonDtoToLesson(LessonDto lessonDto);

}
