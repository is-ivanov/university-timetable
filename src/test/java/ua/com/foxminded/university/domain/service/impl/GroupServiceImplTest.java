package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.GroupRepository;
import ua.com.foxminded.university.dao.StudentRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    public static final String NAME_GROUP = "Group name";

    @Mock
    private GroupRepository groupRepositoryMock;
    @Mock
    private StudentRepository studentRepositoryMock;
    @Mock
    private GroupDtoMapper mapperMock;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    @DisplayName("test 'save' when call add method then should call Repository once")
    void testSave_CallDaoOnce() {
        Group group = new Group();
        groupService.create(group);
        verify(groupRepositoryMock).save(group);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Group then method should " +
            "return this Group")
        void testReturnExpectedGroup() {
            Group group = createTestGroup();
            GroupDto groupDto = createTestGroupDto();

            when(groupRepositoryMock.findById(ID1)).thenReturn(Optional.of(group));
            when(mapperMock.toDto(group)).thenReturn(groupDto);

            assertThat(groupService.findById(ID1)).isEqualTo(groupDto);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should " +
            "return empty Group")
        void testReturnEmptyGroup() {
            when(groupRepositoryMock.findById(ID3)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> groupService.findById(ID3))
                .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Group id(3) not found");
        }
    }

    @Test
    @DisplayName("test 'getAll' when Repository return List groups then method " +
        "should return this List")
    void testGetAll_ReturnListGroups() {
        List<Group> groups = createTestGroups();
        List<GroupDto> groupDtos = createTestGroupDtos(FACULTY_ID1);

        when(groupRepositoryMock.findAll()).thenReturn(groups);
        when(mapperMock.toDtos(groups)).thenReturn(groupDtos);

        assertThat(groupService.findAll()).isEqualTo(groupDtos);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "groupDao once")
    void testDelete_CallDaoOnce() {
        int groupId = anyInt();
        groupService.delete(groupId);
        verify(groupRepositoryMock).deleteById(groupId);
    }

    @Nested
    @DisplayName("test 'deactivateGroup' method")
    class DeactivateGroupTest {

        @Test
        @DisplayName("should call groupDao.update once")
        void testCallGroupDaoOnce() {
            Group group = new Group();
            groupService.deactivateGroup(group);
            verify(groupRepositoryMock).save(group);
        }

        @Test
        @DisplayName("should update group with active = false")
        void testSetGroupActiveFalse() {
            Group group = new Group();
            groupService.deactivateGroup(group);
            ArgumentCaptor<Group> captor =
                ArgumentCaptor.forClass(Group.class);
            verify(groupRepositoryMock).save(captor.capture());
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
            verify(studentRepositoryMock, times(groups.size()))
                .findAllByGroup(any());
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
            when(studentRepositoryMock.findAllByGroup(any()))
                .thenReturn(studentsGroup1).thenReturn(studentsGroup2);
            groupService.joinGroups(groups, NAME_GROUP, new Faculty());
            verify(studentRepositoryMock,
                times(quantityStudentsInGroup1 + quantityStudentsInGroup2))
                .save(any());
        }
    }

    @Test
    @DisplayName("test 'getAllByFacultyId' when Repository return List groups then " +
        "method should return this List")
    void testGetAllByFacultyId_ReturnListGroups() {
        List<Group> groups = createTestGroups();
        List<GroupDto> groupDtos = createTestGroupDtos(FACULTY_ID1);

        when(groupRepositoryMock.findAllByFacultyId(FACULTY_ID1)).thenReturn(groups);
        when(mapperMock.toDtos(groups)).thenReturn(groupDtos);

        assertThat(groupService.getAllByFacultyId(FACULTY_ID1)).isEqualTo(groupDtos);

    }
}