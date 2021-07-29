package ua.com.foxminded.university.domain.service.interfaces;

import java.util.List;

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;

public interface GroupService extends Service<Group> {

    void deactivateGroup(Group group);

    Group joinGroups(List<Group> groups, String nameNewGroup,
            Faculty facultyNewGroup);

    List<Group> getAllByFacultyId(int facultyId);
}