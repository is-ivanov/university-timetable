package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.FacultyRepository;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/faculty-test-data.sql")
class FacultyJpaRepositoryTest extends IntegrationTestBase {

    public static final String MESSAGE_DELETE_FACULTY_NOT_FOUND =
        "Can't delete because faculty id(3) not found";

    @Autowired
    private FacultyRepository dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test faculty should CountRowsTable = 3")
        void testAddFaculty() {
            Faculty faculty = new Faculty(NAME_THIRD_FACULTY);
            int expectedRowsInTable = dao.countAll() + 1;

            dao.add(faculty);

            int actualRowsInTable = dao.countAll();
            assertThat(actualRowsInTable).isEqualTo(expectedRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id=1 should return faculty (1, 'Foreign Language')")
        void testGetByIdFaculty() {

            Optional<Faculty> facultyOptional = dao.getById(ID1);
            Faculty actualFaculty = facultyOptional.get();
            assertThat(actualFaculty.getId()).isEqualTo(ID1);
            assertThat(actualFaculty.getName()).isEqualTo(NAME_FIRST_FACULTY);
        }

        @Test
        @DisplayName("with id=3 should return empty Optional")
        void testGetByIdFacultyException() {
            Optional<Faculty> facultyOptional = dao.getById(ID3);
            assertThat(facultyOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllFaculties() {
            int expectedQuantityFaculties = dao.countAll();

            List<Faculty> actualFaculties = dao.getAll();
            assertThat(actualFaculties).hasSize(expectedQuantityFaculties);
            assertThat(actualFaculties).extracting(Faculty::getName)
                .contains(NAME_FIRST_FACULTY, NAME_SECOND_FACULTY);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with faculty id=1 should write new fields and" +
            " getById(1) return expected faculty")
        void testUpdateExistingFaculty_WriteNewFacultyName() {
            Faculty expectedFaculty = new Faculty(ID1, NAME_SECOND_FACULTY);

            dao.update(expectedFaculty);

            Optional<Faculty> facultyOptional = dao.getById(ID1);
            Faculty actualFaculty = facultyOptional.get();
            assertThat(actualFaculty).isEqualTo(expectedFaculty);
        }

        @Test
        @DisplayName("with faculty id=3 should write new faculty")
        void testUpdateNonExistingFaculty_CreateNewFaculty() {
            Faculty faculty = new Faculty(ID3, NAME_THIRD_FACULTY);

            dao.update(faculty);

            Optional<Faculty> facultyOptional = dao.getById(ID3);

            Faculty actualFaculty = facultyOptional.get();
            assertThat(actualFaculty).isEqualTo(faculty);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Nested
        @DisplayName("delete(faculty) method")
        class DeleteFacultyTest {

            @Test
            @DisplayName("with faculty id=2 should delete one record and number " +
                "records table should equals 1")
            void testDeleteExistingFaculty_ReduceNumberRowsInTable() {
                int expectedQuantityFaculties = dao.countAll() - 1;
                Faculty faculty = dao.getById(ID2).get();
                dao.delete(faculty);
                int actualQuantityFaculties = dao.countAll();
                assertThat(actualQuantityFaculties).isEqualTo(expectedQuantityFaculties);
            }
        }

        @Nested
        @DisplayName("delete(facultyId) method")
        class DeleteFacultyIdTest {

            @Test
            @DisplayName("with faculty id=2 should delete one record and number " +
                "records table should equals 1")
            void testDeleteExistingFacultyId2_ReduceNumberRowsInTable() {
                int expectedQuantityFaculties = dao.countAll() - 1;
                dao.delete(ID2);
                int actualQuantityFaculties = dao.countAll();
                assertThat(actualQuantityFaculties).isEqualTo(expectedQuantityFaculties);
            }

            @Test
            @DisplayName("with faculty id=3 should write new log.warn with " +
                "expected message")
            void testDeleteNonExistingFaculty_ExceptionWriteLogWarn() {
                assertThatThrownBy(() -> dao.delete(ID3))
                    .isInstanceOf(DaoException.class)
                    .hasMessageContaining(MESSAGE_DELETE_FACULTY_NOT_FOUND);
            }
        }
    }

    @Test
    @DisplayName("test 'getAllSortedByNameAsc' method")
    void testShouldReturnFacultiesInOrder() {
        List<Faculty> sortedFaculties = dao.getAllSortedByNameAsc();
        assertThat(sortedFaculties).extracting(Faculty::getName)
                .containsExactly(NAME_SECOND_FACULTY, NAME_FIRST_FACULTY);
    }

    @Nested
    @DisplayName("test 'getAllSortedPaginated' method")
    class GetAllSortedPaginatedTest {

        final int totalFaculties = 2;

        @Test
        @DisplayName("when size 1 and first page then return first one faculty")
        void testShouldReturnOneSortedFaculties() {
            int pageNumber = 0;
            int pageSize = 1;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Faculty> page = dao.getAllSortedPaginated(pageable);

            Faculty actualFaculty = page.getContent().get(0);


            assertThat(page.getTotalElements()).isEqualTo(totalFaculties);
            assertThat(page.getContent().size()).isEqualTo(pageSize);
            assertThat(actualFaculty.getId()).isEqualTo(ID2);
            assertThat(actualFaculty.getName()).isEqualTo(NAME_SECOND_FACULTY);
        }

        @Test
        @DisplayName("when pageable with sort by ID then return sorted by ID page")
        void test_Size1_WithSortingById() {
            int pageNumber = 0;
            int pageSize = 1;

            Sort sort = Sort.by(Sort.Direction.ASC, "faculty_id");
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<Faculty> page = dao.getAllSortedPaginated(pageable);

            Faculty actualFaculty = page.getContent().get(0);

            assertThat(page.getTotalElements()).isEqualTo(totalFaculties);
            assertThat(page.getContent().size()).isEqualTo(pageSize);
            assertThat(actualFaculty.getId()).isEqualTo(ID1);
            assertThat(actualFaculty.getName()).isEqualTo(NAME_FIRST_FACULTY);
        }

    }
}