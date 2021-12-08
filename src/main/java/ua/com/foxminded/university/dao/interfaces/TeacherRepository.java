package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;
import java.util.List;

public interface TeacherRepository extends Repository<Teacher> {

    List<Teacher> getAllByDepartment(int departmentId);

    List<Teacher> getAllByFaculty(int facultyId);

    List<Teacher> getFreeTeachersOnLessonTime(LocalDateTime startTime,
                                              LocalDateTime endTime);
}
