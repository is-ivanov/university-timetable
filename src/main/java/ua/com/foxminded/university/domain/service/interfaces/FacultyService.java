package ua.com.foxminded.university.domain.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

public interface FacultyService extends Service<Faculty> {

    List<Faculty> getAllSortedByNameAsc();

    Page<Faculty> getAllSortedPaginated(Pageable pageable);
}