package ua.com.foxminded.university.domain.checker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;

import java.util.List;

import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class RoomCheckerImplTest {

    private Room room;
    private Lesson lesson;

    @Mock
    private LessonDao lessonDaoMock;

    @Mock
    private AvailableLessonChecker availableLessonCheckerMock;

    @InjectMocks
    private RoomCheckerImpl roomChecker;

    @BeforeEach
    void setUp() {
        room = createTestRoom();
        lesson = createTestLesson(LESSON_ID1);
    }

    @Nested
    @DisplayName("test 'check' method")
    class CheckTest {
        @Test
        @DisplayName("call LessonDao and AvailableLessonChecker with expected parameters")
        void callLessonDaoAndAvailableLessonCheckerWithExpectedParameters() {
            List<Lesson> testLessons = createTestLessons();
            int roomId = room.getId();

            when(lessonDaoMock.getAllForRoom(roomId)).thenReturn(testLessons);

            roomChecker.check(room, lesson);

            verify(lessonDaoMock, times(1)).getAllForRoom(roomId);
            verify(availableLessonCheckerMock, times(1))
                .checkAvailableLesson(lesson, testLessons);
        }
    }
}