package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";
    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final LocalDateTime TIME_START_CHECKED_LESSON = LocalDateTime.of(2021, Month.JANUARY,
        3, 12, 0);
    public static final LocalDateTime TIME_START_BUSY_LESSON = LocalDateTime.of(2021,
        Month.JANUARY, 3, 11, 0);
    public static final int LESSON_DURATION = 90;

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
            lessonService.add(testLesson);
            verify(lessonDaoMock).add(testLesson);
        }

        @Test
        @DisplayName("if teacher's time is busy then should throw " +
            "ServiceException with message")
        void testAdd_TeacherBusy_ThrowException() {
            Lesson testLesson = createTestLesson();
            Lesson lessonWithTeacherBusyTime = createLessonWithBusyTime();
            List<Lesson> lessonsThisTeacher = new ArrayList<>();
            lessonsThisTeacher.add(lessonWithTeacherBusyTime);
            when(lessonDaoMock.getAllByTeacher(ID1))
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
        void testAdd_RoomBusy_ThrowException() {
            Lesson testLesson = createTestLesson();
            Lesson lessonWithRoomBusyTime = createLessonWithBusyTime();
            List<Lesson> lessonsThisRoom = new ArrayList<>();
            lessonsThisRoom.add(lessonWithRoomBusyTime);

            when(lessonDaoMock.getAllByRoom(ID2)).thenReturn(lessonsThisRoom);
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

        @Test
        @DisplayName("when checks return true should call lessonDao.update " +
            "once")
        void testUpdate_CallDaoOnce() throws ServiceException {
            Lesson testLesson = createTestLesson();
            lessonService.update(testLesson);
            verify(lessonDaoMock).update(testLesson);
        }

        @Test
        @DisplayName("if teacher's time is busy then should throw " +
            "ServiceException with message")
        void testUpdate_TeacherBusy_ThrowException(){
            Lesson testLesson = createTestLesson();
            Lesson lessonWithTeacherBusyTime = createLessonWithBusyTime();
            List<Lesson> lessonsThisTeacher = new ArrayList<>();
            lessonsThisTeacher.add(lessonWithTeacherBusyTime);
            when(lessonDaoMock.getAllByTeacher(ID1))
                .thenReturn(lessonsThisTeacher);
            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            String expectedMessage =
                String.format(MESSAGE_TEACHER_NOT_AVAILABLE, testLesson.getTeacher());
            assertEquals(expectedMessage, e.getMessage());
        }

        @Test
        @DisplayName("if room's time is busy then should throw " +
            "ServiceException with message")
        void testUpdate_RoomBusy_ThrowException() {
            Lesson testLesson = createTestLesson();
            Lesson lessonWithRoomBusyTime = createLessonWithBusyTime();
            List<Lesson> lessonsThisRoom = new ArrayList<>();
            lessonsThisRoom.add(lessonWithRoomBusyTime);

            when(lessonDaoMock.getAllByRoom(ID2)).thenReturn(lessonsThisRoom);
            Exception e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            String expectedMessage =
                String.format(MESSAGE_ROOM_NOT_AVAILABLE, testLesson.getRoom());
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Lesson then method should" +
            " return this Lesson")
        void testReturnExpectedLesson() throws Exception {
            Lesson expectedLesson = createTestLesson();
            when(lessonDaoMock.getById(anyInt())).thenReturn(Optional.of(expectedLesson));
            assertEquals(expectedLesson, lessonService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Lesson")
        void testReturnEmptyLesson() throws Exception {
            Optional<Lesson> optional = Optional.empty();
            when(lessonDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Lesson(), lessonService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao throw DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception{
            when(lessonDaoMock.getById(anyInt())).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> lessonService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List lessons then method " +
        "should return this List")
    void testGetAll_ReturnListLessons() {
        Lesson firstLesson = createTestLesson();
        Lesson secondLesson = createLessonWithBusyTime();
        List<Lesson> expectedLessons = new ArrayList<>();
        expectedLessons.add(firstLesson);
        expectedLessons.add(secondLesson);
        when(lessonDaoMock.getAll()).thenReturn(expectedLessons);
        assertEquals(expectedLessons, lessonService.getAll());
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "lessonDao in Order")
    void testDelete_CallDaoInOrder () {
        Lesson testLesson = createTestLesson();
        InOrder inOrder = inOrder(lessonDaoMock);
        lessonService.delete(testLesson);
        inOrder.verify(lessonDaoMock).deleteAllStudentsFromLesson(testLesson.getId());
        inOrder.verify(lessonDaoMock).delete(testLesson);
    }

    @Test
    @DisplayName("test 'addStudentToLesson' ")

    private Lesson createTestLesson(){
        Teacher teacher = new Teacher();
        teacher.setId(ID1);
        Course course = new Course();
        course.setId(ID1);
        Room room = new Room();
        room.setId(ID2);
        LocalDateTime timeEndCheckedLesson =
            TIME_START_CHECKED_LESSON.plusMinutes(90);
        return Lesson.builder()
            .id(ID1)
            .teacher(teacher)
            .course(course)
            .room(room)
            .timeStart(TIME_START_CHECKED_LESSON)
            .timeEnd(timeEndCheckedLesson)
            .build();
    }
    private Lesson createLessonWithBusyTime(){
        LocalDateTime endSecondLessonThisTeacher =
            TIME_START_BUSY_LESSON.plusMinutes(LESSON_DURATION);
        return Lesson.builder()
            .timeStart(TIME_START_BUSY_LESSON)
            .timeEnd(endSecondLessonThisTeacher)
            .build();
    }
}