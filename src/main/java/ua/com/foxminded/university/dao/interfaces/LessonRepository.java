package ua.com.foxminded.university.dao.interfaces;

import java.time.LocalDateTime;
import java.util.List;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;

public interface LessonRepository extends Repository<Lesson> {

    void addStudentToLesson(int lessonId, int studentId);

    void deleteAllStudentsFromLesson(int lessonId);

    List<Lesson> getAllForTeacher(int teacherId);

    List<Lesson> getAllForRoom(int roomId);

    List<Lesson> getAllForStudent(int studentId);

    void removeStudentFromLesson(int lessonId, int studentId);

    List<Lesson> getAllWithFilter(LessonFilter filter);

    List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<Lesson> getAllForTeacherForTimePeriod(int teacherId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<Lesson> getAllForRoomForTimePeriod(int roomId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);
}
