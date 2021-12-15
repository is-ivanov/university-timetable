package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.StudentRepository;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepo;
    private final StudentDtoMapper studentDtoMapper;

    @Override
    public void save(Student student) {
        log.debug("Saving student {}", student);
        studentRepo.save(student);
        log.debug("Student [{} {} {}, active={}, group {}] saved successfully",
            student.getFirstName(), student.getPatronymic(),
            student.getLastName(), student.isActive(),
            student.getGroup().getName());
    }

    @Override
    public StudentDto getById(int id) {
        log.debug("Getting student by id({})", id);
        Student student = studentRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Student id(%d) not found", id)));
        log.debug("Found {}", student);
        return studentDtoMapper.toStudentDto(student);
    }

    @Override
    public List<StudentDto> getAll() {
        log.debug("Getting all students");
        List<Student> students = studentRepo.findAll();
        log.debug("Found {} students", students.size());
        return studentDtoMapper.toStudentDtos(students);

    }

    @Override
    public void delete(int id) {
        log.debug("Deleting student id({})", id);
        studentRepo.deleteById(id);
        log.debug("Delete student id({})", id);
    }

    @Override
    public void deactivateStudent(Student student) {
        log.debug("Deactivating student [id={}, {} {} {}]", student.getId(),
            student.getFirstName(), student.getPatronymic(), student.getLastName());
        student.setActive(false);
        studentRepo.save(student);
        log.debug("Deactivate student id({})", student.getId());
    }

    @Override
    public void activateStudent(Student student, Group group) {
        log.debug("Activating student [id={}, {} {} {}]", student.getId(),
            student.getFirstName(), student.getPatronymic(), student.getLastName());
        student.setActive(true);
        student.setGroup(group);
        studentRepo.save(student);
        log.debug("Activate student id({})", student.getId());
    }

    @Override
    public Student transferStudentToGroup(Student student, Group group) {
        log.debug("Transferring student id({}) to group id({})",
            student.getId(), group.getId());
        student.setGroup(group);
        studentRepo.save(student);
        log.debug("Complete transfer student id({}) to group id({})",
            student.getId(), group.getId());
        return student;
    }

    @Override
    public List<StudentDto> getStudentsByGroup(Group group) {
        log.debug("Getting all students from group ({})", group);
        List<Student> students = studentRepo.findAllByGroup(group);
        log.debug("Found {} students from group {}", students.size(), group);
        return studentDtoMapper.toStudentDtos(students);
    }

    @Override
    public List<StudentDto> getStudentsByGroup(int groupId) {
        log.debug("Getting all students from group id({})", groupId);
        Group group = new Group();
        group.setId(groupId);
        List<Student> students = studentRepo.findAllByGroup(group);
        log.debug("Found {} students from group id({})", students.size(), groupId);
        return studentDtoMapper.toStudentDtos(students);
    }

    @Override
    public List<StudentDto> getStudentsByFaculty(int facultyId) {
        log.debug("Getting all students from faculty id({})", facultyId);
        Faculty faculty = new Faculty(facultyId, null);
        List<Student> students = studentRepo.findAllByFaculty(faculty);
        log.debug("Found {} student from faculty id({})", students.size(), facultyId);
        return studentDtoMapper.toStudentDtos(students);
    }

    @Override
    public List<Student> getAllActiveStudents() {
        log.debug("Getting all active students");
        List<Student> students = studentRepo.findAllByActiveTrue();
        log.debug("Found {} students", students.size());
        return students;
    }

    @Override
    public List<StudentDto> getFreeStudentsFromGroup(int groupId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        log.debug("Getting active students from group id({}) free from {} to {}",
            groupId, startTime, endTime);
        List<Student> freeStudents = studentRepo.findFreeStudentsFromGroup(groupId,
            startTime, endTime);
        log.debug("Found {} free student from group id({})", freeStudents.size(),
            groupId);
        return studentDtoMapper.toStudentDtos(freeStudents);
    }

}