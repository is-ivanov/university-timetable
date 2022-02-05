package ua.com.foxminded.university.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    List<Department> findAll();

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    Page<Department> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"faculty"})
    Optional<Department> findById(Integer integer);

    @EntityGraph(attributePaths = {"faculty"})
    List<Department> findAllByFacultyId(int facultyId);

}