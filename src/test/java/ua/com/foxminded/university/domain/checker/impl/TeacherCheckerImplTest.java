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
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class TeacherCheckerImplTest {

    private Teacher teacher;
    private Lesson lesson;

    @Mock
    private LessonDao lessonDaoMock;

    @Mock
    private AvailableLessonChecker availableLessonCheckerMock;

    @InjectMocks
    private TeacherCheckerImpl teacherChecker;

    @BeforeEach
    void setUp() {
        teacher = createTestTeacher();
        lesson = createTestLesson(LESSON_ID1);
    }

    @Nested
    @DisplayName("test 'check' method")
    class CheckTest {
        @Test
        @DisplayName("call LessonDao and AvailableLessonChecker with expected parameters")
        void callLessonDaoAndAvailableLessonCheckerWithExpectedParameters() {
            List<Lesson> testLessons = createTestLessons();

            int teacherId = teacher.getId();
            when(lessonDaoMock.getAllForTeacher(teacherId)).thenReturn(testLessons);

            teacherChecker.check(teacher, lesson);

            verify(lessonDaoMock, times(1)).getAllForTeacher(teacherId);
            verify(availableLessonCheckerMock, times(1))
                .checkAvailableLesson(lesson, testLessons);
        }

        @Test
        @DisplayName("when availableLessonChecker throw ServiceException then should throw Exception")
        void whenAvailableLessonCheckerThrowServiceExceptionShouldThrowException() {
            List<Lesson> testLessons = createTestLessons();
            int teacherId = teacher.getId();

            when(lessonDaoMock.getAllForTeacher(teacherId)).thenReturn(testLessons);
            doThrow(ServiceException.class).when(availableLessonCheckerMock)
                .checkAvailableLesson(lesson, testLessons);

            ServiceException e = assertThrows(ServiceException.class,
                () -> teacherChecker.check(teacher, lesson));
            String expectedMessage = "Teacher id(" + teacherId + ") is not available";
            assertThat(e.getMessage(), is(equalTo(expectedMessage)));
        }
    }
}