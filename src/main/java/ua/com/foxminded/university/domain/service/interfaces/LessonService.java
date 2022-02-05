package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonService extends Service<Lesson> {

//    void update(LessonDto lessonDto);

    Lesson addStudentToLesson(int lessonId, int studentId);

    void addStudentsFromGroupToLesson(int groupId, int lessonId);

    Iterable<Lesson> getAllWithFilter(LessonFilter filter);

    List<Lesson> getAllForStudentForTimePeriod(int studentId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<Lesson> getAllForTeacherForTimePeriod(int teacherId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<Lesson> getAllForRoomForTimePeriod(int roomId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);

    void removeStudentFromLesson(int lessonId, int studentId);

    void removeStudentsFromLesson(int lessonId, Integer[] studentIds);

}