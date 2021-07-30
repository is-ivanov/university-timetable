package ua.com.foxminded.university.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.DepartmentDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;

import java.util.List;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public void add(Department department) {
        log.debug("Adding {}", department);
        departmentDao.add(department);
        log.info("{} added successfully", department);
    }

    @Override
    public Department getById(int id) {
        log.debug("Getting department by id({})", id);
        Department department = departmentDao.getById(id)
                .orElse(new Department());
        log.info("Found {}", department);
        return department;
    }

    @Override
    public List<Department> getAll() {
        log.debug("Getting all departments");
        List<Department> departments = departmentDao.getAll();
        log.info("Found {} departments", departments.size());
        return departments;
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
    public List<Department> getAllByFacultyId(int facultyId) {
        log.debug("Getting all departments from faculty id({})", facultyId);
        List<Department> departments = departmentDao.getAllByFacultyId(facultyId);
        log.info("Found {} departments", departments.size());
        return departments;
    }
}