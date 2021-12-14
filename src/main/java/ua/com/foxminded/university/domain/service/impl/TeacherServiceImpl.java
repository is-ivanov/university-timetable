package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.TeacherRepository;
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
    private final TeacherRepository teacherRepository;
    private final TeacherDtoMapper teacherDtoMapper;

    @Override
    public void add(Teacher teacher) {
        log.debug("Adding teacher [{} {} {}, active={}, department {}]",
            teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
        teacherRepository.save(teacher);
        log.debug("Teacher [{} {} {}, active={}, department {}] added " +
                "successfully", teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive(),
            teacher.getDepartment().getName());
    }

    @Override
    public TeacherDto getById(int id) {
        log.debug("Getting teacher by id({})", id);
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format(MESSAGE_TEACHER_NOT_FOUND, id)));
        log.debug("Found {}", teacher);
        return teacherDtoMapper.toTeacherDto(teacher);
    }

    @Override
    public List<TeacherDto> getAll() {
        log.debug("Getting all teachers");
        List<Teacher> teachers = teacherRepository.findAll();
        log.debug("Found {} teachers", teachers.size());
        return teacherDtoMapper.toTeacherDtos(teachers);
    }

    @Override
    public void update(Teacher teacher) {
        log.debug("Updating teacher [id={}, {} {} {}, active={}]",
            teacher.getId(), teacher.getFirstName(), teacher.getPatronymic(),
            teacher.getLastName(), teacher.isActive());
        teacherRepository.save(teacher);
        log.debug("Update teacher id({})", teacher.getId());
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting teacher id({})", id);
        teacherRepository.deleteById(id);
        log.debug("Delete teacher id({})", id);
    }

    @Override
    public void deactivateTeacher(Teacher teacher) {
        log.debug("Deactivating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(false);
        teacherRepository.save(teacher);
        log.debug("Deactivate teacher id({})", teacher.getId());
    }

    @Override
    public void activateTeacher(Teacher teacher) {
        log.debug("Activating teacher [id={}, {} {} {}]", teacher.getId(),
            teacher.getFirstName(), teacher.getPatronymic(), teacher.getLastName());
        teacher.setActive(true);
        teacherRepository.save(teacher);
        log.debug("Activate teacher id({})", teacher.getId());
    }

    @Override
    public Teacher transferTeacherToDepartment(Teacher teacher,
                                               Department department) {
        log.debug("Transferring teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        teacher.setDepartment(department);
        teacherRepository.save(teacher);
        log.debug("Complete transfer teacher id({}) to department id({})",
            teacher.getId(), department.getId());
        return teacher;
    }

    @Override
    public List<TeacherDto> getAllByDepartment(int departmentId) {
        log.debug("Getting all teachers from department id({})", departmentId);
        List<Teacher> teachers = teacherRepository.findAllByDepartmentId(departmentId);
        log.debug("Found {} teachers from department id({})", teachers.size(), departmentId);
        return teacherDtoMapper.toTeacherDtos(teachers);
    }

    @Override
    public List<TeacherDto> getAllByFaculty(int facultyId) {
        log.debug("Getting all teachers from faculty id({})", facultyId);
        List<Teacher> teachers = teacherRepository.findAllByFaculty(facultyId);
        log.debug("Found {} teachers from faculty id({})", teachers.size(), facultyId);
        return teacherDtoMapper.toTeacherDtos(teachers);
    }

    @Override
    public List<TeacherDto> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        log.debug("Getting active teachers free from {} to {}", startTime, endTime);
        List<Teacher> freeTeachers =
            teacherRepository.findFreeTeachersOnLessonTime(startTime, endTime);
        log.debug("Found {} active free teachers", freeTeachers.size());
        return teacherDtoMapper.toTeacherDtos(freeTeachers);
    }

}
