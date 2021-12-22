package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.springconfig.BaseRepositoryIT;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.domain.entity.Assertions.assertThat;

@Sql("/sql/hibernate/group-test-data.sql")
class GroupRepositoryTest extends BaseRepositoryIT {

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

    @Nested
    @DisplayName("test 'findAllActiveWithoutStudents' method")
    class FindAllActiveWithoutStudentsTest {
        @Test
        @DisplayName("when studentIds equal {1, 2} then should return group with " +
            "id 2")
        void testStudentsEqual_1_2_ReturnGroupId1() {
            List<Integer> studentIds = Arrays.asList(1, 2);

            List<Group> groups = repo.findAllActiveWithoutStudents(studentIds);

            assertThat(groups).hasSize(1);
            assertThat(groups.get(0))
                .hasId(2)
                .hasName(NAME_SECOND_GROUP);
        }

        @Test
        @DisplayName("when studentIds equal {1, 3} then should return groups with " +
            "id {1, 2}")
        void testStudentsEqual_1_3_ReturnGroupsId_1_2() {
            List<Integer> studentIds = Arrays.asList(1, 3);

            List<Group> groups = repo.findAllActiveWithoutStudents(studentIds);

            assertThat(groups).hasSize(2);
            assertThat(groups).extracting(Group::getId)
                .containsExactly(2, 1);
        }

        @Test
        @DisplayName("when studentIds equal {1, 2, 3, 4} then should return empty " +
            "list")
        void testStudentsEqual_1_2_3_4_ReturnEmptyList() {
            List<Integer> studentIds = Arrays.asList(1, 2, 3, 4);

            List<Group> groups = repo.findAllActiveWithoutStudents(studentIds);

            assertThat(groups).isEmpty();
        }
    }
}