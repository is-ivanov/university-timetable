package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;

import java.util.List;

public interface DepartmentService extends Service<Department> {

    List<DepartmentDto> getAllDtos();

    List<Department> getAllByFaculty(int facultyId);

    List<DepartmentDto> getAllDtosByFaculty(int facultyId);

    DepartmentDto getDtoById(int id);
}