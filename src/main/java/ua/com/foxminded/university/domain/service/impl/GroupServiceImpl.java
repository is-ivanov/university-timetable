package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    public static final String FOUND_GROUPS = "Found {} groups";

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final GroupDtoMapper groupDtoMapper;

    @Override
    public void add(Group group) {
        log.debug("Adding {}", group);
        groupDao.add(group);
        log.info("{} added successfully", group);
    }

    @Override
    public Group getById(int id) {
        log.debug("Getting group by id({})", id);
        Group group = groupDao.getById(id).orElse(new Group());
        log.info("Found {}", group);
        return group;
    }

    @Override
    public GroupDto getDtoById(int groupId) {
        Group group = getById(groupId);
        return groupDtoMapper.toGroupDto(group);
    }

    @Override
    public List<Group> getAll() {
        log.debug("Getting all groups");
        List<Group> groups = groupDao.getAll();
        log.info(FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<GroupDto> getAllDtos() {
        List<Group> groups = getAll();
        return groupDtoMapper.toGroupDtos(groups);
    }

    @Override
    public void update(Group group) {
        log.debug("Updating {}", group);
        groupDao.update(group);
        log.info("Update {}", group);
    }

    @Override
    public void delete(Group group) {
        log.debug("Deleting {}", group);
        groupDao.delete(group);
        log.info("Delete {}", group);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting group id({})", id);
        groupDao.delete(id);
        log.info("Delete group id({})", id);
    }

    @Override
    public void deactivateGroup(Group group) {
        log.debug("Deactivating {}", group);
        group.setActive(false);
        groupDao.update(group);
        log.info("Deactivate {}", group);
    }

    @Override
    public Group joinGroups(List<Group> groups, String nameNewGroup,
                            Faculty facultyNewGroup) {
        String groupsId = Arrays.toString(groups.stream()
            .map(Group::getId)
            .toArray());
        log.debug("Joining groups with id{}", groupsId);
        Group newGroup = new Group();
        newGroup.setName(nameNewGroup);
        newGroup.setFaculty(facultyNewGroup);
        newGroup.setActive(true);
        log.debug("Saving new {} in DB", newGroup);
        groupDao.add(newGroup);
        log.debug("Replace all students from groups id ({}) into new {}",
            groupsId, newGroup);
        groups.forEach(group -> {
            studentDao.getStudentsByGroup(group).forEach(student -> {
                student.setGroup(newGroup);
                studentDao.update(student);
            });
            log.debug("Deactivating former {}", group);
            deactivateGroup(group);
        });
        log.info("Create new {}", newGroup);
        return newGroup;
    }

    @Override
    public List<Group> getAllByFacultyId(int facultyId) {
        log.debug("Getting all groups by faculty id({})", facultyId);
        List<Group> groups = groupDao.getAllByFacultyId(facultyId);
        log.info(FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<GroupDto> getAllDtosByFacultyId(int facultyId) {
        List<Group> groups = getAllByFacultyId(facultyId);
        return groupDtoMapper.toGroupDtos(groups);
    }

    @Override
    public List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        log.debug("Getting groups free from {} to {}", startTime, endTime);
        List<Group> groups = groupDao.getFreeGroupsOnLessonTime(startTime, endTime);
        log.info(FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<Group> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        log.debug("Getting active groups from faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Group> freeGroups = groupDao
            .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        log.info(FOUND_GROUPS, freeGroups.size());
        return freeGroups;
    }

    @Override
    public List<Group> getActiveGroups() {
        log.debug("Getting all active groups");
        List<Group> groups = groupDao.getActiveGroups();
        log.info(FOUND_GROUPS, groups.size());
        return groups;
    }
}