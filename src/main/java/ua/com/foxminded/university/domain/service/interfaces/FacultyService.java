package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

public interface FacultyService extends Service<Faculty> {

    void delete(int facultyId);

    List<Faculty> getAllSortedByNameAsc();
}