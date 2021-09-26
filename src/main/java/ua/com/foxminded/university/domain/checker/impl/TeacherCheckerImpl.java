package ua.com.foxminded.university.domain.checker.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.checker.interfaces.TeacherChecker;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class TeacherCheckerImpl implements TeacherChecker {

    private static final String MESSAGE_TEACHER_NOT_AVAILABLE = "Teacher id(%d) is not available";

    private final LessonDao lessonDao;
    private final AvailableLessonChecker availableLessonChecker;

    @Override
    public void check(Teacher teacher, Lesson lesson) {
        log.debug("Checking the teacher id({}) for lesson id({})", teacher.getId(),
            lesson.getId());
        List<Lesson> lessonsFromThisTeacher = lessonDao.getAllForTeacher(teacher.getId());
        try {
            availableLessonChecker.checkAvailableLesson(lesson, lessonsFromThisTeacher);
        } catch (ServiceException e) {
            log.warn("Teacher id({}) is not available for the lesson id({})",
                teacher.getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_TEACHER_NOT_AVAILABLE,
                teacher.getId()), e);
        }
    }
}
