package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Group;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    List<Group> findAllByFacultyId(int facultyId);

    List<Group> findAllByActiveTrue();

    List<Group> findAllByActiveTrueAndFaculty_IdOrderByNameAsc(Integer facultyId);

    @Query("SELECT DISTINCT g " +
           "FROM Group g " +
                "LEFT JOIN Student s ON s.group = g " +
           "WHERE g.active = TRUE " +
             "AND s.active = TRUE " +
             "AND s.id NOT IN :studentIds " +
           "ORDER BY g.name")
    List<Group> findAllActiveWithoutStudents(List<Integer> studentIds);

    @Query("SELECT DISTINCT g " +
           "FROM Group g " +
               "LEFT JOIN Student s ON s.group = g " +
           "WHERE g.active = TRUE " +
             "AND s.active = TRUE " +
             "AND s.id NOT IN :studentIds " +
             "AND g.faculty.id = :facultyId " +
           "ORDER BY g.name")
    List<Group> findAllActiveWithoutStudentsByFaculty(List<Integer> studentIds,
                                                      int facultyId);
}