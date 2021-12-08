package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.DepartmentRepository;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    public static final String MESSAGE_DEPARTMENT_NOT_FOUND =
        "Department id(%d) not found";

    private final DepartmentRepository departmentRepository;
    private final DepartmentDtoMapper departmentDtoMapper;

    @Override
    public void add(Department department) {
        log.debug("Adding {}", department);
        departmentRepository.add(department);
        log.debug("{} added successfully", department);
    }

    @Override
    public DepartmentDto getById(int id) {
        log.debug("Getting department by id({})", id);
        Department department = departmentRepository.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_DEPARTMENT_NOT_FOUND, id)));
        log.debug("Found {}", department);
        return departmentDtoMapper.toDepartmentDto(department);
    }


    @Override
    public List<DepartmentDto> getAll() {
        log.debug("Getting all departments");
        List<Department> departments = departmentRepository.getAll();
        log.debug("Found {} departments", departments.size());
        return departmentDtoMapper.toDepartmentDtos(departments);
    }


    @Override
    public void update(Department department) {
        log.debug("Updating {}", department);
        departmentRepository.update(department);
        log.debug("Update {}", department);
    }

    @Override
    public void delete(Department department) {
        log.debug("Deleting {}", department);
        departmentRepository.delete(department);
        log.debug("Delete {}", department);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting department id({})", id);
        departmentRepository.delete(id);
        log.debug("Delete department id({})", id);
    }

    @Override
    public List<DepartmentDto> getAllByFaculty(int facultyId) {
        log.debug("Getting all departments from faculty id({})", facultyId);
        List<Department> departments = departmentRepository.getAllByFacultyId(facultyId);
        log.debug("Found {} departments", departments.size());
        return departmentDtoMapper.toDepartmentDtos(departments);
    }

}