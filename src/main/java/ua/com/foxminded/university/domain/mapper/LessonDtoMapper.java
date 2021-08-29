package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonDtoMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherFullName", source = "teacher.fullName")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "buildingAndRoom",
        expression = "java(roomToBuildingAndRoom(lesson.getRoom()))")
    LessonDto lessonToLessonDto(Lesson lesson);

    @InheritInverseConfiguration(name = "lessonToLessonDto")
    @Mapping(target = "students", ignore = true)
    Lesson lessonDtoToLesson(LessonDto lessonDto);

    List<LessonDto> lessonsToLessonDtos(List<Lesson> lessons);

    default String roomToBuildingAndRoom(Room room) {
        return String.format("%s - %s", room.getBuilding(), room.getNumber());
    }
}
