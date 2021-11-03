package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyDao facultyDao;

    @Override
    public void add(Faculty faculty) {
        log.debug("Adding {}", faculty);
        facultyDao.add(faculty);
        log.info("{} added successfully", faculty);
    }

    @Override
    public Faculty getById(int id) {
        log.debug("Getting faculty by id({})", id);
        Faculty faculty = facultyDao.getById(id).orElse(new Faculty());
        log.info("Found {}", faculty);
        return faculty;
    }

    @Override
    public List<Faculty> getAll() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = facultyDao.getAll();
        log.info("Found {} faculties", faculties.size());
        return faculties;
    }

    @Override
    public void update(Faculty faculty) {
        log.debug("Updating {}", faculty);
        facultyDao.update(faculty);
        log.info("Update {}", faculty);
    }

    @Override
    public void delete(Faculty faculty) {
        log.debug("Deleting {}", faculty);
        facultyDao.delete(faculty);
        log.info("Delete {}", faculty);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting faculty id({})", id);
        facultyDao.delete(id);
        log.info("Delete faculty id({})", id);
    }

    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        log.debug("Getting all faculties sorted by name ascending");
        List<Faculty> faculties = facultyDao.getAllSortedByNameAsc();
        log.info("Found {} sorted faculties", faculties.size());
        return faculties;
    }

    @Override
    public Page<Faculty> getAllSortedPaginated(Pageable pageable) {
        log.debug("Getting sorted page {} from list of faculties", pageable.getPageNumber());
        Page<Faculty> pageFaculties = facultyDao.getAllSortedPaginated(pageable);
        log.info("Found {} faculties on page {}", pageFaculties.getContent().size(),
            pageFaculties.getNumber());
        return pageFaculties;
    }


}