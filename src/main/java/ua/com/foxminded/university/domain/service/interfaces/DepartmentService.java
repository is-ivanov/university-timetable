package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

public interface DepartmentService extends Service<Department> {

    List<Department> getAllByFacultyId(int facultyId);
}