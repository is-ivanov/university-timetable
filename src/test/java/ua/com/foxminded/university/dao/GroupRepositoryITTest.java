package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.springconfig.BaseRepositoryIT;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@Sql("/sql/hibernate/group-test-data.sql")
class GroupRepositoryITTest extends BaseRepositoryIT {

    private static final LocalDateTime START_FIRST_LESSON =
        LocalDateTime.of(2021, 6, 12, 14, 0);
    private static final LocalDateTime END_FIRST_LESSON =
        LocalDateTime.of(2021, 6, 12, 15, 30);
    private static final LocalDateTime START_SECOND_LESSON =
        LocalDateTime.of(2021, 6, 10, 14, 0);
    private static final LocalDateTime END_SECOND_LESSON =
        LocalDateTime.of(2021, 6, 10, 15, 30);

    @Autowired
    private GroupRepository repo;

    @Nested
    @DisplayName("test 'findAllByFacultyId' method")
    class FindAllByFacultyIdTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 3")
        void testGetGroupsByFacultyId1() {
            List<Group> groupsFromFaculty1 = repo.findAllByFacultyId(ID1);
            assertThat(groupsFromFaculty1).hasSize(3);
            assertThat(groupsFromFaculty1).extracting(Group::getName)
                .contains(NAME_FIRST_GROUP, NAME_SECOND_GROUP, NAME_THIRD_GROUP);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetGroupsByFacultyId2() {
            assertThat(repo.findAllByFacultyId(ID2)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findFreeGroupsOnLessonTime' method")
    class FindFreeGroupsOnLessonTimeTest {

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

                    List<Group> freeGroupsOnSecondLessonTime = repo
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

                    List<Group> freeGroupsOnSecondLessonTime = repo
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

                    List<Group> freeGroupsOnSecondLessonTime = repo
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

                    List<Group> freeGroupsOnSecondLessonTime = repo
                        .findFreeGroupsOnLessonTime(START_FIRST_LESSON,
                            END_FIRST_LESSON.plusMinutes(2));

                    assertThat(freeGroupsOnSecondLessonTime).hasSize(2);
                    assertThat(freeGroupsOnSecondLessonTime).extracting(Group::getId)
                        .containsOnly(1, 2);
                }

                @Test
                @DisplayName("starts before and ends during the scheduled lesson")
                void lessonStartsBeforeAndEndsDuringScheduledLesson() {

                    List<Group> freeGroupsOnSecondLessonTime = repo
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
    @DisplayName("test 'findFreeGroupsByFacultyOnLessonTime' method")
    class FindFreeGroupsByFacultyOnLessonTimeTest {

        @Test
        @DisplayName("when faculty without group then return empty list")
        void testFacultyWithoutGroup_ReturnEmptyList() {
            List<Group> freeGroupsFaculty2 =
                repo.findFreeGroupsByFacultyOnLessonTime(ID2, START_FIRST_LESSON,
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

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = repo
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

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = repo
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

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = repo
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

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = repo
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

                        List<Group> freeGroupsOnFaculty1InSecondLessonTime = repo
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
    @DisplayName("test 'findAllByActiveTrue' method")
    class FindAllByActiveTrueTest {

        @Test
        @DisplayName("should return List with size = 2 with expected groups")
        void testGetActiveGroups() {

            List<Group> groups = repo.findAllByActiveTrue();
            assertThat(groups).hasSize(2);
            assertThat(groups).extracting(Group::getName)
                .containsOnly(NAME_FIRST_GROUP, NAME_SECOND_GROUP);
        }
    }
}