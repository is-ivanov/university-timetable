package ua.com.foxminded.university.domain.checker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ua.com.foxminded.university.TestObjects.LESSON_ID1;
import static ua.com.foxminded.university.TestObjects.createTestLesson;

class AvailableLessonCheckerTest {


    private AvailableLessonChecker availableLessonChecker;

    @BeforeEach
    void setUp() {
        availableLessonChecker = new AvailableLessonChecker();
    }

    @Nested
    @DisplayName("test 'checkAvailableLesson' method")
    class CheckAvailableLessonTest {
        @Test
        @DisplayName("when list lessons is empty then return true")
        void whenListLessonsIsEmptyThenDoNothing() {
            Lesson checkedLesson = createTestLesson(LESSON_ID1);
            List<Lesson> lessons = new ArrayList<>();

            assertDoesNotThrow(() -> availableLessonChecker
                .checkAvailableLesson(checkedLesson, lessons));
        }

        @Test
        @DisplayName("when time checked lesson don't intersect with time other " +
            "lessons then return true")
        void whenTimeCheckedLessonDonTIntersectWithTimeOtherLessonsThenReturnTrue() {
            LocalDateTime timeStartCheckedLesson = LocalDateTime.of(2021, 10, 29, 8, 30);
            LocalDateTime timeEndCheckedLesson = LocalDateTime.of(2021, 10, 29, 10, 0);
            Lesson checkedLesson = Lesson.builder()
                .id(LESSON_ID1)
                .timeStart(timeStartCheckedLesson)
                .timeEnd(timeEndCheckedLesson)
                .build();

            LocalDateTime timeStartFirstLesson = LocalDateTime.of(2021, 9, 29, 8, 30);
            LocalDateTime timeEndFirstLesson = LocalDateTime.of(2021, 9, 29, 10, 0);
            Lesson firstLesson = Lesson.builder()
                .id(34)
                .timeStart(timeStartFirstLesson)
                .timeEnd(timeEndFirstLesson)
                .build();

            LocalDateTime timeStartSecondLesson = LocalDateTime.of(2021, 10, 28, 10, 15);
            LocalDateTime timeEndSecondLesson = LocalDateTime.of(2021, 10, 28, 11, 45);
            Lesson secondLesson = Lesson.builder()
                .id(56)
                .timeStart(timeStartSecondLesson)
                .timeEnd(timeEndSecondLesson)
                .build();

            List<Lesson> lessons = Arrays.asList(firstLesson, secondLesson);

            assertDoesNotThrow(() -> availableLessonChecker
                .checkAvailableLesson(checkedLesson, lessons));
        }

        @Test
        @DisplayName("when there is lesson at the same time as checked lesson " +
            "then throw new ServiceException")
        void whenThereIsLessonSameTimeAsCheckedLessonThenThrowException() {
            LocalDateTime timeStartCheckedLesson = LocalDateTime.of(2021, 10, 29, 8, 30);
            LocalDateTime timeEndCheckedLesson = LocalDateTime.of(2021, 10, 29, 10, 0);
            Lesson checkedLesson = Lesson.builder()
                .id(LESSON_ID1)
                .timeStart(timeStartCheckedLesson)
                .timeEnd(timeEndCheckedLesson)
                .build();

            LocalDateTime timeStartLessonSameTime = LocalDateTime.of(2021, 10, 29, 8, 30);
            LocalDateTime timeEndLessonSameTime = LocalDateTime.of(2021, 10, 29, 10, 0);
            int idLessonSameTime = 34;
            Lesson lessonSameTime = Lesson.builder()
                .id(idLessonSameTime)
                .timeStart(timeStartLessonSameTime)
                .timeEnd(timeEndLessonSameTime)
                .build();

            List<Lesson> lessons = Collections.singletonList(lessonSameTime);

            ServiceException e = assertThrows(ServiceException.class,
                () -> availableLessonChecker.checkAvailableLesson(checkedLesson, lessons));
            String expectedMessage = "Lesson id(" + LESSON_ID1 +
                ") intersect with lesson id(" + idLessonSameTime + ")";
            assertThat(e.getMessage(), is(equalTo(expectedMessage)));
        }

        @Test
        @DisplayName("when time checked lesson intersect with time other lessons " +
            "then throw new ServiceException")
        void whenTimeCheckedLessonIntersectWithTimeOtherLessonsThenReturnTrue() {
            LocalDateTime timeStartCheckedLesson = LocalDateTime.of(2021, 10, 29, 8, 30);
            LocalDateTime timeEndCheckedLesson = LocalDateTime.of(2021, 10, 29, 10, 0);
            Lesson checkedLesson = Lesson.builder()
                .id(LESSON_ID1)
                .timeStart(timeStartCheckedLesson)
                .timeEnd(timeEndCheckedLesson)
                .build();

            LocalDateTime timeStartLessonIntersectTime = LocalDateTime.of(2021, 10, 29, 9, 30);
            LocalDateTime timeEndLessonIntersectTime = LocalDateTime.of(2021, 10, 29, 11, 0);
            int idLessonIntersectTime = 34;
            Lesson lessonIntersectTime = Lesson.builder()
                .id(idLessonIntersectTime)
                .timeStart(timeStartLessonIntersectTime)
                .timeEnd(timeEndLessonIntersectTime)
                .build();

            List<Lesson> lessons = Collections.singletonList(lessonIntersectTime);

            ServiceException e = assertThrows(ServiceException.class,
                () -> availableLessonChecker.checkAvailableLesson(checkedLesson, lessons));
            String expectedMessage = "Lesson id(" + LESSON_ID1 +
                ") intersect with lesson id(" + idLessonIntersectTime + ")";
            assertThat(e.getMessage(), is(equalTo(expectedMessage)));
        }
    }
}