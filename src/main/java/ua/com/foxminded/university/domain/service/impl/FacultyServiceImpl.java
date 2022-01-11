package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ua.com.foxminded.university.dao.FacultyRepository;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.mapper.FacultyDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepo;
    private final FacultyDtoMapper facultyMapper;

    @Override
    public Faculty save(Faculty faculty) {
        log.debug("Saving {}", faculty);
        return facultyRepo.save(faculty);
    }

    @Override
    public FacultyDto getById(int id) {
        log.debug("Getting faculty by id({})", id);
        Faculty faculty = facultyRepo.findById(id)
            .orElseThrow(() -> new MyEntityNotFoundException(
                "faculty", "id", id));
        log.debug("Found {}", faculty);
        return facultyMapper.toFacultyDto(faculty);
    }

    @Override
    public List<FacultyDto> getAll() {
        log.debug("Getting all faculties");
        List<Faculty> faculties = facultyRepo.findAll();
        log.debug("Found {} faculties", faculties.size());
        return facultyMapper.toFacultyDtos(faculties);
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