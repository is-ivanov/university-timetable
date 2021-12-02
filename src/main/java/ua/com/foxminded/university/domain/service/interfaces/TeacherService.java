package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherService extends Service<Teacher, TeacherDto> {

    void deactivateTeacher(Teacher teacher);

    void activateTeacher(Teacher teacher);

    Teacher transferTeacherToDepartment(Teacher teacher, Department department);

    List<TeacherDto> getAllByDepartment(int departmentId);

    List<TeacherDto> getAllByFaculty(int facultyId);

    List<TeacherDto> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                              LocalDateTime endTime);
}
