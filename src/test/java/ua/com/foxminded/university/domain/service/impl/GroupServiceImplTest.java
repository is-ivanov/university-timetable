package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.GroupDtoMapper;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    public static final String NAME_GROUP = "Group name";

    @Mock
    private GroupDao groupDaoMock;
    @Mock
    private StudentDao studentDaoMock;
    @Mock
    private GroupDtoMapper mapperMock;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Group group = new Group();
        groupService.add(group);
        verify(groupDaoMock).add(group);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Group then method should " +
            "return this Group")
        void testReturnExpectedGroup() {
            Group group = createTestGroup();
            GroupDto groupDto = createTestGroupDto();

            when(groupDaoMock.getById(ID1)).thenReturn(Optional.of(group));
            when(mapperMock.toGroupDto(group)).thenReturn(groupDto);

            assertThat(groupService.getById(ID1)).isEqualTo(groupDto);
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Group")
        void testReturnEmptyGroup() {
            when(groupDaoMock.getById(ID3)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> groupService.getById(ID3))
                .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Group id(3) not found");
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List groups then method " +
        "should return this List")
    void testGetAll_ReturnListGroups() {
        List<Group> groups = createTestGroups();
        List<GroupDto> groupDtos = createTestGroupDtos(FACULTY_ID1);

        when(groupDaoMock.getAll()).thenReturn(groups);
        when(mapperMock.toGroupDtos(groups)).thenReturn(groupDtos);

        assertThat(groupService.getAll()).isEqualTo(groupDtos);
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "groupDao once")
    void testUpdate_CallDaoOnce() {
        Group group = new Group();
        groupService.update(group);
        verify(groupDaoMock).update(group);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "groupDao once")
    void testDelete_CallDaoOnce() {
        Group group = new Group();
        groupService.delete(group);
        verify(groupDaoMock).delete(group);
    }

    @Nested
    @DisplayName("test 'deactivateGroup' method")
    class DeactivateGroupTest {

        @Test
        @DisplayName("should call groupDao.update once")
        void testCallGroupDaoOnce() {
            Group group = new Group();
            groupService.deactivateGroup(group);
            verify(groupDaoMock).update(group);
        }

        @Test
        @DisplayName("should update group with active = false")
        void testSetGroupActiveFalse() {
            Group group = new Group();
            groupService.deactivateGroup(group);
            ArgumentCaptor<Group> captor =
                ArgumentCaptor.forClass(Group.class);
            verify(groupDaoMock).update(captor.capture());
            assertFalse(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'joinGroups' method")
    class JoinGroupsTest {

        @Test
        @DisplayName("join 2 groups should return one group with expected Name " +
            "and Faculty")
        void testReturnGroupWithNameAndFaculty() {
            List<Group> groups = createTestGroups();
            Faculty expectedFaculty = createTestFaculty();

            Group actualGroup = groupService.joinGroups(groups, NAME_GROUP,
                expectedFaculty);

            assertThat(actualGroup.getName()).isEqualTo(NAME_GROUP);
            assertThat(actualGroup.getFaculty()).isEqualTo(expectedFaculty);
        }

        @Test
        @DisplayName("should call studentDao.getStudentsByGroup as many group" +
            " as we join")
        void testCallStudentDaoGetStudentsByGroupTimes() {
            List<Group> groups = new ArrayList<>();
            groups.add(new Group());
            groups.add(new Group());
            groups.add(new Group());
            groupService.joinGroups(groups, NAME_GROUP, new Faculty());
            verify(studentDaoMock, times(groups.size()))
                .getStudentsByGroup(any());
        }

        @Test
        @DisplayName("should call studentDao.update as many students as we " +
            "have in joined groups")
        void testCallStudentDaoUpdateTimes() {
            int quantityStudentsInGroup1 = 4;
            int quantityStudentsInGroup2 = 2;
            Group group1 = new Group();
            Group group2 = new Group();
            List<Group> groups = new ArrayList<>();
            List<Student> studentsGroup1 = new ArrayList<>();
            for (int i = 0; i < quantityStudentsInGroup1; i++) {
                studentsGroup1.add(new Student());
            }
            List<Student> studentsGroup2 = new ArrayList<>();
            for (int i = 0; i < quantityStudentsInGroup2; i++) {
                studentsGroup2.add(new Student());
            }
            groups.add(group1);
            groups.add(group2);
            when(studentDaoMock.getStudentsByGroup(any()))
                .thenReturn(studentsGroup1).thenReturn(studentsGroup2);
            groupService.joinGroups(groups, NAME_GROUP, new Faculty());
            verify(studentDaoMock,
                times(quantityStudentsInGroup1 + quantityStudentsInGroup2))
                .update(any());
        }
    }

    @Test
    @DisplayName("test 'getAllByFacultyId' when Dao return List groups then " +
        "method should return this List")
    void testGetAllByFacultyId_ReturnListGroups() {
        List<Group> groups = createTestGroups();
        List<GroupDto> groupDtos = createTestGroupDtos(FACULTY_ID1);

        when(groupDaoMock.getAllByFacultyId(FACULTY_ID1)).thenReturn(groups);
        when(mapperMock.toGroupDtos(groups)).thenReturn(groupDtos);

        assertThat(groupService.getAllByFacultyId(FACULTY_ID1)).isEqualTo(groupDtos);

    }
}