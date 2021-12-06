package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

public interface DepartmentRepository extends Repository<Department> {

    List<Department> getAllByFacultyId(int facultyId);
}