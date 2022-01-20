package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherService extends Service<Teacher> {

    void deactivateTeacher(Teacher teacher);

    void activateTeacher(Teacher teacher);

    Teacher transferTeacherToDepartment(Teacher teacher, Department department);

    List<Teacher> getAllByDepartment(int departmentId);

    List<Teacher> getAllByFaculty(int facultyId);

    List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                              LocalDateTime endTime);
}