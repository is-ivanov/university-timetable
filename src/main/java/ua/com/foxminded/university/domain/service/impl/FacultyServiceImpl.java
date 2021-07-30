package ua.com.foxminded.university.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyDao facultyDao;

    @Autowired
    public FacultyServiceImpl(FacultyDao facultyDao) {
        this.facultyDao = facultyDao;
    }

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
    public List<Faculty> getAllSortedAscByName() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = facultyDao.getAll();
        log.info("Found {} faculties", faculties.size());
        faculties.sort(Comparator.comparing(Faculty::getName));
        log.info("Faculties sorted");
        return faculties;
    }
}