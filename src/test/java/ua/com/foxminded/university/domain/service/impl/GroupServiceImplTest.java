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
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

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
        void testReturnExpectedGroup() throws Exception {
            Group expectedGroup = new Group();
            expectedGroup.setId(1);
            expectedGroup.setName("Group name");
            expectedGroup.setActive(true);
            expectedGroup.setFaculty(new Faculty());
            when(groupDaoMock.getById(1)).thenReturn(Optional.of(expectedGroup));
            assertEquals(expectedGroup, groupService.getById(1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Group")
        void testReturnEmptyGroup() throws Exception {
            Optional<Group> optional = Optional.empty();
            when(groupDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Group(), groupService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao throw DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception {
            when(groupDaoMock.getById(anyInt())).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> groupService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List groups then method " +
        "should return this List")
    void testGetAll_ReturnListGroups() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        Group group1 = new Group();
        group1.setId(1);
        group1.setFaculty(faculty);
        Group group2 = new Group();
        group2.setId(2);
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
        void testSetGroupActiveFalse(){
            Group group = new Group();
            groupService.deactivateGroup(group);
            ArgumentCaptor<Group> argumentCaptor =
                ArgumentCaptor.forClass(Group.class);
            verify(groupDaoMock).update(argumentCaptor.capture());
            assertFalse(argumentCaptor.getValue().isActive());
        }
    }

    @Test
    void joinGroups() {
    }
}