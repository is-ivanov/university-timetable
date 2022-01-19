package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.filter.LessonFilter;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonService extends Service<Lesson> {

//    void update(LessonDto lessonDto);

    void addStudentToLesson(int lessonId, int studentId);

    void addStudentsFromGroupToLesson(int groupId, int lessonId);

    List<LessonDto> getAllWithFilter(LessonFilter filter);

    List<LessonDto> getAllForStudentForTimePeriod(int studentId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<LessonDto> getAllForTeacherForTimePeriod(int teacherId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime);

    List<LessonDto> getAllForRoomForTimePeriod(int roomId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);

    void removeStudentFromLesson(int lessonId, int studentId);

    void removeStudentsFromLesson(int lessonId, Integer[] studentIds);

}