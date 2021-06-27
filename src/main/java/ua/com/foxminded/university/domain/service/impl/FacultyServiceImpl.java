package ua.com.foxminded.university.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

@Service
public class FacultyServiceImpl implements FacultyService {

    private FacultyDao facultyDao;

    @Autowired
    public FacultyServiceImpl(FacultyDao facultyDao) {
        this.facultyDao = facultyDao;
    }

    @Override
    public void add(Faculty faculty) {
        facultyDao.add(faculty);
    }

    @Override
    public Faculty getById(int id) throws ServiceException {
        Faculty faculty = null;
        try {
            faculty = facultyDao.getById(id).orElse(new Faculty());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return faculty;
    }

    @Override
    public List<Faculty> getAll() {
        return facultyDao.getAll();
    }

    @Override
    public void update(Faculty faculty) {
        facultyDao.update(faculty);
    }

    @Override
    public void delete(Faculty faculty) {
        facultyDao.delete(faculty);
    }

}
