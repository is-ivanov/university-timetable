package ua.com.foxminded.university.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    @Override
    @EntityGraph(attributePaths = {"department"})
    List<Teacher> findAll();

    @Override
    @EntityGraph(attributePaths = {"department"})
    Page<Teacher> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"department"})
    Optional<Teacher> findById(Integer integer);

    @EntityGraph(attributePaths = {"department"})
    List<Teacher> findAllByDepartmentId(int departmentId);

    @EntityGraph(attributePaths = {"department"})
    List<Teacher> findByDepartment_Faculty_IdIs(int facultyId);

    @Query("SELECT t " +
           "FROM Teacher t " +
               "LEFT JOIN Lesson l ON t = l.teacher " +
           "WHERE l.timeEnd >= :from " +
             "AND l.timeStart <= :to ")
    List<Teacher> findBusyTeachersOnTime(LocalDateTime from, LocalDateTime to);

    @EntityGraph(attributePaths = {"department"})
    List<Teacher> findByActiveIsTrueAndIdNotInOrderByLastNameAscFirstNameAsc(Collection<Integer> ids);

}
