package ua.com.foxminded.university.domain.service.impl;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.DepartmentRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DepartmentServiceImpl extends AbstractService<Department> implements DepartmentService {

    private final DepartmentRepository departmentRepo;
    private final FacultyService facultyService;

    @Override
    public Department update(int id, Department department) {
        Preconditions.checkNotNull(department);
        Department existingDepartment = findById(id);
        existingDepartment.setName(department.getName());
        Integer existingFacultyId = existingDepartment.getFaculty().getId();
        Integer newFacultyId = department.getFaculty().getId();
        if (newFacultyId != null && !newFacultyId.equals(existingFacultyId)) {
            Faculty newFaculty = facultyService.findById(newFacultyId);
            existingDepartment.setFaculty(newFaculty);
        }
        return departmentRepo.save(existingDepartment);
    }

    @Override
    protected JpaRepository<Department, Integer> getRepo() {
        return departmentRepo;
    }

    @Override
    protected String getEntityName() {
        return Department.class.getSimpleName();
    }

    @Override
    public List<Department> getAllByFaculty(int facultyId) {
        List<Department> departments = departmentRepo.findAllByFacultyId(facultyId);
        log.debug("Found {} departments", departments.size());
        return departments;
    }

}