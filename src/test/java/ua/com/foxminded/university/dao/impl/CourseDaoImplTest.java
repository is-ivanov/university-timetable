package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql",
        "/course-test-data.sql" })
class CourseDaoImplTest {

    private static final String TABLE_NAME = "courses";
    private static final String TEST_COURSE_NAME = "testName";
    private static final int FIRST_ID = 1;
    private static final String FIRST_COURSE_NAME = "English";

    @Autowired
    private JdbcTemplate JdbcTemplate;

    @Autowired
    CourseDaoImpl dao;

    @AfterEach
    void tearDown() throws Exception {
        JdbcTestUtils.deleteFromTables(JdbcTemplate, TABLE_NAME);
    }

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test course should CountRowsTable = 4")
        void testAddCourse() {
            Course course = new Course();
            course.setName(TEST_COURSE_NAME);

            dao.add(course);
            int expectedRowsInTable = 4;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(JdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {


        @Test
        @DisplayName("with id = 1 should return course (1, English)")
        void testGetByIdCourse() {
            Course expectedCourse = new Course(FIRST_ID, FIRST_COURSE_NAME);
            Course actualCourse = dao.getById(FIRST_ID).get();
            assertEquals(expectedCourse, actualCourse);
        }

        @Test
        @DisplayName("with id=4 should return 'Course not found: 4' and empty Optional")
        void testGetByIdCourseException() {

            Optional<Course> opt = dao.getById(4);
            assertFalse(opt.isPresent());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllCourses() {
            int expectedQuantityCourses = JdbcTestUtils
                    .countRowsInTable(JdbcTemplate, TABLE_NAME);
            int actualQuantityCourses = dao.getAll().size();
            assertEquals(expectedQuantityCourses, actualQuantityCourses);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update name course id=1 should write new name and getById(1) return this name")
        void testUpdateCourses() {
            Course course = new Course(FIRST_ID, TEST_COURSE_NAME);
            dao.update(course);
            String actualName = dao.getById(FIRST_ID).get().getName();
            assertEquals(TEST_COURSE_NAME, actualName);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete course id=1 should delete one record and number records table should equals 2")
        void testUpdateCourses() {
            int expectedQuantityCourses = JdbcTestUtils
                    .countRowsInTable(JdbcTemplate, TABLE_NAME) - 1;
            Course course = new Course(FIRST_ID, FIRST_COURSE_NAME);
            dao.delete(course);
            int actualQuantityCourses = JdbcTestUtils
                    .countRowsInTable(JdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityCourses, actualQuantityCourses);
        }
    }


}
