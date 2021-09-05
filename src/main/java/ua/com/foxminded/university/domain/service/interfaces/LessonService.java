package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

public interface LessonService extends Service<Lesson> {

    void addStudentToLesson(Lesson lesson, Student student);

    void addStudentToLesson(int lessonId, int studentId);

    void addStudentsFromGroupToLesson(int groupId, int lessonId);

    List<Lesson> getAllWithFilter(LessonFilter filter);

}