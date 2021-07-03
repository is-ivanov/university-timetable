package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    private FacultyServiceImpl facultyService;

    @Mock
    private FacultyDao facultyDaoMock;

    @BeforeEach
    void setUp() {
        facultyService = new FacultyServiceImpl(facultyDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Faculty faculty = new Faculty();
        facultyService.add(faculty);
        verify(facultyDaoMock).add(faculty);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Faculty then method " +
            "should return this Faculty")
        void testReturnExpectedFaculty() throws Exception {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(1);
            expectedFaculty.setName("Faculty name");
            when(facultyDaoMock.getById(1))
                .thenReturn(Optional.of(expectedFaculty));
            assertEquals(expectedFaculty, facultyService.getById(1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should return" +
            " empty Faculty")
        void testReturnEmptyFaculty() throws Exception {
            Optional<Faculty> optional = Optional.empty();
            when(facultyDaoMock.getById(1)).thenReturn(optional);
            assertEquals(new Faculty(), facultyService.getById(1));
        }

        @Test
        @DisplayName("when Dao throw DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception {
            when(facultyDaoMock.getById(anyInt())).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> facultyService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List faculties then method " +
        "should return this List")
    void testGetAll_ReturnListFaculties() {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1);
        Faculty faculty2 = new Faculty();
        faculty2.setId(2);
        List<Faculty> expectedFaculties = new ArrayList<>();
        expectedFaculties.add(faculty1);
        expectedFaculties.add(faculty2);
        when(facultyDaoMock.getAll()).thenReturn(expectedFaculties);
        assertEquals(expectedFaculties, facultyService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "facultyDao once")
    void testUpdate_CallDaoOnce() {
        Faculty faculty = new Faculty();
        facultyService.update(faculty);
        verify(facultyDaoMock).update(faculty);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "facultyDao once")
    void testDelete_CallDaoOnce() {
        Faculty faculty = new Faculty();
        facultyService.delete(faculty);
        verify(facultyDaoMock).delete(faculty);
    }
}