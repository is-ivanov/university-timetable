package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherDao teacherDao;
    private final TeacherDtoMapper teacherMapper;

    @Override
    public void add(Teacher teacher) {
        log.debug("Adding teacher [{} {} {}, active={}, department {}]",
            teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
        teacherDao.add(teacher);
        log.info("Teacher [{} {} {}, active={}, department {}] added " +
                "successfully", teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
    }

    @Override
    public Teacher getById(int id) {
        log.debug("Getting teacher by id({})", id);
        Teacher teacher = teacherDao.getById(id).orElse(new Teacher());
        log.info("Found {}", teacher);
        return teacher;
    }

    @Override
    public List<Teacher> getAll() {
        log.debug("Getting all teachers");
        List<Teacher> teachers = teacherDao.getAll();
        log.info("Found {} teachers", teachers.size());
        return teachers;
    }

    @Override
    public void update(Teacher teacher) {
        log.debug("Updating teacher [id={}, {} {} {}, active={}]",
            teacher.getId(), teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive());
        teacherDao.update(teacher);
        log.info("Update teacher id({})", teacher.getId());
    }

    @Override
    public void delete(Teacher teacher) {
        log.debug("Deleting teacher [id={}, {} {} {}, active={}]",
            teacher.getId(), teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive());
        teacherDao.delete(teacher);
        log.info("Delete teacher id({})", teacher.getId());
    }

    @Override
    public void deactivateTeacher(Teacher teacher) {
        log.debug("Deactivating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(false);
        teacherDao.update(teacher);
        log.info("Deactivate teacher id({})", teacher.getId());
    }

    @Override
    public void activateTeacher(Teacher teacher) {
        log.debug("Activating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(true);
        teacherDao.update(teacher);
        log.info("Activate teacher id({})", teacher.getId());
    }

    @Override
    public Teacher transferTeacherToDepartment(Teacher teacher,
                                               Department department) {
        log.debug("Transferring teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        teacher.setDepartment(department);
        teacherDao.update(teacher);
        log.info("Complete transfer teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        return teacher;
    }

    @Override
    public List<Teacher> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers from department id({})", departmentId);
        List<Teacher> teachers = teacherDao.getAllByDepartment(departmentId);
        log.info("Found {} teachers from department id({})", teachers.size(), departmentId);
        return teachers;
    }

    @Override
    public List<Teacher> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers from faculty id({})", facultyId);
        List<Teacher> teachers = teacherDao.getAllByFaculty(facultyId);
        log.info("Found {} teachers from faculty id({})", teachers.size(), facultyId);
        return teachers;
    }

    @Override
    public List<TeacherDto> convertListTeachersToDtos(List<Teacher> teachers) {
        log.debug("convert list teachers to list teacherDTOs");
        return teacherMapper.teachersToTeacherDtos(teachers);
    }

}
