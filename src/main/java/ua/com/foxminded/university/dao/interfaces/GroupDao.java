package ua.com.foxminded.university.dao.interfaces;

import ua.com.foxminded.university.domain.entity.Group;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupDao extends Dao<Group> {

    List<Group> getAllByFacultyId(int facultyId);

    List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                          LocalDateTime endTime);

}