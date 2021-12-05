package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private static final String MESSAGE_TEACHER_NOT_FOUND = "Teacher id(%d) not found";
    private final TeacherDao teacherDao;
    private final TeacherDtoMapper teacherDtoMapper;

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
    public TeacherDto getById(int id) {
        log.debug("Getting teacher by id({})", id);
        Teacher teacher = teacherDao.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_TEACHER_NOT_FOUND, id)));
        log.info("Found {}", teacher);
        return teacherDtoMapper.toTeacherDto(teacher);
    }

    @Override
    public List<TeacherDto> getAll() {
        log.debug("Getting all teachers");
        List<Teacher> teachers = teacherDao.getAll();
        log.info("Found {} teachers", teachers.size());
        return teacherDtoMapper.toTeacherDtos(teachers);
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
    public void delete(int id) {
        log.debug("Deleting teacher id({})", id);
        teacherDao.delete(id);
        log.info("Delete teacher id({})", id);
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
    public List<TeacherDto> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers from department id({})", departmentId);
        List<Teacher> teachers = teacherDao.getAllByDepartment(departmentId);
        log.info("Found {} teachers from department id({})", teachers.size(), departmentId);
        return teacherDtoMapper.toTeacherDtos(teachers);
    }

    @Override
    public List<TeacherDto> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers from faculty id({})", facultyId);
        List<Teacher> teachers = teacherDao.getAllByFaculty(facultyId);
        log.info("Found {} teachers from faculty id({})", teachers.size(), facultyId);
        return teacherDtoMapper.toTeacherDtos(teachers);
    }

    @Override
    public List<TeacherDto> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        log.debug("Getting active teachers free from {} to {}", startTime, endTime);
        List<Teacher> freeTeachers =
            teacherDao.getFreeTeachersOnLessonTime(startTime, endTime);
        log.info("Found {} active free teachers", freeTeachers.size());
        return teacherDtoMapper.toTeacherDtos(freeTeachers);
    }

}
