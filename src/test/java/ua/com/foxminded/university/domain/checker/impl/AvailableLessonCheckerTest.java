package ua.com.foxminded.university.domain.checker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.util.ArrayList;
import java.util.List;

import static ua.com.foxminded.university.TestObjects.LESSON_ID1;
import static ua.com.foxminded.university.TestObjects.createTestLesson;

@ExtendWith(MockitoExtension.class)
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
        @DisplayName("when list lessons is empty then do nothing")
        void whenListLessonsIsEmptyThenDoNothing() {
            Lesson checkedLesson = createTestLesson(LESSON_ID1);
            List<Lesson> lessons = new ArrayList<>();

            availableLessonChecker.checkAvailableLesson(checkedLesson, lessons);

        }
    }
}