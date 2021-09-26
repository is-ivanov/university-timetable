package ua.com.foxminded.university.domain.checker.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.checker.interfaces.RoomChecker;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class RoomCheckerImpl implements RoomChecker {

    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room id(%d) is not available";

    private final LessonDao lessonDao;
    private final AvailableLessonChecker availableLessonChecker;

    @Override
    public void check(Room room, Lesson lesson) {
        log.debug("Checking the room id({}) for lesson id({})", room.getId(),
            lesson.getId());
        List<Lesson> lessonsFromThisRoom = lessonDao.getAllForRoom(room.getId());

        try {
            availableLessonChecker.checkAvailableLesson(lesson, lessonsFromThisRoom);
        } catch (ServiceException e) {
            log.warn("Room id({}) is not available for lesson id({})",
                room.getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_ROOM_NOT_AVAILABLE,
                room.getId()), e);
        }
    }
}
