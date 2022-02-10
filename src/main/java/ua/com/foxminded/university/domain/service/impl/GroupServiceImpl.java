package ua.com.foxminded.university.domain.service.impl;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.FacultyRepository;
import ua.com.foxminded.university.dao.GroupRepository;
import ua.com.foxminded.university.dao.StudentRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.StudentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
public class GroupServiceImpl extends AbstractService<Group> implements GroupService {

    public static final String FOUND_GROUPS = "Found {} groups";

    private final GroupRepository groupRepo;
    private final StudentRepository studentRepo;
    private final FacultyService facultyService;
    private final StudentService studentService;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepo, StudentRepository studentRepo,
                            FacultyService facultyService,
                            @Lazy StudentService studentService) {
        this.groupRepo = groupRepo;
        this.studentRepo = studentRepo;
        this.facultyService = facultyService;
        this.studentService = studentService;
    }

    @Override
    public Group update(int id, Group group) {
        Preconditions.checkNotNull(group);
        Group existingGroup = findById(id);
        String newGroupName = group.getName();
        existingGroup.setName(newGroupName);
        Integer existingFaultyId = existingGroup.getFaculty().getId();
        Integer newFacultyId = group.getFaculty().getId();
        if (newFacultyId != null && !newFacultyId.equals(existingFaultyId)) {
            Faculty newFaculty = facultyService.findById(newFacultyId);
            existingGroup.setFaculty(newFaculty);
        }
        existingGroup.setActive(group.isActive());

        return groupRepo.save(existingGroup);
    }

    @Override
    protected JpaRepository<Group, Integer> getRepo() {
        return groupRepo;
    }

    @Override
    protected String getEntityName() {
        return Group.class.getSimpleName();
    }

    @Override
    public void deactivateGroup(Group group) {
        log.debug("Deactivating {}", group);
        group.setActive(false);
        groupRepo.save(group);
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
        groupRepo.save(newGroup);
        log.debug("Replace all students from groups id ({}) into new {}",
            groupsId, newGroup);
        groups.forEach(group -> {
            studentRepo.findAllByGroup(group).forEach(student -> {
                student.setGroup(newGroup);
                studentRepo.save(student);
            });
            log.debug("Deactivating former {}", group);
            deactivateGroup(group);
        });
        log.debug("Create new {}", newGroup);
        return newGroup;
    }

    @Override
    public List<Group> getAllByFacultyId(int facultyId) {
        log.debug("Getting all groups by faculty id({})", facultyId);
        List<Group> groups = groupRepo.findAllByFacultyId(facultyId);
        log.debug(FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<Group> getFreeGroupsOnLessonTime(LocalDateTime startTime,
                                                 LocalDateTime endTime) {
        log.debug("Getting groups free from {} to {}", startTime, endTime);
        List<Integer> busyStudentIds =
            studentService.findIdsOfBusyStudentsOnTime(startTime, endTime);
        List<Group> groups;
        if (busyStudentIds.isEmpty()) {
            groups = groupRepo.findAllByActiveTrue();
        } else {
            groups = groupRepo.findAllActiveWithoutStudents(busyStudentIds);
        }
        log.debug(FOUND_GROUPS, groups.size());
        return groups;
    }

    @Override
    public List<Group> getFreeGroupsByFacultyOnLessonTime(int facultyId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        log.debug("Getting active groups from faculty id({}) free from {} to {}",
            facultyId, startTime, endTime);
        List<Integer> busyStudentIds =
            studentService.findIdsOfBusyStudentsOnTime(startTime, endTime);
        List<Group> groups;
        if (busyStudentIds.isEmpty()) {
            groups = groupRepo.findAllByActiveTrueAndFaculty_IdOrderByNameAsc(facultyId);
        } else {
            groups = groupRepo.findAllActiveWithoutStudentsByFaculty(busyStudentIds,
                facultyId);
        }
        log.debug(FOUND_GROUPS, groups.size());
        return groups;
    }

}