package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherDao teacherDao;

    @Autowired
    public TeacherServiceImpl(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    @Override
    public void add(Teacher teacher) {
        teacherDao.add(teacher);
    }

    @Override
    public Teacher getById(int id) throws ServiceException {
        Teacher teacher;
        try {
            teacher = teacherDao.getById(id).orElse(new Teacher());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return teacher;
    }

    @Override
    public List<Teacher> getAll() {
        return teacherDao.getAll();
    }

    @Override
    public void update(Teacher teacher) {
        teacherDao.update(teacher);
    }

    @Override
    public void delete(Teacher teacher) {
        teacherDao.delete(teacher);
    }

    @Override
    public void deactivateTeacher(Teacher teacher) {
        teacher.setActive(false);
        teacherDao.update(teacher);
    }

    @Override
    public void activateTeacher(Teacher teacher) {
        teacher.setActive(true);
        teacherDao.update(teacher);
    }

    @Override
    public Teacher transferTeacherToDepartment(Teacher teacher,
            Department department) {
        teacher.setDepartment(department);
        teacherDao.update(teacher);
        return teacher;
    }

}
