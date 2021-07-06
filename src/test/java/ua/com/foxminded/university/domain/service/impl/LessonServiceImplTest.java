package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.ServiceException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";

    private LessonServiceImpl lessonService;

    @Mock
    private LessonDao lessonDaoMock;

    @BeforeEach
    void setUp() {
        lessonService = new LessonServiceImpl(lessonDaoMock);
    }

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("when checks return true should call lessonDao.add " +
            "once")
        void testAdd_CallDaoOnce() throws Exception {
            Lesson testLesson = createTestLesson();
            when(lessonDaoMock.getAllByTeacher(1))
                .thenReturn(new ArrayList<>());
            lessonService.add(testLesson);
            verify(lessonDaoMock).add(testLesson);
        }

        @Test
        @DisplayName("if teacher's time is busy then should throw " +
            "ServiceException with message")
        void testTeacherBusy_ThrowException() {
            Lesson testLesson = createTestLesson();
            LocalDateTime startSecondLessonThisTeacher = LocalDateTime.of(2021,
                Month.JANUARY, 3, 11, 0);
            LocalDateTime endSecondLessonThisTeacher =
                startSecondLessonThisTeacher.plusMinutes(90);
            Lesson secondLessonThisTeacher = Lesson.builder()
                .timeStart(startSecondLessonThisTeacher)
                .timeEnd(endSecondLessonThisTeacher)
                .build();
            List<Lesson> lessonsThisTeacher = new ArrayList<>();
            lessonsThisTeacher.add(secondLessonThisTeacher);
            when(lessonDaoMock.getAllByTeacher(1))
                .thenReturn(lessonsThisTeacher);
            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.add(testLesson));
            String expectedMessage =
                String.format(MESSAGE_TEACHER_NOT_AVAILABLE, testLesson.getTeacher());
            assertEquals(expectedMessage, e.getMessage());
        }

        @Test
        @DisplayName("if room's time is busy then should throw " +
            "ServiceException with message")
        void testRoomBusy_ThrowException() {
            Lesson testLesson = createTestLesson();
            LocalDateTime startSecondLesson = LocalDateTime.of(2021,
                Month.JANUARY, 3, 11, 0);
            LocalDateTime endSecondLesson =
                startSecondLesson.plusMinutes(90);
            Lesson anotherLessonThisRoom = Lesson.builder()
                .timeStart(startSecondLesson)
                .timeEnd(endSecondLesson)
                .build();
            List<Lesson> lessonsThisRoom = new ArrayList<>();
            lessonsThisRoom.add(anotherLessonThisRoom);

            when(lessonDaoMock.getAllByRoom(2)).thenReturn(lessonsThisRoom);
            Exception e = assertThrows(ServiceException.class,
                () -> lessonService.add(testLesson));
            String expectedMessage =
                String.format(MESSAGE_ROOM_NOT_AVAILABLE, testLesson.getRoom());
            assertEquals(expectedMessage, e.getMessage());
        }

    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

    }

    private Lesson createTestLesson(){
        Teacher teacher = new Teacher();
        teacher.setId(1);
        Course course = new Course();
        course.setId(1);
        Room room = new Room();
        room.setId(2);
        LocalDateTime startLesson = LocalDateTime.of(2021, Month.JANUARY,
            3, 12, 0);
        LocalDateTime endLesson =
            startLesson.plusMinutes(90);
        return Lesson.builder()
            .id(1)
            .teacher(teacher)
            .course(course)
            .room(room)
            .timeStart(startLesson)
            .timeEnd(endLesson)
            .build();
    }
}