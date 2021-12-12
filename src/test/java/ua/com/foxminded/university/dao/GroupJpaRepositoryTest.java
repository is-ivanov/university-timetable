package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.GroupRepository;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/group-test-data.sql")
class GroupJpaRepositoryTest extends IntegrationTestBase {

    private static final String TABLE_NAME = "groups";
    private static final String NAME_TEST_GROUP = "GroupName";
    private static final String MESSAGE_DELETE_GROUP_NOT_FOUND =
        "Can't delete because group id(4) not found";
    private static final LocalDateTime START_FIRST_LESSON =
        LocalDateTime.of(2021, 6, 12, 14, 0);
    private static final LocalDateTime END_FIRST_LESSON =
        LocalDateTime.of(2021, 6, 12, 15, 30);
    private static final LocalDateTime START_SECOND_LESSON =
        LocalDateTime.of(2021, 6, 10, 14, 0);
    private static final LocalDateTime END_SECOND_LESSON =
        LocalDateTime.of(2021, 6, 10, 15, 30);

    @Autowired
    private GroupRepository dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Nested
//    @DisplayName("test 'add' method")
//    class AddTest {
//
//        @Test
//        @DisplayName("after add test group should CountRowsTable = 4")
//        void testAddGroupCountRows() {
//            Faculty faculty = new Faculty(ID1, NAME_FIRST_FACULTY);
//
//            Group group = new Group(NAME_TEST_GROUP, faculty, true);
//
//            int expectedRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
//                TABLE_NAME) + 1;
//
//            dao.add(group);
//            List<Group> actualGroups = dao.getAll();
//            assertThat(actualGroups).hasSize(expectedRowsInTable);
//            assertThat(actualGroups).extracting(Group::getName)
//                .contains(NAME_TEST_GROUP);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("test 'getById' method")
//    class GetByIdTest {
//
//        @Test
//        @DisplayName("with id=1 should return expected group")
//        void testGetByIdGroup() {
//
//            Group actualGroup = dao.getById(ID1).get();
//            assertThat(actualGroup.getId()).isEqualTo(ID1);
//            assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
//            assertThat(actualGroup.isActive()).isTrue();
//        }
//
//        @Test
//        @DisplayName("with id=4 should return empty Optional")
//        void testGetByIdGroupEmptyOptional() {
//            Optional<Group> groupOptional = dao.getById(4);
//            assertThat(groupOptional).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAll' method")
//    class GetAllTest {
//
//        @Test
//        @DisplayName("should return List with size = 3")
//        void testGetAllGroups() {
//            int expectedQuantityGroups = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            List<Group> groups = dao.getAll();
//            assertThat(groups).hasSize(expectedQuantityGroups);
//            assertThat(groups).extracting(Group::getName)
//                .containsOnly(NAME_FIRST_GROUP, NAME_SECOND_GROUP, NAME_THIRD_GROUP);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class UpdateTest {
//
//        @Test
//        @DisplayName("with group id=1 should write new fields and getById(1) " +
//            "return expected group")
//        void testUpdateExistingGroup_WriteExpectedGroup() {
//            Faculty expectedFaculty = new Faculty(ID2, NAME_SECOND_FACULTY);
//            Group expectedGroup = new Group(ID1, NAME_TEST_GROUP,
//                expectedFaculty, true);
//            dao.update(expectedGroup);
//            Group actualGroup = dao.getById(ID1).get();
//            assertThat(actualGroup).isEqualTo(expectedGroup);
//        }
//
//        @Test
//        @DisplayName("with group id=4 should write new group")
//        void testUpdateNonExistingGroup_CreateNewGroup() {
//            Faculty expectedFaculty = new Faculty(ID2, NAME_SECOND_FACULTY);
//            Group expectedGroup = new Group(5, NAME_TEST_GROUP,
//                expectedFaculty, true);
//            dao.update(expectedGroup);
//
//            Group actualGroup = dao.getById(5).get();
//            assertThat(actualGroup).isEqualTo(expectedGroup);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Nested
//        @DisplayName("delete(group) method")
//        class DeleteGroupTest {
//
//            @Test
//            @DisplayName("with group id=3 should delete one record and number " +
//                "of records should decrease by one")
//            void testDeleteExistingGroup_ReduceNumberRowsInTable() {
//
//                Group group = dao.getById(ID3).get();
//                dao.delete(group);
//                List<Group> groups = dao.getAll();
//                assertThat(groups).hasSize(2);
//                assertThat(groups).extracting(Group::getName)
//                    .doesNotContain(NAME_THIRD_GROUP);
//            }
//        }
//
//        @Nested
//        @DisplayName("delete(groupId) method")
//        class DeleteGroupIdTest {
//
//            @Test
//            @DisplayName("with group id=3 should delete one record and number " +
//                "of records should decrease by one")
//            void testDeleteExistingGroupId3_ReduceNumberRowsInTable() {
//                dao.delete(ID3);
//                List<Group> groups = dao.getAll();
//                assertThat(groups).hasSize(2);
//                assertThat(groups).extracting(Group::getName)
//                    .doesNotContain(NAME_THIRD_GROUP);
//            }
//
//            @Test
//            @DisplayName("with group id=4 should throw DaoException with " +
//                "expected message")
//            void testDeleteNonExistingGroup_ThrowDaoException() {
//                assertThatThrownBy(() -> dao.delete(4))
//                    .isInstanceOf(DaoException.class)
//                    .hasMessageContaining(MESSAGE_DELETE_GROUP_NOT_FOUND);
//            }
//        }
//    }
//
    @Nested
    @DisplayName("test 'getAllByFacultyId' method")
    class GetAllByFacultyIdTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 3")
        void testGetGroupsByFacultyId1() {
            List<Group> groupsFromFaculty1 = dao.findAllByFacultyId(ID1);
            assertThat(groupsFromFaculty1).hasSize(3);
            assertThat(groupsFromFaculty1).extracting(Group::getName)
                .contains(NAME_FIRST_GROUP, NAME_SECOND_GROUP, NAME_THIRD_GROUP);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetGroupsByFacultyId2() {
            assertThat(dao.findAllByFacultyId(ID2)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getFreeGroupsOnLessonTime' method")
    class GetFreeGroupsOnLessonTimeTest {

        @Nested
        @DisplayName("when all students from the group have lesson on checked time")
        class AllStudentFromGroupHaveLessonTest {

            @Nested
            @DisplayName("if checked lesson")
            class IfCheckedLessonTest {

                @Test
                @DisplayName("starts at the same time as the scheduled lesson " +
                    "then don't return this group")
                void lessonStartsAtSameTimeScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .findFreeGroupsOnLessonTime(START_SECOND_LESSON,
                            END_SECOND_LESSON.plusMinutes(2));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(1);

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                    assertThat(actualGroup.getId()).isEqualTo(ID1);
                    assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                }

                @Test
                @DisplayName("starts before and ends during the scheduled lesson " +
                    "then don't return this group")
                void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .findFreeGroupsOnLessonTime(START_SECOND_LESSON.minusHours(1),
                            START_SECOND_LESSON.plusHours(1));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(1);

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                    assertThat(actualGroup.getId()).isEqualTo(ID1);
                    assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                }

                @Test
                @DisplayName("starts and ends during the scheduled lesson " +
                    "the scheduled lesson then don't return this group")
                void lessonStartsAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .findFreeGroupsOnLessonTime(START_SECOND_LESSON.plusMinutes(1),
                            END_SECOND_LESSON.minusMinutes(1));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(1);

                    Group actualGroup = freeGroupsOnSecondLessonTime.get(0);

                    assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                    assertThat(actualGroup.getId()).isEqualTo(ID1);
                    assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                }
            }
        }

        @Nested
        @DisplayName("when several students from the group have lesson on checked time")
        class SeveralStudentsFromGroupHaveLessonTest {

            @Nested
            @DisplayName("then should return this group regardless of checked lesson time")
            class IfCheckedLessonTest {

                @Test
                @DisplayName("starts at the same time as the scheduled lesson")
                void lessonStartsAtSameTimeScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .findFreeGroupsOnLessonTime(START_FIRST_LESSON,
                            END_FIRST_LESSON.plusMinutes(2));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(2);
                    assertThat(freeGroupsOnSecondLessonTime).extracting(Group::getId)
                        .containsOnly(1, 2);
                }

                @Test
                @DisplayName("starts before and ends during the scheduled lesson")
                void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = dao
                        .findFreeGroupsOnLessonTime(START_FIRST_LESSON.minusHours(1),
                            END_FIRST_LESSON.minusHours(1));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(2);
                    assertThat(freeGroupsOnSecondLessonTime).extracting(Group::getId)
                        .containsOnly(1, 2);
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
                dao.findFreeGroupsByFacultyOnLessonTime(ID2, START_FIRST_LESSON,
                    END_FIRST_LESSON);
            assertThat(freeGroupsFaculty2).isEmpty();
        }

        @Nested
        @DisplayName("when the faculty contains groups")
        class FacultyContainsGroupTest {

            @Nested
            @DisplayName("when all students from the group have lesson on checked time")
            class AllStudentFromGroupHaveLessonTest {

                @Nested
                @DisplayName("if checked lesson")
                class IfCheckedLessonTest {

                    @Test
                    @DisplayName("starts at the same time as the scheduled lesson " +
                        "then don't return this group")
                    void lessonStartsAtSameTimeScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .findFreeGroupsByFacultyOnLessonTime(ID1, START_SECOND_LESSON,
                                END_SECOND_LESSON.plusMinutes(2));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).hasSize(1);

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                        assertThat(actualGroup.getId()).isEqualTo(ID1);
                        assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                    }

                    @Test
                    @DisplayName("starts before and ends during the scheduled lesson " +
                        "then don't return this group")
                    void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .findFreeGroupsByFacultyOnLessonTime(ID1,
                                START_SECOND_LESSON.minusHours(1),
                                START_SECOND_LESSON.plusHours(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).hasSize(1);

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                        assertThat(actualGroup.getId()).isEqualTo(ID1);
                        assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                    }

                    @Test
                    @DisplayName("starts and ends during the scheduled lesson " +
                        "the scheduled lesson then don't return this group")
                    void lessonStartsAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .findFreeGroupsByFacultyOnLessonTime(ID1,
                                START_SECOND_LESSON.plusMinutes(1),
                                END_SECOND_LESSON.minusMinutes(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).hasSize(1);

                        Group actualGroup = freeGroupsOnFaculty1InSecondLessonTime.get(0);

                        assertThat(actualGroup.getName()).isNotEqualTo(NAME_SECOND_GROUP);
                        assertThat(actualGroup.getId()).isEqualTo(ID1);
                        assertThat(actualGroup.getName()).isEqualTo(NAME_FIRST_GROUP);
                    }
                }
            }

            @Nested
            @DisplayName("when several students from the group have lesson on checked time")
            class SeveralStudentsFromGroupHaveLessonTest {

                @Nested
                @DisplayName("then should return this group regardless of checked lesson time")
                class IfCheckedLessonTest {

                    @Test
                    @DisplayName("starts at the same time as the scheduled lesson")
                    void lessonStartsAtSameTimeScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .findFreeGroupsByFacultyOnLessonTime(ID1,
                                START_FIRST_LESSON,
                                END_FIRST_LESSON.plusMinutes(2));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).hasSize(2);
                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).extracting(Group::getId)
                            .containsOnly(1, 2);
                    }

                    @Test
                    @DisplayName("starts before and ends during the scheduled lesson")
                    void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = dao
                            .findFreeGroupsByFacultyOnLessonTime(ID1,
                                START_FIRST_LESSON.minusHours(1),
                                END_FIRST_LESSON.minusHours(1));

                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).hasSize(2);
                        assertThat(freeGroupsOnFaculty1InSecondLessonTime).extracting(Group::getId)
                            .containsOnly(1, 2);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("test 'getActiveGroups' method")
    class GetActiveGroupsTest {

        @Test
        @DisplayName("should return List with size = 2 with expected groups")
        void testGetActiveGroups() {

            List<Group> groups = dao.findAllByActiveTrue();
            assertThat(groups).hasSize(2);
            assertThat(groups).extracting(Group::getName)
                .containsOnly(NAME_FIRST_GROUP, NAME_SECOND_GROUP);
        }
    }
}