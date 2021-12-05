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
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.domain.mapper.LessonDtoMapper;
import ua.com.foxminded.university.exception.ServiceException;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher id(7) is not available";
    private static final String MESSAGE_ROOM_NOT_AVAILABLE = "Room id(5) is not available";
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student id(78) is not available";
    private static final String MESSAGE_FILTER_NOT_SELECT = "Select at least one filter";
    public static final String MESSAGE_STUDENT_IS_INACTIVE = "Student id(78) is inactive";

    @Mock
    private LessonDao lessonDaoMock;

    @Mock
    private StudentDao studentDaoMock;

    @Mock
    private LessonDtoMapper mapperMock;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    @DisplayName("test 'getAll' when Dao return List lessons then method " +
        "should return this List")
    void testGetAll_ReturnListLessons() {
        List<Lesson> lessons = createTestLessons();
        List<LessonDto> lessonDtos = createTestLessonDtos();

        when(lessonDaoMock.getAll()).thenReturn(lessons);
        when(mapperMock.toLessonDtos(lessons)).thenReturn(lessonDtos);

        assertThat(lessonService.getAll()).isEqualTo(lessonDtos);
    }


    @Nested
    @DisplayName("test 'delete lesson' method")
    class DeleteLessonTest {
        @Test
        @DisplayName("when call delete method then should call " +
            "lessonDao in Order")
        void whenDeleteLesson_CallDaoInOrder() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            InOrder inOrder = inOrder(lessonDaoMock);

            lessonService.delete(testLesson);

            inOrder.verify(lessonDaoMock).deleteAllStudentsFromLesson(testLesson.getId());
            inOrder.verify(lessonDaoMock).delete(testLesson);
        }
    }

    @Nested
    @DisplayName("test 'delete lessonId' method")
    class DeleteLessonIdTest {
        @Test
        @DisplayName("when call delete method then should call " +
            "lessonDao in Order")
        void whenDeleteLesson_CallDaoInOrder() {
            InOrder inOrder = inOrder(lessonDaoMock);

            lessonService.delete(ID1);

            inOrder.verify(lessonDaoMock).deleteAllStudentsFromLesson(ID1);
            inOrder.verify(lessonDaoMock).delete(ID1);
        }
    }

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("when check is passed then should call lessonDao.add once")
        void testAddCheckPassed_CallDaoOnce() {
            Lesson testLesson = createTestLesson(LESSON_ID1);

            lessonService.add(testLesson);

            verify(lessonDaoMock, times(1)).add(testLesson);
        }

        @Test
        @DisplayName("when lesson has teacher who has another lesson at same time " +
            "as checked lesson then should throw ServiceException")
        void whenTeacherHasLessonAtSameTimeAsChecked_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Teacher teacher = testLesson.getTeacher();
            Lesson anotherLessonAtSameTime = Lesson.builder()
                .id(LESSON_ID2)
                .teacher(teacher)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            List<Lesson> lessonsThisTeacher =
                Collections.singletonList(anotherLessonAtSameTime);

            when(lessonDaoMock.getAllForTeacher(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            assertThatThrownBy(() -> lessonService.add(testLesson))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_TEACHER_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson has teacher who has another lesson overlap time " +
            "checked lesson then throw ServiceException")
        void whenTeacherHasLessonOverlapTimeCheckedLesson_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Teacher teacher = testLesson.getTeacher();
            Lesson anotherLessonWithOverlappedTime = Lesson.builder()
                .id(LESSON_ID2)
                .teacher(teacher)
                .timeStart(DATE_START_FIRST_LESSON.minusMinutes(30))
                .timeEnd(DATE_END_FIRST_LESSON.minusMinutes(30))
                .build();
            List<Lesson> lessonsThisTeacher =
                Collections.singletonList(anotherLessonWithOverlappedTime);

            when(lessonDaoMock.getAllForTeacher(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            assertThatThrownBy(() -> lessonService.add(testLesson))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_TEACHER_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson's room is occupied another lesson at same time " +
            "as checked lesson then should throw ServiceException")
        void whenRoomOccupiedAnotherLessonAtSameTimeAsChecked_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Room room = testLesson.getRoom();
            Lesson anotherLessonAtSameTime = Lesson.builder()
                .id(LESSON_ID2)
                .room(room)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            List<Lesson> lessonsThisRoom =
                Collections.singletonList(anotherLessonAtSameTime);

            when(lessonDaoMock.getAllForRoom(ROOM_ID1)).thenReturn(lessonsThisRoom);

            assertThatThrownBy(() -> lessonService.add(testLesson))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_ROOM_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson's room is occupied another lesson which overlap " +
            "time checked lesson then throw ServiceException")
        void whenRoomOccupiedAnotherLessonOverlapTimeCheckedLesson_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Room room = testLesson.getRoom();
            Lesson anotherLessonWithOverlappedTime = Lesson.builder()
                .id(LESSON_ID2)
                .room(room)
                .timeStart(DATE_START_FIRST_LESSON.minusMinutes(30))
                .timeEnd(DATE_END_FIRST_LESSON.minusMinutes(30))
                .build();
            List<Lesson> lessonsThisRoom =
                Collections.singletonList(anotherLessonWithOverlappedTime);

            when(lessonDaoMock.getAllForRoom(ROOM_ID1)).thenReturn(lessonsThisRoom);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.add(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_ROOM_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("when check is passed should call lessonDao.update once")
        void testUpdateCheckPassed_CallDaoOnce() throws ServiceException {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            lessonService.update(testLesson);
            verify(lessonDaoMock, times(1)).update(testLesson);
        }

        @Test
        @DisplayName("when lesson  then should throw ServiceException with message")
        void whenTeacherHasLessonAtSameTimeAsChecked_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Teacher teacher = testLesson.getTeacher();
            Lesson anotherLessonAtSameTime = Lesson.builder()
                .id(LESSON_ID2)
                .teacher(teacher)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            List<Lesson> lessonsThisTeacher =
                Collections.singletonList(anotherLessonAtSameTime);

            when(lessonDaoMock.getAllForTeacher(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_TEACHER_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson has teacher who has another lesson overlap time " +
            "checked lesson then throw ServiceException")
        void whenTeacherHasLessonOverlapTimeCheckedLesson_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Teacher teacher = testLesson.getTeacher();
            Lesson anotherLessonWithOverlappedTime = Lesson.builder()
                .id(LESSON_ID2)
                .teacher(teacher)
                .timeStart(DATE_START_FIRST_LESSON.minusMinutes(30))
                .timeEnd(DATE_END_FIRST_LESSON.minusMinutes(30))
                .build();
            List<Lesson> lessonsThisTeacher =
                Collections.singletonList(anotherLessonWithOverlappedTime);

            when(lessonDaoMock.getAllForTeacher(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_TEACHER_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson's room is occupied another lesson at same time " +
            "as checked lesson then should throw ServiceException")
        void whenRoomOccupiedAnotherLessonAtSameTimeAsChecked_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Room room = testLesson.getRoom();
            Lesson anotherLessonAtSameTime = Lesson.builder()
                .id(LESSON_ID2)
                .room(room)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            List<Lesson> lessonsThisRoom =
                Collections.singletonList(anotherLessonAtSameTime);

            when(lessonDaoMock.getAllForRoom(ROOM_ID1)).thenReturn(lessonsThisRoom);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_ROOM_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("when lesson's room is occupied another lesson which overlap " +
            "time checked lesson then throw ServiceException")
        void whenRoomOccupiedAnotherLessonOverlapTimeCheckedLesson_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Room room = testLesson.getRoom();
            Lesson anotherLessonWithOverlappedTime = Lesson.builder()
                .id(LESSON_ID2)
                .room(room)
                .timeStart(DATE_START_FIRST_LESSON.minusMinutes(30))
                .timeEnd(DATE_END_FIRST_LESSON.minusMinutes(30))
                .build();
            List<Lesson> lessonsThisRoom =
                Collections.singletonList(anotherLessonWithOverlappedTime);

            when(lessonDaoMock.getAllForRoom(ROOM_ID1)).thenReturn(lessonsThisRoom);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.update(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_ROOM_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Lesson then method should" +
            " return this Lesson")
        void testReturnExpectedLesson() {
            Lesson lesson = createTestLesson(LESSON_ID1);
            LessonDto lessonDto = createTestLessonDto(LESSON_ID1);

            when(lessonDaoMock.getById(anyInt())).thenReturn(Optional.of(lesson));
            when(mapperMock.toLessonDto(lesson)).thenReturn(lessonDto);

            assertThat(lessonService.getById(anyInt())).isEqualTo(lessonDto);
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should throw " +
            "EntityNotFoundException")
        void testReturnEmptyLesson() {
            when(lessonDaoMock.getById(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.getById(ID1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lesson id(1) not found");
        }
    }

    @Nested
    @DisplayName("test 'addStudentToLesson' method")
    class AddStudentToLessonTest {

        @Test
        @DisplayName("when the check is passed then method call studentChecker once")
        void testChecksTrue_CallDaoOnce() throws ServiceException {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Student student = Student.builder()
                .id(STUDENT_ID2)
                .active(true)
                .build();

            when(lessonDaoMock.getById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentDaoMock.getById(STUDENT_ID2)).thenReturn(Optional.of(student));

            lessonService.addStudentToLesson(LESSON_ID1, STUDENT_ID2);

            verify(lessonDaoMock).addStudentToLesson(LESSON_ID1, STUDENT_ID2);
        }

        @Test
        @DisplayName("when added inactive student then should throw Exception")
        void whenAddedInactiveStudent_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Student student = Student.builder()
                .id(STUDENT_ID2)
                .active(false)
                .build();

            when(lessonDaoMock.getById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentDaoMock.getById(STUDENT_ID2)).thenReturn(Optional.of(student));


            verify(lessonDaoMock, never()).addStudentToLesson(LESSON_ID1, STUDENT_ID2);

            assertThatThrownBy(() -> lessonService.addStudentToLesson(
                LESSON_ID1, STUDENT_ID2))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_STUDENT_IS_INACTIVE);
        }

        @Test
        @DisplayName("when added student who has another lesson at same time as " +
            "this lesson then should throw ServiceException")
        void whenAddedStudentHasAnotherLessonAtSameTime_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);

            Lesson anotherLessonAtSameTime = Lesson.builder()
                .id(LESSON_ID2)
                .timeStart(DATE_START_FIRST_LESSON)
                .timeEnd(DATE_END_FIRST_LESSON)
                .build();
            Set<Lesson> lessonsThisStudent = new HashSet<>();
            lessonsThisStudent.add(anotherLessonAtSameTime);

            Student studentAdding = Student.builder()
                .id(STUDENT_ID2)
                .active(true)
                .lessons(lessonsThisStudent)
                .build();

            when(lessonDaoMock.getById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentDaoMock.getById(STUDENT_ID2)).thenReturn(Optional.of(studentAdding));

            assertThatThrownBy(() -> lessonService.addStudentToLesson(
                LESSON_ID1, STUDENT_ID2))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_STUDENT_NOT_AVAILABLE);
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
            assertThat(exception.getMessage()).isEqualTo(MESSAGE_FILTER_NOT_SELECT);
        }
    }
}