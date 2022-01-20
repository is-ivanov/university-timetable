package ua.com.foxminded.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByGroup(Group group);

    @Query("SELECT s " +
        "FROM Student s " +
        "WHERE s.group.faculty = :faculty")
    List<Student> findAllByFaculty(Faculty faculty);

    List<Student> findAllByActiveTrue();

    List<Student> findAllByLessonsTimeEndGreaterThanEqualAndLessonsTimeStartLessThanEqual(LocalDateTime from,
                                                                                          LocalDateTime to);

    default List<Student> findBusyStudentsOnTime(LocalDateTime from, LocalDateTime to) {
        return findAllByLessonsTimeEndGreaterThanEqualAndLessonsTimeStartLessThanEqual(from, to);
    }

    List<Student> findAllByIdNotInAndActiveIsTrueAndGroup_IdIsAndGroup_ActiveIsTrueOrderByLastNameAscFirstNameAsc(
        Collection<Integer> studentIds, Integer groupId);

    default List<Student> findAllFromGroupExcluded(Collection<Integer> studentIds,
                                                   Integer groupId) {
        return findAllByIdNotInAndActiveIsTrueAndGroup_IdIsAndGroup_ActiveIsTrueOrderByLastNameAscFirstNameAsc(
            studentIds, groupId);
    }

    List<Student> findAllByActiveTrueAndGroup_Id(int groupId);
}