package ua.com.foxminded.university.domain.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

public interface FacultyService extends Service<Faculty, FacultyDto> {

    List<Faculty> getAllSortedByNameAsc();

    Page<Faculty> getAllSortedPaginated(Pageable pageable);
}