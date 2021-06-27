package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Lesson;

public interface LessonDao extends Dao<Lesson> {

    void addStudentToLesson(int lessonId, int studentId);

}
