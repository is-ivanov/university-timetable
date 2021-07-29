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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/group-test-data.sql"})
class GroupDaoImplTest {

    private static final String TABLE_NAME = "groups";
    private static final String TEST_GROUP_NAME = "GroupName";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;
    private static final String FIRST_GROUP_NAME = "20Eng-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Group id(3) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because group id(3) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because group id(3) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GroupDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("after add test group should CountRowsTable = 3")
        void testAddGroupCountRows() {
            Faculty faculty = new Faculty();
            faculty.setId(ID1);
            faculty.setName(TEST_FACULTY_NAME);

            Group group = new Group();
            group.setName(TEST_GROUP_NAME);
            group.setActive(true);
            group.setFaculty(faculty);

            dao.add(group);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }

        @Test
        @DisplayName("after add test group should getGroup id=3 equals test group")
        void testAddGroupGetEqualsTestGroup() {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group expectedGroup = new Group();
            expectedGroup.setId(ID3);
            expectedGroup.setName(TEST_GROUP_NAME);
            expectedGroup.setActive(true);
            expectedGroup.setFaculty(expectedFaculty);

            dao.add(expectedGroup);

            assertEquals(expectedGroup, dao.getById(ID3).orElse(null));
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return group (1, '20Eng-1', faculty id=1, true)")
        void testGetByIdGroup() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group expectedGroup = new Group(ID1, FIRST_GROUP_NAME,
                expectedFaculty, true);

            Group actualGroup = dao.getById(ID1).orElse(null);
            assertEquals(expectedGroup, actualGroup);
        }

        @Test
        @DisplayName("with id=3 should return DAOException")
        void testGetByIdGroupException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                () -> dao.getById(ID3));
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
        @DisplayName("with group id=1 should write new fields and getById(1) " +
            "return expected group")
        void testUpdateExistingGroup_WriteExpectedGroup() throws DAOException {
            Faculty expectedFaculty = new Faculty(ID2, SECOND_FACULTY_NAME);
            Group expectedGroup = new Group(ID1, TEST_GROUP_NAME,
                expectedFaculty, true);
            dao.update(expectedGroup);
            Group actualGroup = dao.getById(ID1).orElse(new Group());
            assertEquals(expectedGroup, actualGroup);
        }

        @Test
        @DisplayName("with group id=3 should write new log.warn with expected" +
            " message")
        void testUpdateNonExistingGroup_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
            Group group = new Group(ID3, TEST_GROUP_NAME, new Faculty(), true);
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, group);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.update(group));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("with group id=1 should delete one record and number " +
            "records table should equals 1")
        void testDeleteExistingGroup_ReduceNumberRowsInTable() {
            int expectedQuantityGroups = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Faculty faculty = new Faculty(ID1, FIRST_FACULTY_NAME);
            Group group = new Group(ID1, FIRST_GROUP_NAME, faculty, true);
            dao.delete(group);
            int actualQuantityGroups = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityGroups, actualQuantityGroups);
        }

        @Test
        @DisplayName("with group id=3 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingGroup_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
            Group group = new Group(ID3, TEST_GROUP_NAME, new Faculty(), true);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, group);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.delete(group));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getByFacultyId' method")
    class getByFacultyIdTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 2")
        void testGetGroupsByFacultyId1() {
            int expectedQuantityGroups = 2;
            int actualQuantityGroups = dao.getAllByFacultyId(ID1).size();
            assertEquals(expectedQuantityGroups, actualQuantityGroups);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetGroupsByFacultyId2() {
            assertTrue(dao.getAllByFacultyId(ID2).isEmpty());
        }
    }
}