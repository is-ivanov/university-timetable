package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    @Autowired
    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public void add(Student student) {
        studentDao.add(student);
    }

    @Override
    public Student getById(int id) throws ServiceException {
        Student student;
        try {
            student = studentDao.getById(id).orElse(new Student());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return student;
    }

    @Override
    public List<Student> getAll() {
        return studentDao.getAll();
    }

    @Override
    public void update(Student student) {
        studentDao.update(student);
    }

    @Override
    public void delete(Student student) {
        studentDao.delete(student);
    }

    @Override
    public void deactivateStudent(Student student) {
        student.setActive(false);
        studentDao.update(student);
    }

    @Override
    public void activateStudent(Student student, Group group) {
        student.setActive(true);
        student.setGroup(group);
        studentDao.update(student);
    }

    @Override
    public Student transferStudentToGroup(Student student, Group group) {
        student.setGroup(group);
        studentDao.update(student);
        return student;
    }



}