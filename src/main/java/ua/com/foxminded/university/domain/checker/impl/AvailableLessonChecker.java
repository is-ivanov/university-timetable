package ua.com.foxminded.university.domain.checker.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class AvailableLessonChecker {

    public static final String MESSAGE_EXCEPTION = "Lesson id(%d) intersect with lesson id(%d)";

    public void checkAvailableLesson(Lesson checkedLesson, List<Lesson> lessons) {
        if (checkLessonsIsEmpty(lessons)) {
            return;
        }
        checkLessonTime(checkedLesson, lessons);
    }

    public void checkLessonTime(Lesson checkedLesson, List<Lesson> lessons) {
        log.debug("Checking the intersection of time lesson id({}) with other lessons",
            checkedLesson.getId());
        LocalDateTime timeStartCheckedLesson = checkedLesson.getTimeStart();
        LocalDateTime timeEndCheckedLesson = checkedLesson.getTimeEnd();
        for (Lesson lesson : lessons) {
            if (lesson.getId() != checkedLesson.getId()) {
                LocalDateTime timeStartLesson = lesson.getTimeStart();
                LocalDateTime timeEndLesson = lesson.getTimeEnd();
                if ((timeStartCheckedLesson.isAfter(timeStartLesson)
                    && timeStartCheckedLesson.isBefore(timeEndLesson))
                    || (timeEndCheckedLesson.isAfter(timeStartLesson)
                    && timeEndCheckedLesson.isBefore(timeEndLesson))) {
                    log.warn("Time lesson id({}) intersect with time lesson id({})",
                        checkedLesson.getId(), lesson.getId());
                    throw new ServiceException(
                        String.format(MESSAGE_EXCEPTION, checkedLesson.getId(), lesson.getId()));
                }
            }
        }
        log.info("Checking passed");
    }

    private boolean checkLessonsIsEmpty(List<Lesson> lessons){
        log.debug("Checking list lessons is empty");
        if (lessons.isEmpty()){
            log.info("Checking passed");
            return true;
        }
        return false;
    }

}
