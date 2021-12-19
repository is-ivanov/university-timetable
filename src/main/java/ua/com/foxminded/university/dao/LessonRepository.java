package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Lesson;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer>,
    QuerydslPredicateExecutor<Lesson> {

    @Modifying
    @Query(value = "DELETE FROM students_lessons WHERE lesson_id = :lessonId",
        nativeQuery = true)
    void deleteAllStudentsFromLesson(int lessonId);

    @Modifying
    @Query(value = "INSERT INTO students_lessons(student_id, lesson_id) " +
        "VALUES (:studentId, :lessonId)",
        nativeQuery = true)
    void addStudentToLesson(int lessonId, int studentId);

    List<Lesson> findAllByTeacherId(int teacherId);

    List<Lesson> findAllByRoomId(int roomId);

//    @Modifying
//    @Query(value = "DELETE FROM students_lessons " +
//        "WHERE student_id = :studentId AND lesson_id = :lessonId",
//        nativeQuery = true)
//    void deleteStudentFromLesson(int lessonId, int studentId);

//    @Query("SELECT l " +
//        "FROM Lesson l " +
//        "JOIN FETCH l.course c " +
//        "JOIN FETCH l.teacher t " +
//        "JOIN FETCH l.room r " +
//        "JOIN FETCH l.students s " +
//        "WHERE s.id = :studentId " +
//        "AND l.timeStart >= :startTime " +
//        "AND l.timeEnd <= :endTime")
//    List<Lesson> findAllForStudentForTimePeriod(int studentId,
//                                                LocalDateTime startTime,
//                                                LocalDateTime endTime);

    List<Lesson> findByStudents_IdAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(Integer studentId,
                                                                                      LocalDateTime from,
                                                                                      LocalDateTime to);

//    @Query("SELECT l " +
//        "FROM Lesson l " +
//        "JOIN FETCH l.course c " +
//        "JOIN FETCH l.teacher t " +
//        "JOIN FETCH l.room r " +
//        "WHERE t.id = :teacherId " +
//        "AND l.timeStart >= :startTime " +
//        "AND l.timeEnd <= :endTime")
//    List<Lesson> findAllForTeacherForTimePeriod(int teacherId,
//                                                LocalDateTime startTime,
//                                                LocalDateTime endTime);

    List<Lesson> findByTeacher_IdAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(Integer teacherId,
                                                                                     LocalDateTime from,
                                                                                     LocalDateTime to);

//    @Query("SELECT l " +
//        "FROM Lesson l " +
//        "JOIN FETCH l.course c " +
//        "JOIN FETCH l.teacher t " +
//        "JOIN FETCH l.room r " +
//        "WHERE l.room.id = :roomId " +
//        "AND l.timeStart >= :startTime " +
//        "AND l.timeEnd <= :endTime")
//    List<Lesson> findAllByRoomByTimePeriod(int roomId,
//                                           LocalDateTime startTime,
//                                           LocalDateTime endTime);

    List<Lesson> findByRoom_IdAndTimeStartGreaterThanEqualAndTimeEndLessThanEqual(Integer teacherId,
                                                                                     LocalDateTime from,
                                                                                     LocalDateTime to);

}
