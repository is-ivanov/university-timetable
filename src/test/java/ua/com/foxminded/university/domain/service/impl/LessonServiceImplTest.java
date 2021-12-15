package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.LessonRepository;
import ua.com.foxminded.university.dao.StudentRepository;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
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
    public static final String MESSAGE_STUDENT_IS_INACTIVE = "Student id(78) is inactive";

    @Mock
    private LessonRepository lessonRepoMock;

    @Mock
    private StudentRepository studentRepoMock;

    @Mock
    private LessonDtoMapper mapperMock;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    @DisplayName("test 'getAll' when Repository return List lessons then method " +
        "should return this List")
    void testGetAll_ReturnListLessons() {
        List<Lesson> lessons = createTestLessons();
        List<LessonDto> lessonDtos = createTestLessonDtos();

        when(lessonRepoMock.findAll()).thenReturn(lessons);
        when(mapperMock.toLessonDtos(lessons)).thenReturn(lessonDtos);

        assertThat(lessonService.getAll()).isEqualTo(lessonDtos);
    }


    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {
        @Test
        @DisplayName("when call delete method then should call " +
            "lessonDao in Order")
        void whenDeleteLesson_CallDaoInOrder() {
            InOrder inOrder = inOrder(lessonRepoMock);
            int lessonId = anyInt();

            lessonService.delete(lessonId);

            inOrder.verify(lessonRepoMock).deleteAllStudentsFromLesson(lessonId);
            inOrder.verify(lessonRepoMock).deleteById(lessonId);
        }
    }

    @Nested
    @DisplayName("test 'delete lessonId' method")
    class DeleteLessonIdTest {
        @Test
        @DisplayName("when call delete method then should call " +
            "lessonDao in Order")
        void whenDeleteLesson_CallDaoInOrder() {
            InOrder inOrder = inOrder(lessonRepoMock);

            lessonService.delete(ID1);

            inOrder.verify(lessonRepoMock).deleteAllStudentsFromLesson(ID1);
            inOrder.verify(lessonRepoMock).deleteById(ID1);
        }
    }

    @Nested
    @DisplayName("test 'save' method")
    class SaveTest {

        @Test
        @DisplayName("when check is passed then should call lessonDao.add once")
        void testAddCheckPassed_CallDaoOnce() {
            Lesson testLesson = createTestLesson(LESSON_ID1);

            lessonService.save(testLesson);

            verify(lessonRepoMock, times(1)).save(testLesson);
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

            when(lessonRepoMock.findAllByTeacherId(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            assertThatThrownBy(() -> lessonService.save(testLesson))
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

            when(lessonRepoMock.findAllByTeacherId(TEACHER_ID1)).thenReturn(lessonsThisTeacher);

            assertThatThrownBy(() -> lessonService.save(testLesson))
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

            when(lessonRepoMock.findAllByRoomId(ROOM_ID1)).thenReturn(lessonsThisRoom);

            assertThatThrownBy(() -> lessonService.save(testLesson))
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

            when(lessonRepoMock.findAllByRoomId(ROOM_ID1)).thenReturn(lessonsThisRoom);

            ServiceException e = assertThrows(ServiceException.class,
                () -> lessonService.save(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_ROOM_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Lesson then method should" +
            " return this Lesson")
        void testReturnExpectedLesson() {
            Lesson lesson = createTestLesson(LESSON_ID1);
            LessonDto lessonDto = createTestLessonDto(LESSON_ID1);

            when(lessonRepoMock.findById(anyInt())).thenReturn(Optional.of(lesson));
            when(mapperMock.toLessonDto(lesson)).thenReturn(lessonDto);

            assertThat(lessonService.getById(anyInt())).isEqualTo(lessonDto);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw " +
            "EntityNotFoundException")
        void testReturnEmptyLesson() {
            when(lessonRepoMock.findById(anyInt())).thenReturn(Optional.empty());

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

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentRepoMock.findById(STUDENT_ID2)).thenReturn(Optional.of(student));

            lessonService.addStudentToLesson(LESSON_ID1, STUDENT_ID2);

            verify(lessonRepoMock).addStudentToLesson(LESSON_ID1, STUDENT_ID2);
        }

        @Test
        @DisplayName("when added inactive student then should throw Exception")
        void whenAddedInactiveStudent_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Student student = Student.builder()
                .id(STUDENT_ID2)
                .active(false)
                .build();

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentRepoMock.findById(STUDENT_ID2)).thenReturn(Optional.of(student));


            verify(lessonRepoMock, never()).addStudentToLesson(LESSON_ID1, STUDENT_ID2);

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

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentRepoMock.findById(STUDENT_ID2)).thenReturn(Optional.of(studentAdding));

            assertThatThrownBy(() -> lessonService.addStudentToLesson(
                LESSON_ID1, STUDENT_ID2))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_STUDENT_NOT_AVAILABLE);
        }
    }
}