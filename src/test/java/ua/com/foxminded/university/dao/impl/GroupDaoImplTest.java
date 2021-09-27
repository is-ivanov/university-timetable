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
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
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
    private static final String SECOND_GROUP_NAME = "21Ger-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Group id(3) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_DELETE_ID_MASK = "Can't delete group id(%d)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because group id(3) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because group id(3) not found";
    private static final LocalDateTime START_FIRST_LESSON = LocalDateTime.of(2021, 6, 12, 14, 0);
    private static final LocalDateTime END_FIRST_LESSON = LocalDateTime.of(2021, 6, 12, 15, 30);
    private static final LocalDateTime START_SECOND_LESSON = LocalDateTime.of(2021, 6, 10, 14, 0);
    private static final LocalDateTime END_SECOND_LESSON = LocalDateTime.of(2021, 6, 10, 15, 30);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GroupDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

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
    class GetByIdTest {

        @Test
        @DisplayName("with id=1 should return group (1, '20Eng-1', faculty id=1, true)")
        void testGetByIdGroup() throws DaoException {
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
        void testGetByIdGroupException() throws DaoException {
            DaoException exception = assertThrows(DaoException.class,
                () -> dao.getById(ID3));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

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
    class UpdateTest {

        @Test
        @DisplayName("with group id=1 should write new fields and getById(1) " +
            "return expected group")
        void testUpdateExistingGroup_WriteExpectedGroup() throws DaoException {
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
            Exception ex = assertThrows(DaoException.class,
                () -> dao.update(group));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Nested
        @DisplayName("delete(group) method")
        class DeleteGroupTest {

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
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(group));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }

        @Nested
        @DisplayName("delete(groupId) method")
        class DeleteGroupIdTest {

            @Test
            @DisplayName("with group id=1 should delete one record and number " +
                "records table should equals 1")
            void testDeleteExistingGroupId1_ReduceNumberRowsInTable() {
                int expectedQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                dao.delete(ID1);
                int actualQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityGroups, actualQuantityGroups);
            }

            @Test
            @DisplayName("with group id=3 should write new log.warn with " +
                "expected message")
            void testDeleteNonExistingGroup_ExceptionWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID3);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(ID3));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("test 'getAllByFacultyId' method")
    class GetAllByFacultyIdTest {

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

    @Nested
    @DisplayName("test 'getFreeGroupsOnLessonTime' method")
    class GetFreeGroupsOnLessonTimeTest {

        @Nested
        @DisplayName("when all students from group have lesson on checked time")
        class AllStudentFromGroupHaveLesson {

            @Nested
            @DisplayName("if checked lesson")
            class IfCheckedLesson {

                @Test
                @DisplayName("starts at the same time as the scheduled lesson then don't return this group")
                void lessonStartsAtSameTimeScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .getFreeGroupsOnLessonTime(START_SECOND_LESSON,
                            END_SECOND_LESSON.plusMinutes(2));

                    assertThat(freeGroupsOnSecondLessonTime.size(), is(equalTo(1)));

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                    assertThat(actualGroup.getId(), is(equalTo(ID1)));
                }

                @Test
                @DisplayName("starts before the scheduled lesson and ends during" +
                    " the scheduled lesson then don't return this group")
                void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .getFreeGroupsOnLessonTime(START_SECOND_LESSON.minusHours(1),
                            START_SECOND_LESSON.plusHours(1));

                    assertThat(freeGroupsOnSecondLessonTime.size(), is(equalTo(1)));

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                    assertThat(actualGroup.getId(), is(equalTo(ID1)));
                }

                @Test
                @DisplayName("starts and ends during the scheduled lesson " +
                    " the scheduled lesson then don't return this group")
                void lessonStartsAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .getFreeGroupsOnLessonTime(START_SECOND_LESSON.plusMinutes(1),
                            END_SECOND_LESSON.minusMinutes(1));

                    assertThat(freeGroupsOnSecondLessonTime.size(), is(equalTo(1)));

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                    assertThat(actualGroup.getId(), is(equalTo(ID1)));
                }

            }

        }


    }

}