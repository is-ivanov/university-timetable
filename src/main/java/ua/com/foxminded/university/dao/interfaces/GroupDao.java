package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Group;

import java.util.List;

public interface GroupDao extends Dao<Group> {

    List<Group> getAllByFacultyId(int facultyId);
}