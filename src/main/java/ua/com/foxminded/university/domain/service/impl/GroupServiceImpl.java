package ua.com.foxminded.university.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final StudentDao studentDao;

    @Autowired
    public GroupServiceImpl(GroupDao groupDao, StudentDao studentDao) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
    }

    @Override
    public void add(Group group) {
        groupDao.add(group);
    }

    @Override
    public Group getById(int id) throws ServiceException {
        Group group;
        try {
            group = groupDao.getById(id).orElse(new Group());
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
        return group;
    }

    @Override
    public List<Group> getAll() {
        return groupDao.getAll();
    }

    @Override
    public void update(Group group) {
        groupDao.update(group);
    }

    @Override
    public void delete(Group group) {
        groupDao.delete(group);
    }

    @Override
    public void deactivateGroup(Group group) {
        group.setActive(false);
        groupDao.update(group);
    }

    @Override
    public Group joinGroups(List<Group> groups, String nameNewGroup,
            Faculty facultyNewGroup) {
        Group newGroup = new Group();
        newGroup.setName(nameNewGroup);
        newGroup.setFaculty(facultyNewGroup);
        newGroup.setActive(true);
        groupDao.add(newGroup);
        groups.forEach(group -> {
            studentDao.getStudentsByGroup(group).forEach(student -> {
                student.setGroup(newGroup);
                studentDao.update(student);
            });
            deactivateGroup(group);
        });
        return newGroup;
    }


}