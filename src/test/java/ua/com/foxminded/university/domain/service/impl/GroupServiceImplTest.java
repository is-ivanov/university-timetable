package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.GroupDao;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    public static final String NAME_GROUP = "Group name";
    public static final String FACULTY_NAME = "Faculty name";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

    private GroupServiceImpl groupService;

    @Mock
    private GroupDao groupDaoMock;
    @Mock
    private StudentDao studentDaoMock;

    @BeforeEach
    void setUp() {
        groupService = new GroupServiceImpl(groupDaoMock, studentDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Group group = new Group();
        groupService.add(group);
        verify(groupDaoMock).add(group);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Group then method should " +
            "return this Group")
        void testReturnExpectedGroup() {
            Group expectedGroup = new Group();
            expectedGroup.setId(ID1);
            expectedGroup.setName(NAME_GROUP);
            expectedGroup.setActive(true);
            expectedGroup.setFaculty(new Faculty());
            when(groupDaoMock.getById(ID1)).thenReturn(Optional.of(expectedGroup));
            assertEquals(expectedGroup, groupService.getById(ID1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Group")
        void testReturnEmptyGroup() {
            Optional<Group> optional = Optional.empty();
            when(groupDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Group(), groupService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List groups then method " +
        "should return this List")
    void testGetAll_ReturnListGroups() {
        Faculty faculty = new Faculty();
        faculty.setId(ID1);
        Group group1 = new Group();
        group1.setId(ID1);
        group1.setFaculty(faculty);
        Group group2 = new Group();
        group2.setId(ID2);
        group2.setFaculty(faculty);
        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(group1);
        expectedGroups.add(group2);
        when(groupDaoMock.getAll()).thenReturn(expectedGroups);
        assertEquals(expectedGroups, groupService.getAll());
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
    class deactivateGroupTest {

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
    class joinGroupsTest {

        @Test
        @DisplayName("join 2 groups should return one group with expected " +
            "Name and Faculty")
        void testReturnGroupWithNameAndFaculty() {
            Group group1 = new Group();
            Group group2 = new Group();
            List<Group> groups = new ArrayList<>();
            groups.add(group1);
            groups.add(group2);
            Faculty expectedFaculty = new Faculty(ID1, FACULTY_NAME);
            Group expectedGroup = new Group();
            expectedGroup.setActive(true);
            expectedGroup.setFaculty(expectedFaculty);
            expectedGroup.setName(NAME_GROUP);
            Group actualGroup = groupService.joinGroups(groups, NAME_GROUP,
                expectedFaculty);
            assertEquals(expectedGroup, actualGroup);
        }

        @Test
        @DisplayName("join 2 groups should call groupDao with expected Group " +
            "in " +
            "parameter")
        void testCallGroupDaoAddWithExpectedParameter() {
            Group group1 = new Group();
            Group group2 = new Group();
            List<Group> groups = new ArrayList<>();
            groups.add(group1);
            groups.add(group2);
            Faculty expectedFaculty = new Faculty(ID1, FACULTY_NAME);
            Group expectedGroup = new Group();
            expectedGroup.setActive(true);
            expectedGroup.setFaculty(expectedFaculty);
            expectedGroup.setName(NAME_GROUP);
            groupService.joinGroups(groups, NAME_GROUP, expectedFaculty);
            verify(groupDaoMock).add(expectedGroup);
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
        Faculty faculty = new Faculty();
        faculty.setId(ID1);
        Group group1 = new Group();
        group1.setId(ID1);
        group1.setFaculty(faculty);
        Group group2 = new Group();
        group2.setId(ID2);
        group2.setFaculty(faculty);
        List<Group> expectedGroups = new ArrayList<>();
        expectedGroups.add(group1);
        expectedGroups.add(group2);
        when(groupDaoMock.getAllByFacultyId(ID1)).thenReturn(expectedGroups);
        assertEquals(expectedGroups, groupService.getAllByFacultyId(ID1));
    }
}