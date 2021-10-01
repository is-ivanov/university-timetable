package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.checker.interfaces.LessonChecker;
import ua.com.foxminded.university.domain.checker.interfaces.StudentChecker;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.exception.ServiceException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher %s is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room %s is not available";
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student id(1) is not available for the lesson id(1)";
    private static final String MESSAGE_FILTER_NOT_SELECT = "Select at least one filter";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final LocalDateTime TIME_START_CHECKED_LESSON = LocalDateTime.of(2021, Month.JANUARY,
        3, 12, 0);
    private static final LocalDateTime TIME_START_BUSY_LESSON = LocalDateTime.of(2021,
        Month.JANUARY, 3, 11, 0);
    private static final int LESSON_DURATION = 90;

    @Mock
    private LessonDao lessonDaoMock;

    @Mock
    private StudentChecker studentChecker;

    @Mock
    private LessonChecker lessonChecker;

    @InjectMocks
    private LessonServiceImpl lessonService;

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
    void testDelete_CallDaoInOrder() {
        Lesson testLesson = createTestLesson();
        InOrder inOrder = inOrder(lessonDaoMock);
        lessonService.delete(testLesson);
        inOrder.verify(lessonDaoMock).deleteAllStudentsFromLesson(testLesson.getId());
        inOrder.verify(lessonDaoMock).delete(testLesson);
    }

    private Lesson createTestLesson() {
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

    private Lesson createLessonWithBusyTime() {
        LocalDateTime endSecondLessonThisTeacher =
            TIME_START_BUSY_LESSON.plusMinutes(LESSON_DURATION);
        return Lesson.builder()
            .timeStart(TIME_START_BUSY_LESSON)
            .timeEnd(endSecondLessonThisTeacher)
            .build();
    }

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("when check is passed then should call lessonDao.add once")
        void testAddCheckPassed_CallDaoOnce() {
            Lesson testLesson = createTestLesson();
            doNothing().when(lessonChecker).check(testLesson);
            lessonService.add(testLesson);
            verify(lessonDaoMock).add(testLesson);
        }

        @Test
        @DisplayName("when check is false then should throw ServiceException with message")
        void testAddCheckFalse_ThrowException() {
            Lesson testLesson = createTestLesson();
            doThrow(new ServiceException(MESSAGE_TEACHER_NOT_AVAILABLE))
                .when(lessonChecker).check(testLesson);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.add(testLesson));
            assertThat(e.getMessage(), is(equalTo(MESSAGE_TEACHER_NOT_AVAILABLE)));
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("when check is passed should call lessonDao.update once")
        void testUpdateCheckPassed_CallDaoOnce() throws ServiceException {
            Lesson testLesson = createTestLesson();
            doNothing().when(lessonChecker).check(testLesson);
            lessonService.update(testLesson);
            verify(lessonDaoMock).update(testLesson);
        }

        @Test
        @DisplayName("when check is false then should throw ServiceException with message")
        void testUpdateCheckFalse_ThrowException() {
            Lesson testLesson = createTestLesson();
            doThrow(new ServiceException(MESSAGE_TEACHER_NOT_AVAILABLE))
                .when(lessonChecker).check(testLesson);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            assertThat(e.getMessage(), is(equalTo(MESSAGE_TEACHER_NOT_AVAILABLE)));
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Lesson then method should" +
            " return this Lesson")
        void testReturnExpectedLesson() {
            Lesson expectedLesson = createTestLesson();
            when(lessonDaoMock.getById(anyInt())).thenReturn(Optional.of(expectedLesson));
            assertEquals(expectedLesson, lessonService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should throw " +
            "EntityNotFoundException")
        void testReturnEmptyLesson() {
            Optional<Lesson> optional = Optional.empty();
            when(lessonDaoMock.getById(anyInt())).thenReturn(optional);
            assertThrows(EntityNotFoundException.class, () -> lessonService.getById(1));
        }
    }

    @Nested
    @DisplayName("test 'addStudentToLesson' method")
    class addStudentToLessonTest {

        @Test
        @DisplayName("when the check is passed then method call studentChecker once")
        void testChecksTrue_CallDaoOnce() throws ServiceException {
            Lesson testLesson = createTestLesson();
            Student student = Student.builder()
                .id(ID1)
                .active(true)
                .build();
            doNothing().when(studentChecker).check(student, testLesson);
            lessonService.addStudentToLesson(testLesson, student);
            verify(lessonDaoMock).addStudentToLesson(testLesson.getId(),
                student.getId());
        }

        @Test
        @DisplayName("when the check is false then method throw ServiceException")
        void testCheckFalse_ThrowServiceException() {
            Lesson lessonForAddingStudent = createTestLesson();

            Student studentAdding = Student.builder()
                .id(ID1)
                .active(true)
                .build();

            doThrow(new ServiceException(MESSAGE_STUDENT_NOT_AVAILABLE))
                .when(studentChecker).check(studentAdding, lessonForAddingStudent);
            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.addStudentToLesson(lessonForAddingStudent, studentAdding));

            assertThat(e.getMessage(), is(equalTo(MESSAGE_STUDENT_NOT_AVAILABLE)));
        }
    }

    @Nested
    @DisplayName("test 'getAllWithFilter' method")
    class TestGetAllWithFilter {
        @Test
        @DisplayName("When one condition not null then call lessonDao once")
        void whenOneConditionNotNullThenCallLessonDaoOnce() {
            LessonFilter lessonFilter = new LessonFilter();
            lessonFilter.setFacultyId(ID1);

            lessonService.getAllWithFilter(lessonFilter);
            verify(lessonDaoMock, times(1))
                .getAllWithFilter(lessonFilter);
        }

        @Test
        @DisplayName("When all conditions is null then should throw ServiceException")
        void whenAllConditionsIsNullThenShouldThrowServiceException() {
            LessonFilter lessonFilter = new LessonFilter();

            ServiceException exception = assertThrows(ServiceException.class,
                () -> lessonService.getAllWithFilter(lessonFilter));
            assertThat(exception.getMessage(), is(equalTo(MESSAGE_FILTER_NOT_SELECT)));
        }
    }
}