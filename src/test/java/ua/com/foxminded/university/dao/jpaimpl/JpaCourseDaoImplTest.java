package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/course-test-data.sql")
class JpaCourseDaoImplTest extends IntegrationTestBase {

    private static final String TEST_COURSE_NAME = "New course";
    @Autowired
    @Qualifier("jpaCourseDaoImpl")
    private CourseDao dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test course should CountRowsTable = 4")
        void testAddCourse() {
            Course course = new Course(TEST_COURSE_NAME);
            int expectedRowsInTable = dao.countAll() + 1;

            dao.add(course);

            int actualRowsInTable = dao.countAll();
            assertThat(actualRowsInTable).isEqualTo(expectedRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id = 1 should return course (1, English)")
        void testGetByIdCourse() {
            Optional<Course> courseOptional = dao.getById(ID1);
            Course course = courseOptional.get();
            assertThat(course.getId()).isEqualTo(ID1);
            assertThat(course.getName()).isEqualTo(NAME_FIRST_COURSE);
        }

        @Test
        @DisplayName("with id=4 should return empty Optional")
        void testGetByIdCourseException() {
            Optional<Course> courseOptional = dao.getById(4);
            assertThat(courseOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllCourses() {
            int expectedQuantityCourses = dao.countAll();
            List<Course> courses = dao.getAll();
            assertThat(courses).hasSize(expectedQuantityCourses);
            assertThat(courses).extracting(Course::getName)
                .contains(NAME_FIRST_COURSE, NAME_SECOND_COURSE, NAME_THIRD_COURSE);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with course id=1 should write new name and getById(1) " +
            "return this name")
        void testUpdateExistingCourse_WriteNewName() {
            Course course = new Course(ID1, TEST_COURSE_NAME);
            dao.update(course);
            String actualName = dao.getById(ID1).get().getName();
            assertThat(actualName).isEqualTo(TEST_COURSE_NAME);
        }

        @Test
        @DisplayName("with course id=4 should write new course")
        void testUpdateNonExistingCourse_CreateNewCourse() {
            Course course = new Course(4, TEST_COURSE_NAME);
            dao.update(course);

            Course actualCourse = dao.getById(4).get();
            assertThat(dao.getAll()).hasSize(4);
            assertThat(actualCourse).isEqualTo(course);
        }
    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Nested
//        @DisplayName("delete(course) method")
//        class DeleteCourseTest {
//
//            @Test
//            @DisplayName("with course id=1 should delete one record and number " +
//                "records table should equals 2")
//            void testDeleteExistingCourse_ReduceNumberRowsInTable() {
//                int expectedQuantityCourses = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                Course course = new Course(ID1, FIRST_COURSE_NAME);
//                dao.delete(course);
//                int actualQuantityCourses = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityCourses, actualQuantityCourses);
//            }
//
//            @Test
//            @DisplayName("with course id=4 should write new log.warn and throw " +
//                "new DAOException")
//            void testDeleteNonExistingCourse_ExceptionAndWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(CourseDaoImpl.class);
//                Course course = new Course(ID4, TEST_COURSE_NAME);
//                String expectedLog = String.format(MESSAGE_DELETE_MASK, course);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(course));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//        }
//
//        @Nested
//        @DisplayName("delete(courseId) method")
//        class DeleteCourseIdTest {
//
//            @Test
//            @DisplayName("with course id=1 should delete one record and number " +
//                "records table should equals 2")
//            void testDeleteExistingCourseId1_ReduceNumberRowsInTable() {
//                int expectedQuantityCourses = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//                dao.delete(ID1);
//                int actualQuantityCourses = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//                assertEquals(expectedQuantityCourses, actualQuantityCourses);
//            }
//
//            @Test
//            @DisplayName("with course id=4 should write new log.warn and throw " +
//                "new DAOException")
//            void testDeleteNonExistingCourse_ExceptionAndWriteLogWarn() {
//                LogCaptor logCaptor = LogCaptor.forClass(CourseDaoImpl.class);
//                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID4);
//                Exception ex = assertThrows(DaoException.class,
//                    () -> dao.delete(ID4));
//                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//            }
//        }
//
//    }
//
//    @Nested
//    @DisplayName("test 'countAll' method")
//    class CountAllTest {
//
//        @Test
//        @DisplayName("should return number 3")
//        void testReturnCountAllRecords() {
//            int expectedRows = JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME);
//            assertThat(dao.countAll(), is(equalTo(expectedRows)));
//
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllSortedPaginated' method")
//    class GetAllSortedPaginatedTest {
//
//        long totalCourses = 3L;
//
//        @Test
//        @DisplayName("when pageable size = 1 without sort then return page with first record sorted by course name")
//        void test_Size1WithoutSorting() {
//            int pageNumber = 0;
//            int pageSize = 1;
//            Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//            Page<Course> page = dao.getAllSortedPaginated(pageable);
//
//            assertThat(page.getSize(), is(equalTo(pageSize)));
//            assertThat(page.getTotalElements(), is(equalTo(totalCourses)));
//            assertThat(page.getNumber(), is(equalTo(pageNumber)));
//
//            Course course = page.getContent().get(0);
//            assertThat(course.getId(), is(equalTo(ID3)));
//            assertThat(course.getName(), is(equalTo(THIRD_COURSE_NAME)));
//        }
//
//        @Test
//        @DisplayName("when pageable with sort by ID then return sorted by ID page")
//        void test_Size2WithSortingById() {
//            int pageNumber = 0;
//            int pageSize = 2;
//            Sort sort = Sort.by(Sort.Direction.ASC, "course_id");
//            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
//
//            Page<Course> page = dao.getAllSortedPaginated(pageable);
//
//            assertThat(page.getSize(), is(equalTo(pageSize)));
//            assertThat(page.getTotalElements(), is(equalTo(totalCourses)));
//            assertThat(page.getNumber(), is(equalTo(pageNumber)));
//
//            List<Course> courses = page.getContent();
//            Course course1 = courses.get(0);
//            Course course2 = courses.get(1);
//
//            assertThat(course1.getId(), is(equalTo(ID1)));
//            assertThat(course2.getId(), is(equalTo(ID2)));
//            assertThat(course1.getName(), is(equalTo(FIRST_COURSE_NAME)));
//            assertThat(course2.getName(), is(equalTo(SECOND_COURSE_NAME)));
//        }
//    }
}