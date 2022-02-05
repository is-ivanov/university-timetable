package ua.com.foxminded.university.ui.restcontroller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.RoomDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.mapper.DtoMapper;
import ua.com.foxminded.university.domain.mapper.RoomDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.domain.service.interfaces.RoomService;
import ua.com.foxminded.university.domain.service.interfaces.Service;
import ua.com.foxminded.university.ui.restcontroller.link.LessonDtoAssembler;
import ua.com.foxminded.university.ui.restcontroller.link.RoomDtoAssembler;
import ua.com.foxminded.university.ui.util.MappingConstants;
import ua.com.foxminded.university.ui.util.QueryConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static ua.com.foxminded.university.ui.util.ResponseUtil.DATE_TIME_PATTERN;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(MappingConstants.API_ROOMS)
public class RoomRestController extends AbstractController<RoomDto, Room> {

    private final RoomService roomService;
    private final RoomDtoMapper mapper;
    private final RoomDtoAssembler assembler;
    private final PagedResourcesAssembler<Room> pagedAssembler;
    private final LessonService lessonService;
    private final LessonDtoAssembler lessonAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<RoomDto> getRooms() {
        log.debug("Getting all rooms");
        return getAllInternal();
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<RoomDto> getRoomsPaginatedAndSorted(Pageable pageable) {
        log.debug("Getting all rooms with {}", pageable);
        return getAllSortedAndPaginatedInternal(pageable);
    }

    @GetMapping(params = {QueryConstants.PAGE, QueryConstants.SIZE})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<RoomDto> getRoomsPaginated(@PageableDefault(sort = "number")
                                                     Pageable pageable) {
        return getRoomsPaginatedAndSorted(pageable);
    }

    @GetMapping(params = {QueryConstants.SORT})
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<RoomDto> getRoomsSorted(Pageable pageable) {
        return getRoomsPaginatedAndSorted(pageable);
    }

    @GetMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public RoomDto getRoom(@PathVariable("id") int roomId) {
        log.debug("Getting room by id({})", roomId);
        return getByIdInternal(roomId);
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto roomDto,
                                              HttpServletRequest request) {
        log.debug("Creating {}", roomDto);
        return createInternal(roomDto, request);
    }

    @PutMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.OK)
    public RoomDto updateRoom(@Valid @RequestBody RoomDto roomDto,
                              @PathVariable("id") int roomId,
                              HttpServletRequest request) {
        RoomDto updatedRoomDto = updateInternal(roomId, roomDto, request);
        log.debug("Room id({}) is updated", roomId);
        return updatedRoomDto;
    }

    @DeleteMapping(MappingConstants.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable("id") int roomId) {
        log.debug("Deleting room with id({})", roomId);
        deleteInternal(roomId);
    }

    @GetMapping(MappingConstants.FREE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<RoomDto> getFreeRooms(@RequestParam("time_start")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime from,
                                                 @RequestParam("time_end")
                                                 @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                     LocalDateTime to) {

        log.debug("Getting rooms free from {} to {}", from, to);
        List<Room> freeRooms = roomService.getFreeRoomsOnLessonTime(from, to);

        return assembler.toCollectionModel(freeRooms);
    }

    @GetMapping(MappingConstants.ID_TIMETABLE)
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<LessonDto> getLessonsForRoom(@PathVariable("id") int roomId,
                                                        @RequestParam("start")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                            ZonedDateTime from,
                                                        @RequestParam("end")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                            ZonedDateTime to) {
        log.debug("Getting lessons for room id({}) from {} to {}", roomId,
            from, to);
        List<Lesson> lessonsForTeacher = lessonService
            .getAllForRoomForTimePeriod(roomId,
                from.toLocalDateTime(), to.toLocalDateTime());
        return lessonAssembler.toCollectionModel(lessonsForTeacher);
    }


    @Override
    protected Service<Room> getService() {
        return roomService;
    }

    @Override
    protected RepresentationModelAssembler<Room, RoomDto> getAssembler() {
        return assembler;
    }

    @Override
    protected PagedResourcesAssembler<Room> getPagedAssembler() {
        return pagedAssembler;
    }

    @Override
    protected DtoMapper<Room, RoomDto> getMapper() {
        return mapper;
    }
}
