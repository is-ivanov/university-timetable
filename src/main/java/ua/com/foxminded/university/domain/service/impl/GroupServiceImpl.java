package ua.com.foxminded.university.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.GroupRepository;
import ua.com.foxminded.university.dao.interfaces.StudentRepository;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    public static final String FOUND_GROUPS = "Found {} groups";

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GroupDtoMapper groupDtoMapper;

    @Override
    public void add(Group group) {
        log.debug("Adding {}", group);
        groupRepository.save(group);
        log.debug("{} added successfully", group);
    }

    @Override
    public GroupDto getById(int id) {
        log.debug("Getting group by id({})", id);
        Group group = groupRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Group id(%d) not found", id)));
        log.debug("Found {}", group);
        return groupDtoMapper.toGroupDto(group);
    }

    @Override
    public List<GroupDto> getAll() {
        log.debug("Getting all groups");
        List<Group> groups = groupRepository.findAll();
        log.debug(FOUND_GROUPS, groups.size());
        return groupDtoMapper.toGroupDtos(groups);
    }

    @Override
    public void update(Group group) {
        log.debug("Updating {}", group);
        groupRepository.save(group);
        log.debug("Update {}", group);
    }

    @Override
    public void delete(Group group) {
        log.debug("Deleting {}", group);
        groupRepository.delete(group);
        log.debug("Delete {}", group);
    }

    @Override
    public void delete(int id) {
        log.debug("Deleting group id({})", id);
        groupRepository.deleteById(id);
        log.debug("Delete group id({})", id);
    }

    @Override
    public void deactivateGroup(Group group) {
        log.debug("Deactivating {}", group);
        group.setActive(false);
        groupRepository.save(group);
        log.debug("Deactivate {}", group);
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
        groupRepository.save(newGroup);
        log.debug("Replace all students from groups id ({}) into new {}",
            groupsId, newGroup);
        groups.forEach(group -> {
            studentRepository.findAllByGroup(group).forEach(student -> {
                student.setGroup(newGroup);
                studentRepository.save(student);
            });
            log.debug("Deactivating former {}", group);
            deactivateGroup(group);
        });
        log.debug("Create new {}", newGroup);
        return newGroup;
    }

    @Override
    public List<GroupDto> getAllByFacultyId(int facultyId) {
        log.debug("Getting all groups by faculty id({})", facultyId);
        List<Group> groups = groupRepository.findAllByFacultyId(facultyId);
        log.debug(FOUND_GROUPS, groups.size());
        return groupDtoMapper.toGroupDtos(groups);
    }

    @Override
    public List<GroupDto> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        log.debug("Getting groups free from {} to {}", startTime, endTime);
        List<Group> groups = groupRepository.findFreeGroupsOnLessonTime(startTime, endTime);
        log.debug(FOUND_GROUPS, groups.size());
        return groupDtoMapper.toGroupDtos(groups);
    }

    @Override
    public List<GroupDto> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        log.debug("Getting active groups from faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Group> freeGroups = groupRepository
            .findFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        log.debug(FOUND_GROUPS, freeGroups.size());
        return groupDtoMapper.toGroupDtos(freeGroups);
    }

    @Override
    public List<Group> getActiveGroups() {
        log.debug("Getting all active groups");
        List<Group> groups = groupRepository.findAllByActiveTrue();
        log.debug(FOUND_GROUPS, groups.size());
        return groups;
    }
}