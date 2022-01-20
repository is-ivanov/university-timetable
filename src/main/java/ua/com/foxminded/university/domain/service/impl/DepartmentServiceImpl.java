package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.DepartmentRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.mapper.DepartmentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DepartmentServiceImpl extends AbstractService<Department> implements DepartmentService {

    public static final String MESSAGE_DEPARTMENT_NOT_FOUND =
        "Department id(%d) not found";

    private final DepartmentRepository departmentRepo;
    private final DepartmentDtoMapper departmentDtoMapper;

//    @Override
//    public Department save(Department department) {
//        log.debug("Saving {}", department);
//        return departmentRepo.save(department);
//    }
//
//    @Override
//    public Department getById(int id) {
//        log.debug("Getting department by id({})", id);
//        Department department = departmentRepo.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException(
//                String.format(MESSAGE_DEPARTMENT_NOT_FOUND, id)));
//        log.debug("Found {}", department);
//        return department;
//    }
//
//
//    @Override
//    public List<Department> getAll() {
//        log.debug("Getting all departments");
//        List<Department> departments = departmentRepo.findAll();
//        log.debug("Found {} departments", departments.size());
//        return departments;
//    }
//
//    @Override
//    public void delete(int id) {
//        log.debug("Deleting department id({})", id);
//        departmentRepo.deleteById(id);
//        log.debug("Delete department id({})", id);
//    }

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