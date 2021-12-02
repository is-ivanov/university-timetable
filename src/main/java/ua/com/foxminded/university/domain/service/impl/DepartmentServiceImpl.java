package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
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

    private final DepartmentDao departmentDao;
    private final DepartmentDtoMapper departmentDtoMapper;

    @Override
    public void add(Department department) {
        log.debug("Adding {}", department);
        departmentDao.add(department);
        log.info("{} added successfully", department);
    }

    @Override
    public DepartmentDto getById(int id) {
        log.debug("Getting department by id({})", id);
        Department department = departmentDao.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_DEPARTMENT_NOT_FOUND, id)));
        log.info("Found {}", department);
        return departmentDtoMapper.toDepartmentDto(department);
    }


    @Override
    public List<DepartmentDto> getAll() {
        log.debug("Getting all departments");
        List<Department> departments = departmentDao.getAll();
        log.info("Found {} departments", departments.size());
        return departmentDtoMapper.toDepartmentDtos(departments);
    }


    @Override
    public void update(Department department) {
        log.debug("Updating {}", department);
        departmentDao.update(department);
        log.info("Update {}", department);
    }

    @Override
    public void delete(Department department) {
        log.debug("Deleting {}", department);
        departmentDao.delete(department);
        log.info("Delete {}", department);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting department id({})", id);
        departmentDao.delete(id);
        log.info("Delete department id({})", id);
    }

    @Override
    public List<DepartmentDto> getAllByFaculty(int facultyId) {
        log.debug("Getting all departments from faculty id({})", facultyId);
        List<Department> departments = departmentDao.getAllByFacultyId(facultyId);
        log.info("Found {} departments", departments.size());
        return departmentDtoMapper.toDepartmentDtos(departments);
    }

}