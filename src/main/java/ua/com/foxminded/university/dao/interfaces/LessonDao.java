package ua.com.foxminded.university.dao.interfaces;

import java.util.List;

import ua.com.foxminded.university.domain.entity.Lesson;

public interface LessonDao extends Dao<Lesson> {

    void addStudentToLesson(int lessonId, int studentId);

    void deleteAllStudentsFromLesson(int lessonId);

    List<Lesson> getAllByTeacher(int teacherId);

    List<Lesson> getAllByRoom(int roomId);

    List<Lesson> getAllByStudent(int studentId);

    void deleteStudentFromLesson(int lessonId, int studentId);

}
