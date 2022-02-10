package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.FacultyRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FacultyServiceImpl extends AbstractService<Faculty> implements FacultyService {

    private final FacultyRepository facultyRepo;

    @Override
    public List<Faculty> getAllSortedByNameAsc() {
        log.debug("Getting all faculties sorted by name ascending");
        return facultyRepo.findAllByOrderByNameAsc();
    }

    @Override
    protected JpaRepository<Faculty, Integer> getRepo() {
        return facultyRepo;
    }

    @Override
    protected String getEntityName() {
        return Faculty.class.getSimpleName();
    }

}