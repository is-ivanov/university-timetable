package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;

import java.util.List;

public interface DepartmentService extends Service<Department, DepartmentDto> {

//    List<DepartmentDto> getAllDtos();

    List<DepartmentDto> getAllByFaculty(int facultyId);

//    List<DepartmentDto> getAllDtosByFaculty(int facultyId);

//    DepartmentDto getDtoById(int id);
}