package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.FacultyRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    public static final String FIRST_FACULTY_NAME = "First Faculty";
    public static final String SECOND_FACULTY_NAME = "Second Faculty";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

    @Mock
    private FacultyRepository facultyRepoMock;

    @InjectMocks
    private FacultyServiceImpl facultyService;

    @Test
    @DisplayName("test 'create' when call add method then should call Repository once")
    void testCreate_CallDaoOnce() {
        Faculty faculty = new Faculty();
        facultyService.create(faculty);
        verify(facultyRepoMock).save(faculty);
    }

    @Nested
    @DisplayName("test 'findById' method")
    class FindByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Faculty then method " +
            "should return this Faculty")
        void testReturnExpectedFaculty() {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            when(facultyRepoMock.findById(ID1))
                .thenReturn(Optional.of(expectedFaculty));
            assertThat(facultyService.findById(ID1)).isEqualTo(expectedFaculty);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw" +
            " new MyEntityNotFoundException")
        void testReturnEmptyFaculty() {
            when(facultyRepoMock.findById(ID1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> facultyService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Faculty with id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'findAll' when Repository return List faculties then method " +
        "should return this List")
    void testFindAll_ReturnListFaculties() {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1);
        Faculty faculty2 = new Faculty();
        faculty2.setId(2);
        List<Faculty> expectedFaculties = new ArrayList<>();
        expectedFaculties.add(faculty1);
        expectedFaculties.add(faculty2);
        when(facultyRepoMock.findAll()).thenReturn(expectedFaculties);
        assertThat(facultyService.findAll()).isEqualTo(expectedFaculties);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "facultyDao once")
    void testDelete_CallDaoOnce() {
       int facultyId = anyInt();
        facultyService.delete(facultyId);
        verify(facultyRepoMock).deleteById(facultyId);
    }

    @Test
    @DisplayName("test 'getAllSortedByNameAsc' when Repository return List faculties" +
        " then method should return this List")
    void testGetAllSortedAscByName() {
        Faculty firstFaculty = new Faculty(ID1, FIRST_FACULTY_NAME);
        Faculty secondFaculty = new Faculty(ID2, SECOND_FACULTY_NAME);
        LinkedList<Faculty> facultiesFromDao = new LinkedList<>();
        facultiesFromDao.add(firstFaculty);
        facultiesFromDao.add(secondFaculty);
        when(facultyRepoMock.findAllByOrderByNameAsc()).thenReturn(facultiesFromDao);
        assertThat(facultyService.getAllSortedByNameAsc()).isEqualTo(facultiesFromDao);
    }
}