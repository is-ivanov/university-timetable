package ua.com.foxminded.university.domain.service.interfaces;

import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupService extends Service<Group> {

    List<GroupDto> getAllDtos();

    GroupDto getDtoById(int groupId);

    void deactivateGroup(Group group);

    Group joinGroups(List<Group> groups, String nameNewGroup,
                     Faculty facultyNewGroup);

    List<Group> getAllByFacultyId(int facultyId);

    List<GroupDto> getAllDtosByFacultyId(int facultyId);

    List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                          LocalDateTime endTime);

    List<Group> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                   LocalDateTime startTime,
                                                   LocalDateTime endTime);

    List<Group> getActiveGroups();

}