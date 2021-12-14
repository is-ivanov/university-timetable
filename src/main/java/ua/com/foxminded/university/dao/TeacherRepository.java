package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    List<Teacher> findAllByDepartmentId(int departmentId);

    @Query("SELECT t " +
        "FROM Teacher t " +
        "WHERE t.department.faculty.id = :facultyId")
    List<Teacher> findAllByFaculty(int facultyId);

    @Query("SELECT t FROM Teacher t " +
        "WHERE t.active = TRUE " +
        "AND t.id NOT IN " +
        "( " +
        "SELECT t2.id " +
        "FROM Teacher t2 " +
        "LEFT JOIN Lesson l ON t2 = l.teacher " +
        "WHERE l.timeEnd >= :startTime " +
        "AND l.timeStart <= :endTime " +
        ") " +
        "ORDER BY t.lastName, t.firstName")
    List<Teacher> findFreeTeachersOnLessonTime(LocalDateTime startTime,
                                               LocalDateTime endTime);
}
