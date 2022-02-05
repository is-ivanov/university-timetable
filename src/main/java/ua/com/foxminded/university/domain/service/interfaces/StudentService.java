package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentService extends Service<Student> {

    void deactivateStudent(Student student);

    void activateStudent(Student student, Group group);

    Student transferStudentToGroup(Student student, Group group);

    List<StudentDto> getStudentsByGroup(Group group);

    List<StudentDto> getStudentsByGroup(int groupId);

    List<StudentDto> getStudentsByFaculty(int facultyId);

    List<Student> getAllActiveStudents();

    List<Student> getFreeStudentsFromGroup(int groupId,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime);

    List<Integer> findIdsOfBusyStudentsOnTime(LocalDateTime startTime,
                                              LocalDateTime endTime);



//    List<Integer> getIdsFromStudents(List<Student> students);
}