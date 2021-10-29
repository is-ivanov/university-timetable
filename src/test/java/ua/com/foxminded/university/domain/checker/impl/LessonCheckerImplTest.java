package ua.com.foxminded.university.domain.checker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.domain.checker.interfaces.RoomChecker;
import ua.com.foxminded.university.domain.checker.interfaces.TeacherChecker;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.ServiceException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.LESSON_ID1;
import static ua.com.foxminded.university.TestObjects.createTestLesson;

@ExtendWith(MockitoExtension.class)
class LessonCheckerImplTest {

    private Lesson checkedLesson;
    private Teacher teacher;
    private Room room;

    @Mock
    private TeacherChecker teacherCheckerMock;

    @Mock
    private RoomChecker roomCheckerMock;

    @InjectMocks
    private LessonCheckerImpl lessonChecker;

    @BeforeEach
    void setUp() {
        checkedLesson = createTestLesson(LESSON_ID1);
        teacher = checkedLesson.getTeacher();
        room = checkedLesson.getRoom();
    }

    @Nested
    @DisplayName("test 'check' method")
    class CheckTest {
        @Test
        @DisplayName("when check the lesson with teacher then should call " +
            "teacherChecker and roomChecker")
        void whenCheckTheLessonWithTeacherThenShouldCallTeacherChecker() {
            lessonChecker.check(checkedLesson);
            verify(teacherCheckerMock, times(1)).check(teacher, checkedLesson);
            verify(roomCheckerMock, times(1)).check(room, checkedLesson);
        }

        @Test
        @DisplayName("when check teacher throw Exception then don't call roomChecker")
        void whenCheckTeacherThrowExceptionThenDonTCallRoomChecker() {
            doThrow(ServiceException.class).when(teacherCheckerMock).check(teacher, checkedLesson);
            assertThrows(ServiceException.class, () -> lessonChecker.check(checkedLesson));
            verify(roomCheckerMock, never()).check(room, checkedLesson);
        }
    }

}