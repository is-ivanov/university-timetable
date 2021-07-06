package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Lesson;

public interface LessonService extends Service<Lesson> {

    void addStudentToLesson(Lesson lesson, int studentId);
}
