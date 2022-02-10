package ua.com.foxminded.university.domain.service.impl;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.TeacherRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.domain.util.EntityUtil;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TeacherServiceImpl extends AbstractService<Teacher> implements TeacherService {

    private final TeacherRepository teacherRepo;
    private final DepartmentService departmentService;

    @Override
    public Teacher create(Teacher teacher) {
        Integer departmentId = teacher.getDepartment().getId();
        Department department = departmentService.findById(departmentId);
        teacher.setDepartment(department);
        return super.create(teacher);
    }

    @Override
    public Teacher update(int id, Teacher teacher) {
        Preconditions.checkNotNull(teacher);
        Integer newDepartmentId = teacher.getDepartment().getId();
        Department newDepartment = departmentService.findById(newDepartmentId);

        Teacher existingTeacher = findById(id);
        existingTeacher.setFirstName(teacher.getFirstName());
        existingTeacher.setPatronymic(teacher.getPatronymic());
        existingTeacher.setLastName(teacher.getLastName());
        existingTeacher.setActive(teacher.isActive());
        existingTeacher.setDepartment(newDepartment);
        return teacherRepo.save(existingTeacher);
    }

    @Override
    protected JpaRepository<Teacher, Integer> getRepo() {
        return teacherRepo;
    }

    @Override
    protected String getEntityName() {
        return Teacher.class.getSimpleName();
    }

    @Override
    public void deactivateTeacher(Teacher teacher) {
        log.debug("Deactivating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(false);
        teacherRepo.save(teacher);
        log.debug("Deactivate teacher id({})", teacher.getId());
    }

    @Override
    public void activateTeacher(Teacher teacher) {
        log.debug("Activating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(true);
        teacherRepo.save(teacher);
        log.debug("Activate teacher id({})", teacher.getId());
    }

    @Override
    public Teacher transferTeacherToDepartment(Teacher teacher,
                                               Department department) {
        log.debug("Transferring teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        teacher.setDepartment(department);
        teacherRepo.save(teacher);
        log.debug("Complete transfer teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        return teacher;
    }

    @Override
    public List<Teacher> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers from department id({})", departmentId);
        List<Teacher> teachers = teacherRepo.findAllByDepartmentId(departmentId);
        log.debug("Found {} teachers from department id({})", teachers.size(), departmentId);
        return teachers;
    }

    @Override
    public List<Teacher> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers from faculty id({})", facultyId);
        List<Teacher> teachers = teacherRepo.findByDepartment_Faculty_IdIs(facultyId);
        log.debug("Found {} teachers from faculty id({})", teachers.size(), facultyId);
        return teachers;
    }

    @Override
    public List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        log.debug("Getting active teachers free from {} to {}", startTime, endTime);
        List<Teacher> busyTeachers =
            teacherRepo.findBusyTeachersOnTime(startTime, endTime);
        List<Teacher> freeTeachers;
        if (busyTeachers.isEmpty()) {
            freeTeachers = findAll();
        } else {
            List<Integer> busyTeacherIds = EntityUtil.extractIdsFromEntities(busyTeachers);
            freeTeachers =
                teacherRepo.findByActiveIsTrueAndIdNotInOrderByLastNameAscFirstNameAsc(busyTeacherIds);
        }
        log.debug("Found {} active free teachers", freeTeachers.size());
        return freeTeachers;
    }

}
