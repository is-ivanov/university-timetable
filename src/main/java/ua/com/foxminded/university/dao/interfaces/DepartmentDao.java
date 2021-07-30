package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

public interface DepartmentDao extends Dao<Department> {

    List<Department> getAllByFacultyId(int facultyId);
}