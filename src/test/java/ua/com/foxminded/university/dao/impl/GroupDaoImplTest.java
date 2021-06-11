package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/group-test-data.sql" })
class GroupDaoImplTest {

    private static final String TABLE_NAME = "groups";
    private static final String TEST_GROUP_NAME = "GroupName";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;
    private static final String FIRST_GROUP_NAME = "20Eng-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Group not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    GroupDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test group should CountRowsTable = 3")
        void testAddGroup() {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
            faculty.setName(TEST_FACULTY_NAME);

            Group group = new Group();
            group.setName(TEST_GROUP_NAME);
            group.setFaculty(faculty);

            dao.add(group);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return group (1, '20Eng-1', faculty id=1)")
        void testGetByIdGroup() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group expectedGroup = new Group(FIRST_ID, FIRST_GROUP_NAME,
                    expectedFaculty);

            Group actualGroup = dao.getById(FIRST_ID).get();
            assertEquals(expectedGroup, actualGroup);
        }

        @Test
        @DisplayName("with id=4 should return DAOException 'Group not found: 4'")
        void testGetByIdGroupException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                    () -> dao.getById(4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllGroups() {
            int expectedQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityGroups = dao.getAll().size();
            assertEquals(expectedQuantityGroups, actualQuantityGroups);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update name and faculty_id group id=1 should write new fields and getById(1) return this fields")
        void testUpdateGroup() throws DAOException {
            Faculty expectedFaculty = new Faculty(SECOND_ID,
                    SECOND_FACULTY_NAME);
            Group expectedGroup = new Group(FIRST_ID, TEST_GROUP_NAME,
                    expectedFaculty);
            dao.update(expectedGroup);
            Group actualGroup = dao.getById(FIRST_ID).get();
            assertEquals(expectedGroup, actualGroup);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete group id=1 should delete one record and number records table should equals 1")
        void testDeleteGroup() {
            int expectedQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;

            Faculty faculty = new Faculty(FIRST_ID, FIRST_FACULTY_NAME);

            Group group = new Group(FIRST_ID, FIRST_GROUP_NAME, faculty);
            dao.delete(group);
            int actualQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityGroups, actualQuantityGroups);
        }
    }
}
