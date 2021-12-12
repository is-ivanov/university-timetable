package ua.com.foxminded.university.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {

    List<Faculty> findAllByOrderByNameAsc();

//    Page<Faculty> getAllSortedPaginated(Pageable pageable);

//    int countAll();
}