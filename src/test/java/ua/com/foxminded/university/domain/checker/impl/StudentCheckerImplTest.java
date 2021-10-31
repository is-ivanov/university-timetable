package ua.com.foxminded.university.domain.checker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class StudentCheckerImplTest {

    private Student student;
    private Lesson lesson;

    @Mock
    private LessonService lessonServiceMock;

    @Mock
    private AvailableLessonChecker availableLessonCheckerMock;

    @InjectMocks
    private StudentCheckerImpl studentChecker;

    @BeforeEach
    void setUp() {
        student = createTestStudent();
        lesson = createTestLesson(LESSON_ID1);
    }

    @Nested
    @DisplayName("test 'check' method")
    class CheckTest {
        @Test
        @DisplayName("call LessonService and AvailableLessonChecker with expected parameters")
        void callLessonServiceAndAvailableLessonCheckerWithExpectedParameters() {
            List<Lesson> testLessons = createTestLessons();
            when(lessonServiceMock.getLessonsForStudent(student)).thenReturn(testLessons);

            studentChecker.check(student, lesson);

            verify(lessonServiceMock, times(1)).getLessonsForStudent(student);
            verify(availableLessonCheckerMock, times(1))
                .checkAvailableLesson(lesson, testLessons);
        }

        @Test
        @DisplayName("when availableLessonChecker throw ServiceException then should throw Exception")
        void whenAvailableLessonCheckerThrowServiceExceptionShouldThrowException() {
            List<Lesson> testLessons = createTestLessons();

            when(lessonServiceMock.getLessonsForStudent(student)).thenReturn(testLessons);
            doThrow(ServiceException.class).when(availableLessonCheckerMock)
                .checkAvailableLesson(lesson, testLessons);

            ServiceException e = assertThrows(ServiceException.class,
                () -> studentChecker.check(student, lesson));
            String expectedMessage = "Student id(" + student.getId() + ") is not available";
            assertThat(e.getMessage(), is(equalTo(expectedMessage)));
        }
    }
}