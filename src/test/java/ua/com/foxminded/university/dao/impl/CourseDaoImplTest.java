package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        "/course-test-data.sql" })
class CourseDaoImplTest {

    private static final String TABLE_NAME = "courses";
    private static final String TEST_COURSE_NAME = "testName";

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

}
