package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

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

    List<Teacher> findByActiveIsTrueAndIdNotInOrderByLastNameAscFirstNameAsc(Collection<Integer> ids);

}
