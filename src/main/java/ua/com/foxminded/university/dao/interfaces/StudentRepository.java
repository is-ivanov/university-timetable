package ua.com.foxminded.university.dao.interfaces;

import java.time.LocalDateTime;
import java.util.List;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;

public interface StudentRepository extends Repository<Student> {

    List<Student> getStudentsByLesson(Lesson lesson);

    List<Student> getStudentsByGroup(Group group);

    List<Student> getStudentsByFaculty(Faculty faculty);

    List<Student> getActiveStudents ();

    List<Student> getFreeStudentsFromGroup(int groupId,
                                           LocalDateTime startTime,
                                           LocalDateTime endTime);
}