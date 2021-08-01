package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.List;

public interface FacultyDao extends Dao<Faculty> {

    List<Faculty> getAllSortedByNameAsc();
}