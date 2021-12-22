package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.springconfig.BaseRepositoryIT;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@Sql("/sql/hibernate/teacher-test-data.sql")
class TeacherRepositoryTest extends BaseRepositoryIT {

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
            List<Teacher> actualTeachers = repo.findByDepartment_Faculty_IdIs(FACULTY_ID1);
            assertThat(actualTeachers).hasSize(expectedQuantityTeachers);
            assertThat(actualTeachers).extracting(Teacher::getFirstName)
                .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER,
                    NAME_THIRD_TEACHER);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetAllTeachersByFacultyId2() {
            assertThat(repo.findByDepartment_Faculty_IdIs(ID2)).isEmpty();
        }
    }

}