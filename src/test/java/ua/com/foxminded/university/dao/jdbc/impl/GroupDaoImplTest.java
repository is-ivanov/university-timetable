package ua.com.foxminded.university.dao.jdbc.impl;

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
import java.util.stream.Collectors;

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
    private static final int ID4 = 4;
    private static final String FIRST_GROUP_NAME = "20Eng-1";
    private static final String SECOND_GROUP_NAME = "21Ger-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String MESSAGE_EXCEPTION = "Group id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update %s";
    private static final String MESSAGE_DELETE_MASK = "Can't delete %s";
    private static final String MESSAGE_DELETE_ID_MASK = "Can't delete group id(%d)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because group id(4) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because group id(4) not found";
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
        @DisplayName("after add test group should CountRowsTable = 4")
        void testAddGroupCountRows() {
            Faculty faculty = new Faculty();
            faculty.setId(ID1);
            faculty.setName(TEST_FACULTY_NAME);

            Group group = new Group();
            group.setName(TEST_GROUP_NAME);
            group.setActive(true);
            group.setFaculty(faculty);

            int expectedRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME) + 1;

            dao.add(group);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }

        @Test
        @DisplayName("after add test group should getGroup id=4 equals test group")
        void testAddGroupGetEqualsTestGroup() {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group groupForAdding = new Group();
            groupForAdding.setName(TEST_GROUP_NAME);
            groupForAdding.setActive(true);
            groupForAdding.setFaculty(expectedFaculty);

            dao.add(groupForAdding);

            Group addedGroup = dao.getById(ID4).orElse(new Group());
            assertThat(addedGroup.getName(), is(equalTo(TEST_GROUP_NAME)));
            assertThat(addedGroup.isActive(), is(equalTo(true)));
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
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdGroupException() throws DaoException {
            DaoException exception = assertThrows(DaoException.class,
                () -> dao.getById(ID4));
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
        @DisplayName("with group id=4 should write new log.warn with expected" +
            " message")
        void testUpdateNonExistingGroup_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
            Group group = new Group(ID4, TEST_GROUP_NAME, new Faculty(), true);
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
            @DisplayName("with group id=3 should delete one record and number " +
                "of records should decrease by one")
            void testDeleteExistingGroup_ReduceNumberRowsInTable() {
                int expectedQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                Faculty faculty = new Faculty(ID1, FIRST_FACULTY_NAME);
                Group group = new Group(ID3, FIRST_GROUP_NAME, faculty, true);
                dao.delete(group);
                int actualQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityGroups, actualQuantityGroups);
            }

            @Test
            @DisplayName("with group id=4 should write new log.warn with " +
                "expected message")
            void testDeleteNonExistingGroup_ExceptionWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
                Group group = new Group(ID4, TEST_GROUP_NAME, new Faculty(), true);
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
            @DisplayName("with group id=3 should delete one record and number " +
                "of records should decrease by one")
            void testDeleteExistingGroupId3_ReduceNumberRowsInTable() {
                int expectedQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
                dao.delete(ID3);
                int actualQuantityGroups = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
                assertEquals(expectedQuantityGroups, actualQuantityGroups);
            }

            @Test
            @DisplayName("with group id=4 should write new log.warn with " +
                "expected message")
            void testDeleteNonExistingGroup_ExceptionWriteLogWarn() {
                LogCaptor logCaptor = LogCaptor.forClass(GroupDaoImpl.class);
                String expectedLog = String.format(MESSAGE_DELETE_ID_MASK, ID4);
                Exception ex = assertThrows(DaoException.class,
                    () -> dao.delete(ID4));
                assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
                assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("test 'getAllByFacultyId' method")
    class GetAllByFacultyIdTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 3")
        void testGetGroupsByFacultyId1() {
            int expectedQuantityGroups = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
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
        @DisplayName("when all students from the group have lesson on checked time")
        class AllStudentFromGroupHaveLesson {

            @Nested
            @DisplayName("if checked lesson")
            class IfCheckedLesson {

                @Test
                @DisplayName("starts at the same time as the scheduled lesson " +
                    "then don't return this group")
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
                @DisplayName("starts before and ends during the scheduled lesson " +
                    "then don't return this group")
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
                    "the scheduled lesson then don't return this group")
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

        @Nested
        @DisplayName("when several students from the group have lesson on checked time")
        class SeveralStudentsFromGroupHaveLesson {

            @Nested
            @DisplayName("then should return this group regardless of checked lesson time")
            class IfCheckedLesson {

                @Test
                @DisplayName("starts at the same time as the scheduled lesson")
                void lessonStartsAtSameTimeScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .getFreeGroupsOnLessonTime(START_FIRST_LESSON,
                            END_FIRST_LESSON.plusMinutes(2));

                    assertThat(freeGroupsOnSecondLessonTime, hasSize(2));

                    List<Integer> groupIds = freeGroupsOnSecondLessonTime.stream()
                        .map(Group::getId)
                        .collect(Collectors.toList());

                    assertThat(groupIds, contains(1, 2));
                    assertThat(groupIds, everyItem(lessThan(3)));
                }

                @Test
                @DisplayName("starts before and ends during the scheduled lesson")
                void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .getFreeGroupsOnLessonTime(START_FIRST_LESSON.minusHours(1),
                            END_FIRST_LESSON.minusHours(1));

                    assertThat(freeGroupsOnSecondLessonTime, hasSize(2));

                    List<Integer> groupIds = freeGroupsOnSecondLessonTime.stream()
                        .map(Group::getId)
                        .collect(Collectors.toList());

                    assertThat(groupIds, contains(1, 2));
                    assertThat(groupIds, everyItem(lessThan(3)));
                }
            }
        }


    }

    @Nested
    @DisplayName("test 'getFreeGroupsByFacultyOnLessonTime' method")
    class GetFreeGroupsByFacultyOnLessonTimeTest {

        @Test
        @DisplayName("when faculty without group then return empty list")
        void testFacultyWithoutGroup_ReturnEmptyList() {
            List<Group> freeGroupsFaculty2 =
                dao.getFreeGroupsByFacultyOnLessonTime(ID2, START_FIRST_LESSON,
                    END_FIRST_LESSON);
            assertThat(freeGroupsFaculty2, is(empty()));
        }

        @Nested
        @DisplayName("when the faculty contains groups")
        class FacultyContainsGroup {

            @Nested
            @DisplayName("when all students from the group have lesson on checked time")
            class AllStudentFromGroupHaveLesson {

                @Nested
                @DisplayName("if checked lesson")
                class IfCheckedLesson {

                    @Test
                    @DisplayName("starts at the same time as the scheduled lesson " +
                        "then don't return this group")
                    void lessonStartsAtSameTimeScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .getFreeGroupsByFacultyOnLessonTime(ID1, START_SECOND_LESSON,
                                END_SECOND_LESSON.plusMinutes(2));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime.size(),
                            is(equalTo(1)));

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                        assertThat(actualGroup.getId(), is(equalTo(ID1)));
                    }

                    @Test
                    @DisplayName("starts before and ends during the scheduled lesson " +
                        "then don't return this group")
                    void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .getFreeGroupsByFacultyOnLessonTime(ID1,
                                START_SECOND_LESSON.minusHours(1),
                                START_SECOND_LESSON.plusHours(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime.size(),
                            is(equalTo(1)));

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                        assertThat(actualGroup.getId(), is(equalTo(ID1)));
                    }

                    @Test
                    @DisplayName("starts and ends during the scheduled lesson " +
                        "the scheduled lesson then don't return this group")
                    void lessonStartsAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .getFreeGroupsByFacultyOnLessonTime(ID1,
                                START_SECOND_LESSON.plusMinutes(1),
                                END_SECOND_LESSON.minusMinutes(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime.size(), is(equalTo(1)));

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName(), is(not(equalTo(SECOND_GROUP_NAME))));
                        assertThat(actualGroup.getId(), is(equalTo(ID1)));
                    }

                }

            }

            @Nested
            @DisplayName("when several students from the group have lesson on checked time")
            class SeveralStudentsFromGroupHaveLesson {

                @Nested
                @DisplayName("then should return this group regardless of checked lesson time")
                class IfCheckedLesson {

                    @Test
                    @DisplayName("starts at the same time as the scheduled lesson")
                    void lessonStartsAtSameTimeScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .getFreeGroupsByFacultyOnLessonTime(ID1,
                                START_FIRST_LESSON,
                                END_FIRST_LESSON.plusMinutes(2));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime, hasSize(2));

                        List<Integer> groupIds = freeGroupsOnFaculty1InSecondLessonTime.stream()
                            .map(Group::getId)
                            .collect(Collectors.toList());

                        assertThat(groupIds, contains(1, 2));
                        assertThat(groupIds, everyItem(lessThan(3)));
                    }

                    @Test
                    @DisplayName("starts before and ends during the scheduled lesson")
                    void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .getFreeGroupsByFacultyOnLessonTime(ID1,
                                START_FIRST_LESSON.minusHours(1),
                                END_FIRST_LESSON.minusHours(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime, hasSize(2));

                        List<Integer> groupIds = freeGroupsOnFaculty1InSecondLessonTime.stream()
                            .map(Group::getId)
                            .collect(Collectors.toList());

                        assertThat(groupIds, contains(1, 2));
                        assertThat(groupIds, everyItem(lessThan(3)));
                    }
                }
            }
        }

    }

}