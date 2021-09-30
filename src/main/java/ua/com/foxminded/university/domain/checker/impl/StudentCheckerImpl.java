package ua.com.foxminded.university.domain.checker.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.foxminded.university.domain.checker.interfaces.StudentChecker;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.LessonService;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

@Slf4j
@NoArgsConstructor
@Component
public class StudentCheckerImpl implements StudentChecker {

    private static final String MESSAGE_STUDENT_NOT_AVAILABLE = "Student id(%d) is not available";

    private LessonService lessonService;
    private AvailableLessonChecker availableLessonChecker;

    @Autowired
    public void setLessonService(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Autowired
    public void setAvailableLessonChecker(AvailableLessonChecker availableLessonChecker) {
        this.availableLessonChecker = availableLessonChecker;
    }

    @Override
    public void check(Student student, Lesson lesson) {
        log.debug("Checking the student id({}) for lesson id({})",
            student.getId(), lesson.getId());
        List<Lesson> lessonsFromThisStudent = lessonService.getLessonsForStudent(student);
        try {
            availableLessonChecker.checkAvailableLesson(lesson, lessonsFromThisStudent);
        } catch (ServiceException e) {
            log.warn("Student id({}) is not available for the lesson id({})",
                student.getId(), lesson.getId());
            throw new ServiceException(String.format(MESSAGE_STUDENT_NOT_AVAILABLE,
                student.getId()), e);
        }
    }
}
