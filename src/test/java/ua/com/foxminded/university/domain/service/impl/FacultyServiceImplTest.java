package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    public static final String FIRST_FACULTY_NAME = "First Faculty";
    public static final String SECOND_FACULTY_NAME = "Second Faculty";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

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
        void testReturnExpectedFaculty() {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);
            when(facultyDaoMock.getById(ID1))
                .thenReturn(Optional.of(expectedFaculty));
            assertEquals(expectedFaculty, facultyService.getById(ID1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should return" +
            " empty Faculty")
        void testReturnEmptyFaculty() {
            Optional<Faculty> optional = Optional.empty();
            when(facultyDaoMock.getById(ID1)).thenReturn(optional);
            assertEquals(new Faculty(), facultyService.getById(ID1));
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

    @Test
    @DisplayName("test 'getAllSortedByNameAsc' when Dao return List faculties" +
        " then method should return this List")
    void testGetAllSortedAscByName() {
        Faculty firstFaculty = new Faculty(ID1, FIRST_FACULTY_NAME);
        Faculty secondFaculty = new Faculty(ID2, SECOND_FACULTY_NAME);
        LinkedList<Faculty> facultiesFromDao = new LinkedList<>();
        facultiesFromDao.add(firstFaculty);
        facultiesFromDao.add(secondFaculty);
        when(facultyDaoMock.getAllSortedByNameAsc()).thenReturn(facultiesFromDao);
        assertEquals(facultiesFromDao, facultyService.getAllSortedByNameAsc());
    }
}