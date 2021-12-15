package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.FacultyRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private static final String MESSAGE_FACULTY_NOT_FOUND = "Faculty id(%d) not found";

    private final FacultyRepository facultyRepo;

    @Override
    public void save(Faculty faculty) {
        log.debug("Saving {}", faculty);
        facultyRepo.save(faculty);
        log.debug("{} saved successfully", faculty);
    }

    @Override
    public Faculty getById(int id) {
        log.debug("Getting faculty by id({})", id);
        Faculty faculty = facultyRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_FACULTY_NOT_FOUND, id)));
        log.debug("Found {}", faculty);
        return faculty;
    }

    @Override
    public List<Faculty> getAll() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = facultyRepo.findAll();
        log.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting faculty id({})", id);
        facultyRepo.deleteById(id);
        log.debug("Delete faculty id({})", id);
    }

    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        log.debug("Getting all faculties sorted by name ascending");
        List<Faculty> faculties = facultyRepo.findAllByOrderByNameAsc();
        log.debug("Found {} sorted faculties", faculties.size());
        return faculties;
    }

    @Override
    public Page<Faculty> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of faculties", pageable.getPageNumber());
        Page<Faculty> pageFaculties = facultyRepo.findAll(pageable);
        log.debug("Found {} faculties on page {}", pageFaculties.getContent().size(),
            pageFaculties.getNumber());
        return pageFaculties;
    }

}