package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.dao.interfaces.FacultyDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.springconfig.TestHibernateRootConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestHibernateRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    scripts = "/faculty-test-hibernate-data.sql")
@Transactional
class JpaFacultyDaoImplTest {

    @Autowired
    private FacultyDao jpaFacultyDaoImpl;

//    @BeforeEach
//    void setup() {
//        Faculty faculty1 = new Faculty(NAME_FIRST_FACULTY);
//        Faculty faculty2 = new Faculty(NAME_SECOND_FACULTY);
//
//        jpaFacultyDaoImpl.add(faculty1);
//        jpaFacultyDaoImpl.add(faculty2);
//    }

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test faculty should CountRowsTable = 3")
        void testAddFaculty() {
            Faculty faculty = createTestFacultyWithEmptyId();
            int expectedRowsInTable = jpaFacultyDaoImpl.countAll() + 1;

            jpaFacultyDaoImpl.add(faculty);

            int actualRowsInTable = jpaFacultyDaoImpl.countAll();
            assertThat(actualRowsInTable).isEqualTo(expectedRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id=1 should return faculty (1, 'Foreign Language')")
        void testGetByIdFaculty() {

            Optional<Faculty> facultyOptional = jpaFacultyDaoImpl.getById(ID1);
            Faculty actualFaculty = facultyOptional.get();
            assertThat(actualFaculty.getId()).isEqualTo(ID1);
            assertThat(actualFaculty.getName()).isEqualTo(NAME_FIRST_FACULTY);
        }

        @Test
        @DisplayName("with id=3 should return empty Optional")
        void testGetByIdFacultyException() {
            Optional<Faculty> facultyOptional = jpaFacultyDaoImpl.getById(ID3);
            assertThat(facultyOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllFaculties() {
            int expectedQuantityFaculties = jpaFacultyDaoImpl.countAll();

            List<Faculty> actualFaculties = jpaFacultyDaoImpl.getAll();
            assertThat(actualFaculties).hasSize(expectedQuantityFaculties);
            assertThat(actualFaculties).extracting(Faculty::getName)
                .contains(NAME_FIRST_FACULTY, NAME_SECOND_FACULTY);
        }
    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class UpdateTest {
//
//        @Test
//        @DisplayName("with faculty id=1 should write new fields and" +
//            " getById(1) return expected faculty")
//        void testUpdateExistingFaculty_WriteNewFacultyName() throws DaoException {
//            Faculty expectedFaculty = new Faculty(ID1, TEST_FACULTY_NAME);
//            dao.update(expectedFaculty);
//            Faculty actualFaculty = dao.getById(ID1).orElse(new Faculty());
//            assertEquals(expectedFaculty, actualFaculty);
//        }
//
//        @Test
//        @DisplayName("with faculty id=3 should write new log.warn with " +
//            "expected message")
//        void testUpdateNonExistingFaculty_ExceptionWriteLogWarn() {
//            LogCaptor logCaptor = LogCaptor.forClass(FacultyDaoImpl.class);
//            Faculty faculty = new Faculty(ID3, TEST_FACULTY_NAME);
//            String expectedLog = String.format(MESSAGE_UPDATE_MASK, faculty);
//            Exception ex = assertThrows(DaoException.class,
//                () -> dao.update(faculty));
//            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Nested
//        @DisplayName("delete(faculty) method")
//        class DeleteFacultyTest {
//
//            @Test
//            @DisplayName("with faculty id=2 should delete one record and number " +
//                "records table should equals 1")
//            void testDeleteExistingFaculty_ReduceNumberRowsInTable() {
//                int expectedQuantityFaculties = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                Faculty faculty = new Faculty(ID2, SECOND_FACULTY_NAME);
//                dao.delete(faculty);
//                int actualQuantityFaculties = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityFaculties, actualQuantityFaculties);
//            }
//
//            @Test
//            @DisplayName("with faculty id=3 should write new log.warn with " +
//                "expected message")
//            void testDeleteNonExistingFaculty_ExceptionWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(FacultyDaoImpl.class);
//                Faculty faculty = new Faculty(ID3, TEST_FACULTY_NAME);
//                String expectedLog = String.format(MESSAGE_DELETE_MASK, faculty);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(faculty));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//
//        }
//
//        @Nested
//        @DisplayName("delete(facultyId) method")
//        class DeleteFacultyIdTest {
//
//            @Test
//            @DisplayName("with faculty id=2 should delete one record and number " +
//                "records table should equals 1")
//            void testDeleteExistingFacultyId2_ReduceNumberRowsInTable() {
//                int expectedQuantityFaculties = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                dao.delete(ID2);
//                int actualQuantityFaculties = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityFaculties, actualQuantityFaculties);
//            }
//
//            @Test
//            @DisplayName("with faculty id=3 should write new log.warn with " +
//                "expected message")
//            void testDeleteNonExistingFaculty_ExceptionWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(FacultyDaoImpl.class);
//                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID3);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(ID3));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//
//        }
//    }
//
//    @Test
//    @DisplayName("test 'getAllSortedByNameAsc 'method")
//    void testShouldReturnFacultiesInOrder() {
//        List<Faculty> sortedFaculties = dao.getAllSortedByNameAsc();
//        assertEquals(SECOND_FACULTY_NAME, sortedFaculties.get(0).getName());
//        assertEquals(FIRST_FACULTY_NAME, sortedFaculties.get(1).getName());
//    }
//
//    @Nested
//    @DisplayName("test 'getAllSortedPaginated' method")
//    class GetAllSortedPaginatedTest {
//
//        long totalFaculties = 2L;
//
//        @Test
//        @DisplayName("when size 1 and first page then return first one faculty")
//        void testShouldReturnOneSortedFaculties() {
//            int pageNumber = 0;
//            int pageSize = 1;
//            Pageable pageable = PageRequest.of(pageNumber, pageSize);
//            Page<Faculty> page = dao.getAllSortedPaginated(pageable);
//
//            Faculty actualFaculty = page.getContent().get(0);
//
//            assertThat(page.getTotalElements(), is(equalTo(totalFaculties)));
//            assertThat(page.getContent().size(), is(equalTo(pageSize)));
//            assertThat(actualFaculty.getId(), is(equalTo(ID2)));
//            assertThat(actualFaculty.getName(), is(equalTo(SECOND_FACULTY_NAME)));
//        }
//
//        @Test
//        @DisplayName("when pageable with sort by ID then return sorted by ID page")
//        void test_Size1_WithSortingById() {
//            int pageNumber = 0;
//            int pageSize = 1;
//
//            Sort sort = Sort.by(Sort.Direction.ASC, "faculty_id");
//            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
//
//            Page<Faculty> page = dao.getAllSortedPaginated(pageable);
//
//            Faculty actualFaculty = page.getContent().get(0);
//
//            assertThat(page.getTotalElements(), is(equalTo(totalFaculties)));
//            assertThat(page.getContent().size(), is(equalTo(pageSize)));
//            assertThat(actualFaculty.getId(), is(equalTo(ID1)));
//            assertThat(actualFaculty.getName(), is(equalTo(FIRST_FACULTY_NAME)));
//        }
//
//    }
}