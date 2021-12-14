package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@Sql("/sql/hibernate/teacher-test-data.sql")
class TeacherJpaRepositoryTest extends IntegrationTestBase {

    private static final String TABLE_NAME = "teachers";

    @Autowired
    private TeacherRepository repo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DisplayName("test 'findAllByDepartmentId' method")
    class FindAllByDepartmentIdTest {

        @Test
        @DisplayName("with department_id1 should return List with size = 2")
        void testGetAllTeachersByDepartmentId1() {
            List<Teacher> actualTeachers = repo.findAllByDepartmentId(DEPARTMENT_ID1);

            assertThat(actualTeachers).hasSize(2);
            assertThat(actualTeachers).extracting(Teacher::getFirstName)
                    .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER);
        }

        @Test
        @DisplayName("with department id=4 should return empty List")
        void testGetAllTeachersByDepartmentId4() {
            assertThat(repo.findAllByDepartmentId(4)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findAllByFaculty' method")
    class FindAllByFacultyTest {

        @Test
        @DisplayName("with faculty_id1 should return List with size = 3")
        void testGetAllTeachersByFacultyId1() {
            int expectedQuantityTeachers =
                JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME);
            List<Teacher> actualTeachers = repo.findAllByFaculty(FACULTY_ID1);
            assertThat(actualTeachers).hasSize(expectedQuantityTeachers);
            assertThat(actualTeachers).extracting(Teacher::getFirstName)
                .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER,
                    NAME_THIRD_TEACHER);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetAllTeachersByFacultyId2() {
            assertThat(repo.findAllByFaculty(ID2)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findFreeTeachersOnLessonTime' method")
    class FindFreeTeachersOnLessonTimeTest {

        @Test
        @DisplayName("when new lesson starts at 14:30 12-09-2021 then return " +
            "list with one teacher (id3)")
        void testReturnEmptyList() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 14, 30);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Teacher> actualTeachers =
                repo.findFreeTeachersOnLessonTime(startTime, endTime);

            assertThat(actualTeachers).hasSize(1);
            Teacher freeTeacher = actualTeachers.get(0);
            assertThat(freeTeacher.getId()).isEqualTo(TEACHER_ID3);
            assertThat(freeTeacher.getFirstName()).isEqualTo(NAME_THIRD_TEACHER);
        }

        @Test
        @DisplayName("when new lesson starts at 15:45 12-09-2021 then return " +
            "list with two teachers (id1 and id3)")
        void testReturnOneTeacher() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 15, 45);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Teacher> actualTeachers =
                repo.findFreeTeachersOnLessonTime(startTime, endTime);

            assertThat(actualTeachers).hasSize(2);
            assertThat(actualTeachers).extracting(Teacher::getId)
                .containsOnly(TEACHER_ID1, TEACHER_ID3)
                .doesNotContain(TEACHER_ID2);

        }
    }

}