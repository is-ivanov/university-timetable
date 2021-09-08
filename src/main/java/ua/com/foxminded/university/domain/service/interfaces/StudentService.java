package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentService extends Service<Student> {

    void deactivateStudent(Student student);

    void activateStudent(Student student, Group group);

    Student transferStudentToGroup(Student student, Group group);

    List<Student> getStudentsByGroup(Group group);

    List<Student> getStudentsByGroup(int id);

    List<Student> getStudentsByFaculty(int facultyId);

    List<Student> getAllActiveStudents();

    List<Student> getFreeStudentsFromGroup(int groupId,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime);
}