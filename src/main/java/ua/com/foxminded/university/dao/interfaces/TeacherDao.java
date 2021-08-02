package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Teacher;

import java.util.List;

public interface TeacherDao extends Dao<Teacher> {

    List<Teacher> getAllByDepartment(int departmentId);
}
