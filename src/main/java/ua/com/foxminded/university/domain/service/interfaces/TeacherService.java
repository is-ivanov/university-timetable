package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

public interface TeacherService extends Service<Teacher> {

    void deactivateTeacher(Teacher teacher);

    void activateTeacher(Teacher teacher);

    Teacher transferTeacherToDepartment(Teacher teacher, Department department);

}
