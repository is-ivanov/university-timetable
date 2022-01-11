package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacultyDtoMapper {

    FacultyDto toFacultyDto(Faculty faculty);

    Faculty toFaculty(FacultyDto facultyDto);

    List<FacultyDto> toFacultyDtos(List<Faculty> faculties);
}
