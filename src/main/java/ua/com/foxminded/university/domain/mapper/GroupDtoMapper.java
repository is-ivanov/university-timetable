package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Group;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupDtoMapper {

    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    GroupDto toGroupDto(Group group);

    @InheritInverseConfiguration(name = "toGroupDto")
    Group toGroup(GroupDto groupDto);

    List<GroupDto> toGroupDtos(List<Group> groups);
}
