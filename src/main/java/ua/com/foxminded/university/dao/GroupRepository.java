package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Group;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findAllByFacultyId(int facultyId);


    @Query("SELECT DISTINCT g " +
        "FROM Group g " +
        "LEFT JOIN Student s ON s.group = g " +
        "WHERE g.active = TRUE " +
        "AND g.faculty.id = :facultyId " +
        "AND s.active = TRUE " +
        "AND s.id NOT IN " +
        "(" +
        "SELECT s2.id " +
        "FROM Student s2 " +
        "JOIN s2.lessons l " +
        "WHERE s2.active = TRUE " +
        "AND l.timeEnd >= :startTime " +
        "AND l.timeStart <= :endTime " +
        ") " +
        "ORDER BY g.name")
    List<Group> findFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                    LocalDateTime startTime,
                                                    LocalDateTime endTime);

    List<Group> findAllByActiveTrue();

    @Query("SELECT DISTINCT g " +
           "FROM Group g " +
                "LEFT JOIN Student s ON s.group = g " +
           "WHERE g.active = TRUE " +
             "AND s.active = TRUE " +
             "AND s.id NOT IN :studentIds " +
           "ORDER BY g.name")
    List<Group> findAllActiveWithoutStudents(List<Integer> studentIds);
}