package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import ua.com.foxminded.university.domain.dto.CourseDto;
import ua.com.foxminded.university.domain.entity.Course;

@Mapper
public interface CourseDtoMapper extends DtoMapper<Course, CourseDto> {
}
