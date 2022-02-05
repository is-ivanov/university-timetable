package ua.com.foxminded.university.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Group;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAll();

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    Page<Group> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    Optional<Group> findById(Integer integer);

    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAllByFacultyId(int facultyId);

    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAllByActiveTrue();

    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAllByActiveTrueAndFaculty_IdOrderByNameAsc(Integer facultyId);

    @Query("SELECT DISTINCT g " +
           "FROM Group g " +
                "LEFT JOIN Student s ON s.group = g " +
           "WHERE g.active = TRUE " +
             "AND s.active = TRUE " +
             "AND s.id NOT IN :studentIds " +
           "ORDER BY g.name")
    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAllActiveWithoutStudents(List<Integer> studentIds);

    @Query("SELECT DISTINCT g " +
           "FROM Group g " +
               "LEFT JOIN Student s ON s.group = g " +
           "WHERE g.active = TRUE " +
             "AND s.active = TRUE " +
             "AND s.id NOT IN :studentIds " +
             "AND g.faculty.id = :facultyId " +
           "ORDER BY g.name")
    @EntityGraph(attributePaths = {"faculty"})
    List<Group> findAllActiveWithoutStudentsByFaculty(List<Integer> studentIds,
                                                      int facultyId);
}