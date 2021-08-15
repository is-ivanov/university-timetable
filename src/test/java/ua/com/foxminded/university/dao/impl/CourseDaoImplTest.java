package ua.com.foxminded.university.dao.impl;

import nl.altindag.log.LogCaptor;
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
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

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
    private static final int ID4 = 4;
    private static final String FIRST_COURSE_NAME = "English";
    private static final String MESSAGE_EXCEPTION = "Course id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
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
    class addTest {

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
    class getByIdTest {

        @Test
        @DisplayName("with id = 1 should return course (1, English)")
        void testGetByIdCourse() throws DAOException {
            Course expectedCourse = new Course(ID1, FIRST_COURSE_NAME);
            Course actualCourse = dao.getById(ID1).orElse(null);
            assertEquals(expectedCourse, actualCourse);
        }

        @Test
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdCourseException() throws DAOException {
            DAOException ex = assertThrows(DAOException.class,
                () -> dao.getById(ID4));
            assertEquals(MESSAGE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

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
    class updateTest {

        @Test
        @DisplayName("with course id=1 should write new name and getById(1) " +
            "return this name")
        void testUpdateExistingCourse_WriteNewName() throws DAOException {
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
            Exception ex = assertThrows(DAOException.class,
                () -> dao.update(course));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

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
            Exception ex = assertThrows(DAOException.class,
                () -> dao.delete(course));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }


}