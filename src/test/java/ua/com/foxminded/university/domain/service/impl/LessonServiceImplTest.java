package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.LessonRepository;
import ua.com.foxminded.university.dao.StudentRepository;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;
import ua.com.foxminded.university.exception.ServiceException;

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
    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student id(3) is not available";
    public static final String MESSAGE_STUDENT_IS_INACTIVE = "Student id(3) is inactive";

    @Mock
    private LessonRepository lessonRepoMock;

    @Mock
    private StudentRepository studentRepoMock;

    @Mock
    private StudentService studentServiceMock;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    @DisplayName("test 'findAll' when Repository return List lessons then method " +
        "should return this List")
    void testFindAll_ReturnListLessons() {
        List<Lesson> lessons = createTestLessons();

        when(lessonRepoMock.findAll()).thenReturn(lessons);

        assertThat(lessonService.findAll()).isEqualTo(lessons);
    }


    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {
        @Test
        @DisplayName("when call delete method then should call lessonDao ")
        void whenDeleteLesson_CallDaoInOrder() {
            int lessonId = anyInt();

            lessonService.delete(lessonId);

            verify(lessonRepoMock).deleteById(lessonId);
        }
    }

    @Nested
    @DisplayName("test 'create' method")
    class CreateTest {

        @Test
        @DisplayName("when check is passed then should call lessonDao.save once")
        void testAddCheckPassed_CallDaoOnce() {
            Lesson testLesson = createTestLesson(LESSON_ID1);

            lessonService.create(testLesson);

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

            assertThatThrownBy(() -> lessonService.create(testLesson))
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

            assertThatThrownBy(() -> lessonService.create(testLesson))
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

            assertThatThrownBy(() -> lessonService.create(testLesson))
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
                () -> lessonService.create(testLesson));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_ROOM_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("test 'findById' method")
    class FindByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Lesson then method should" +
            " return this Lesson")
        void testReturnExpectedLesson() {
            Lesson lesson = createTestLesson(LESSON_ID1);

            when(lessonRepoMock.findById(anyInt())).thenReturn(Optional.of(lesson));

            assertThat(lessonService.findById(anyInt())).isEqualTo(lesson);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw " +
            "EntityNotFoundException")
        void testReturnEmptyLesson() {
            when(lessonRepoMock.findById(anyInt())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Lesson with id(1) not found");
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
                .id(STUDENT_ID3)
                .active(true)
                .build();

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentServiceMock.findById(STUDENT_ID3)).thenReturn(student);

            lessonService.addStudentToLesson(LESSON_ID1, STUDENT_ID3);

            verify(lessonRepoMock).save(testLesson);
        }

        @Test
        @DisplayName("when added inactive student then should throw Exception")
        void whenAddedInactiveStudent_ThrowException() {
            Lesson testLesson = createTestLesson(LESSON_ID1);
            Student student = Student.builder()
                .id(STUDENT_ID3)
                .active(false)
                .build();

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentServiceMock.findById(STUDENT_ID3)).thenReturn(student);

            assertThatThrownBy(() -> lessonService.addStudentToLesson(
                LESSON_ID1, STUDENT_ID3))
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
                .id(STUDENT_ID3)
                .active(true)
                .lessons(lessonsThisStudent)
                .build();

            when(lessonRepoMock.findById(LESSON_ID1)).thenReturn(Optional.of(testLesson));
            when(studentServiceMock.findById(STUDENT_ID3)).thenReturn(studentAdding);

            assertThatThrownBy(() -> lessonService.addStudentToLesson(
                LESSON_ID1, STUDENT_ID3))
                .isInstanceOf(ServiceException.class)
                .hasMessage(MESSAGE_STUDENT_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("test 'removeStudentsFromLesson' method")
    class RemoveStudentsFromLessonTest {
        @Test
        @DisplayName("Test name")
        void testName() {

            Student student1 = createTestStudent();
            Student student2 = Student.builder()
                .id(STUDENT_ID2)
                .lessons(new HashSet<>())
                .build();
            Student student3 = Student.builder()
                .id(STUDENT_ID3)
                .lessons(new HashSet<>())
                .build();

            Lesson testLesson = createTestLesson();
            testLesson.getStudents().add(student3);
            student1.getLessons().add(testLesson);
            student2.getLessons().add(testLesson);
            student3.getLessons().add(testLesson);

            Integer[] studentIds = new Integer[] {STUDENT_ID2, STUDENT_ID3};

            when(lessonRepoMock.getById(LESSON_ID1)).thenReturn(testLesson);
            when(studentRepoMock.getById(STUDENT_ID2)).thenReturn(student2);
            when(studentRepoMock.getById(STUDENT_ID3)).thenReturn(student3);

            lessonService.removeStudentsFromLesson(LESSON_ID1, studentIds);

            assertThat(testLesson.getStudents()).hasSize(1);
        }
    }
}