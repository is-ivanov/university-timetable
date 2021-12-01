package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    @Override
    public void add(Student student) {
        log.debug("Adding student [{} {} {}, active={}, group {}]",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
        studentDao.add(student);
        log.info("Student [{} {} {}, active={}, group {}] added successfully",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
    }

    @Override
    public Student getById(int id) {
        log.debug("Getting student by id({})", id);
        Student student = studentDao.getById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Student id(%d) not found", id)));
        log.info("Found {}", student);
        return student;
    }

    @Override
    public List<Student> getAll() {
        log.debug("Getting all students");
        List<Student> students = studentDao.getAll();
        log.info("Found {} students", students.size());
        return students;
    }

    @Override
    public void update(Student student) {
        log.debug("Updating student [id={}, {} {} {}, active={}]",
            student.getId(), student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive());
        studentDao.update(student);
        log.info("Update student id({})", student.getId());
    }

    @Override
    public void delete(Student student) {
        log.debug("Deleting student [id={}, {} {} {}, active={}]",
            student.getId(), student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive());
        studentDao.delete(student);
        log.info("Delete student id({})", student.getId());
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting student id({})", id);
        studentDao.delete(id);
        log.info("Delete student id({})", id);
    }

    @Override
    public void deactivateStudent(Student student) {
        log.debug("Deactivating student [id={}, {} {} {}]", student.getId(),
            student.getFirstName(), student.getPatronymic(), student.getLastName());
        student.setActive(false);
        studentDao.update(student);
        log.info("Deactivate student id({})", student.getId());
    }

    @Override
    public void activateStudent(Student student, Group group) {
        log.debug("Activating student [id={}, {} {} {}]", student.getId(),
            student.getFirstName(), student.getPatronymic(), student.getLastName());
        student.setActive(true);
        student.setGroup(group);
        studentDao.update(student);
        log.info("Activate student id({})", student.getId());
    }

    @Override
    public Student transferStudentToGroup(Student student, Group group) {
        log.debug("Transferring student id({}) to group id({})",
            student.getId(), group.getId());
        student.setGroup(group);
        studentDao.update(student);
        log.info("Complete transfer student id({}) to group id({})",
            student.getId(), group.getId());
        return student;
    }

    @Override
    public List<Student> getStudentsByGroup(Group group) {
        log.debug("Getting all students from group ({})", group);
        List<Student> students = studentDao.getStudentsByGroup(group);
        log.info("Found {} students from group {}", students.size(), group);
        return students;
    }

    @Override
    public List<Student> getStudentsByGroup(int groupId) {
        log.debug("Getting all students from group id({})", groupId);
        Group group = new Group();
        group.setId(groupId);
        List<Student> students = studentDao.getStudentsByGroup(group);
        log.info("Found {} students from group id({})", students.size(), groupId);
        return students;
    }

    @Override
    public List<Student> getStudentsByFaculty(int facultyId) {
        log.debug("Getting all students from faculty id({})", facultyId);
        Faculty faculty = new Faculty(facultyId, null);
        List<Student> students = studentDao.getStudentsByFaculty(faculty);
        log.info("Found {} student from faculty id({})", students.size(), facultyId);
        return students;
    }

    @Override
    public List<Student> getAllActiveStudents() {
        log.debug("Getting all active students");
        List<Student> students = studentDao.getActiveStudents();
        log.info("Found {} students", students.size());
        return students;
    }

    @Override
    public List<Student> getFreeStudentsFromGroup(int groupId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, startTime, endTime);
        List<Student> freeStudents = studentDao.getFreeStudentsFromGroup(groupId,
            startTime, endTime);
        log.info("Found {} free student from group id({})", freeStudents.size(),
            groupId);
        return freeStudents;
    }

}