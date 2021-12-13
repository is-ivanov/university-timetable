package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByGroup(Group group);

    @Query("SELECT s " +
        "FROM Student s " +
        "WHERE s.group.faculty = :faculty")
    List<Student> findAllByFaculty(Faculty faculty);

    List<Student> findAllByActiveTrue();

    @Query("SELECT s " +
        "FROM Student s " +
        "WHERE s.group.id = :groupId " +
          "AND s.group.active = TRUE " +
          "AND s.active = TRUE " +
          "AND s.id NOT IN " +
             "( " +
                "SELECT s2.id " +
                "FROM Student s2 " +
                    "JOIN s2.lessons l " +
                "WHERE l.timeEnd >= :startTime " +
                  "AND l.timeStart <= :endTime " +
             ") " +
        "ORDER BY s.lastName, s.firstName")
    List<Student> findFreeStudentsFromGroup(int groupId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime);
}