package ua.com.foxminded.university.dao.impl;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql",
    "/course-test-data.sql"})
class CourseDaoImplTest {

    private static final String TABLE_NAME = "courses";
    private static final String TEST_COURSE_NAME = "testName";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;
    private static final int ID4 = 4;
    private static final String FIRST_COURSE_NAME = "English";
    private static final String SECOND_COURSE_NAME = "Java";
    private static final String THIRD_COURSE_NAME = "Chemistry";
    private static final String MESSAGE_EXCEPTION = "Course id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_DELETE_ID_MASK = "Can't delete course id(%s)";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete because " +
        "course id(4) not found";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update because " +
        "course id(4) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CourseDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test course should CountRowsTable = 4")
        void testAddCourse() {
            Course course = new Course();
            course.setName(TEST_COURSE_NAME);

            dao.add(course);
            int expectedRowsInTable = 4;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id = 1 should return course (1, English)")
        void testGetByIdCourse() throws DaoException {
            Course expectedCourse = new Course(ID1, FIRST_COURSE_NAME);
            Course actualCourse = dao.getById(ID1).orElse(null);
            assertEquals(expectedCourse, actualCourse);
        }

        @Test
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdCourseException() throws DaoException {
            DaoException ex = assertThrows(DaoException.class,
                () -> dao.getById(ID4));
            assertEquals(MESSAGE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllCourses() {
            int expectedQuantityCourses = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityCourses = dao.getAll().size();
            assertEquals(expectedQuantityCourses, actualQuantityCourses);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with course id=1 should write new name and getById(1) " +
            "return this name")
        void testUpdateExistingCourse_WriteNewName() throws DaoException {
            Course course = new Course(ID1, TEST_COURSE_NAME);
            dao.update(course);
            String actualName = dao.getById(ID1).orElse(new Course()).getName();
            assertEquals(TEST_COURSE_NAME, actualName);
        }

        @Test
        @DisplayName("with course id=4 should write new log.warn and throw " +
            "new DAOException")
        void testUpdateNonExistingCourse_ExceptionAndWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(CourseDaoImpl.class);
            Course course = new Course(ID4, TEST_COURSE_NAME);
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, course);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.update(course));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Nested
        @DisplayName("delete(course) method")
        class DeleteCourseTest {

            @Test
            @DisplayName("with course id=1 should delete one record and number " +
                "records table should equals 2")
            void testDeleteExistingCourse_ReduceNumberRowsInTable() {
                int expectedQuantityCourses = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                Course course = new Course(ID1, FIRST_COURSE_NAME);
                dao.delete(course);
                int actualQuantityCourses = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityCourses, actualQuantityCourses);
            }

            @Test
            @DisplayName("with course id=4 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingCourse_ExceptionAndWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(CourseDaoImpl.class);
                Course course = new Course(ID4, TEST_COURSE_NAME);
                String expectedLog = String.format(MESSAGE_DELETE_MASK, course);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(course));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }

        @Nested
        @DisplayName("delete(courseId) method")
        class DeleteCourseIdTest {

            @Test
            @DisplayName("with course id=1 should delete one record and number " +
                "records table should equals 2")
            void testDeleteExistingCourseId1_ReduceNumberRowsInTable() {
                int expectedQuantityCourses = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                dao.delete(ID1);
                int actualQuantityCourses = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityCourses, actualQuantityCourses);
            }

            @Test
            @DisplayName("with course id=4 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingCourse_ExceptionAndWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(CourseDaoImpl.class);
                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID4);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(ID4));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }

    }

    @Nested
    @DisplayName("test 'countAll' method")
    class CountAllTest {

        @Test
        @DisplayName("should return number 3")
        void testReturnCountAllRecords() {
            int expectedRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertThat(dao.countAll(), is(equalTo(expectedRows)));

        }
    }

    @Nested
    @DisplayName("test 'getAllSortedPaginated' method")
    class GetAllSortedPaginatedTest {
        long totalCourses = 3L;

        @Test
        @DisplayName("when pageable size = 1 without sort then return page with first record sorted by course name")
        void test_Size1WithoutSorting() {
            int pageNumber = 0;
            int pageSize = 1;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Course> page = dao.getAllSortedPaginated(pageable);

            assertThat(page.getSize(), is(equalTo(pageSize)));
            assertThat(page.getTotalElements(), is(equalTo(totalCourses)));
            assertThat(page.getNumber(), is(equalTo(pageNumber)));

            Course course = page.getContent().get(0);
            assertThat(course.getId(), is(equalTo(ID3)));
            assertThat(course.getName(), is(equalTo(THIRD_COURSE_NAME)));
        }

        @Test
        @DisplayName("when pageable with sort by ID then return sorted by ID page")
        void test_Size2WithSortingById() {
            int pageNumber = 0;
            int pageSize = 2;
            Sort sort = Sort.by(Sort.Direction.ASC, "course_id");
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<Course> page = dao.getAllSortedPaginated(pageable);

            assertThat(page.getSize(), is(equalTo(pageSize)));
            assertThat(page.getTotalElements(), is(equalTo(totalCourses)));
            assertThat(page.getNumber(), is(equalTo(pageNumber)));

            List<Course> courses = page.getContent();
            Course course1 = courses.get(0);
            Course course2 = courses.get(1);

            assertThat(course1.getId(), is(equalTo(ID1)));
            assertThat(course2.getId(), is(equalTo(ID2)));
            assertThat(course1.getName(), is(equalTo(FIRST_COURSE_NAME)));
            assertThat(course2.getName(), is(equalTo(SECOND_COURSE_NAME)));
        }
    }

}