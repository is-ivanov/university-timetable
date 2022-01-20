package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;

@Mapper
public interface FacultyDtoMapper extends DtoMapper<Faculty, FacultyDto> {
}
