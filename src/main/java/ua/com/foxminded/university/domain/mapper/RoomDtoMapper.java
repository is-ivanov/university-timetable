package ua.com.foxminded.university.domain.mapper;

import org.mapstruct.Mapper;
import ua.com.foxminded.university.domain.dto.RoomDto;
import ua.com.foxminded.university.domain.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomDtoMapper extends DtoMapper<Room, RoomDto> {
}
