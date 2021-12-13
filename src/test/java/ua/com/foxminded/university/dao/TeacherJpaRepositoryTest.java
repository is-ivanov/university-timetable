package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.dao.interfaces.TeacherRepository;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/teacher-test-data.sql")
class TeacherJpaRepositoryTest extends IntegrationTestBase {

    private static final String TABLE_NAME = "teachers";
    private static final String MESSAGE_DELETE_EXCEPTION =
        "Can't delete because teacher id(4) not found";
    private static final String TEST_TEACHER_FIRST_NAME = "John";
    private static final String TEST_TEACHER_LAST_NAME = "Dou";
    private static final String TEST_TEACHER_PATRONYMIC = "Ivanovich";


    @Autowired
    private TeacherRepository dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Nested
//    @DisplayName("test 'add' method")
//    class AddTest {
//
//        @Test
//        @DisplayName("add test teacher should add one row in table")
//        void testAddTeacher() {
//            Department department = createTestDepartment(FACULTY_ID1);
//
//            Teacher teacher = Teacher.builder()
//                .firstName(TEST_TEACHER_FIRST_NAME)
//                .patronymic(TEST_TEACHER_PATRONYMIC)
//                .lastName(TEST_TEACHER_LAST_NAME)
//                .active(true)
//                .department(department)
//                .build();
//
//            int expectedRowsInTable = JdbcTestUtils.countRowsInTable(
//                jdbcTemplate, TABLE_NAME) + 1;
//
//            dao.add(teacher);
//
//            List<Teacher> teachers = dao.getAll();
//            assertThat(teachers).hasSize(expectedRowsInTable);
//            assertThat(teachers).extracting(Teacher::getFirstName)
//                .contains(TEST_TEACHER_FIRST_NAME);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getById' method")
//    class GetByIdTest {
//
//        @Test
//        @DisplayName("with teacher_id1 should return expected teacher)")
//        void testGetByIdTeacher() {
//            Teacher actualTeacher = dao.getById(TEACHER_ID1).get();
//            assertThat(actualTeacher.getId()).isEqualTo(TEACHER_ID1);
//            assertThat(actualTeacher.getFirstName()).isEqualTo(NAME_FIRST_TEACHER);
//            assertThat(actualTeacher.getPatronymic()).isEqualTo(PATRONYMIC_FIRST_TEACHER);
//            assertThat(actualTeacher.getLastName()).isEqualTo(LAST_NAME_FIRST_TEACHER);
//            assertThat(actualTeacher.isActive()).isEqualTo(true);
//
//            Department actualDepartment = actualTeacher.getDepartment();
//            assertThat(actualDepartment.getId()).isEqualTo(DEPARTMENT_ID1);
//            assertThat(actualDepartment.getName()).isEqualTo(NAME_FIRST_DEPARTMENT);
//
//        }
//
//        @Test
//        @DisplayName("with id=4 should return empty Optional")
//        void testGetByIdTeacherException() {
//            Optional<Teacher> teacherOptional = dao.getById(4);
//            assertThat(teacherOptional).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAll' method")
//    class GetAllTest {
//
//        @Test
//        @DisplayName("should return List with size = 3 with expected teachers")
//        void testGetAllTeachers() {
//            int expectedQuantityTeachers = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            List<Teacher> teachers = dao.getAll();
//            assertThat(teachers).hasSize(expectedQuantityTeachers);
//            assertThat(teachers).extracting(Teacher::getFirstName)
//                .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER, NAME_THIRD_TEACHER);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class UpdateTest {
//
//        @Test
//        @DisplayName("with teacher_id1 should write new fields and " +
//            "getById(teacher_id1) return expected teacher")
//        void testUpdateExistingTeacher_WriteNewFields() {
//            Department expectedDepartment = createTestDepartment(FACULTY_ID1);
//            Teacher expectedTeacher = Teacher.builder()
//                .id(TEACHER_ID1)
//                .firstName(TEST_TEACHER_FIRST_NAME)
//                .patronymic(TEST_TEACHER_PATRONYMIC)
//                .lastName(TEST_TEACHER_LAST_NAME)
//                .active(false)
//                .department(expectedDepartment)
//                .build();
//
//            dao.update(expectedTeacher);
//
//            Teacher actualTeacher = dao.getById(TEACHER_ID1).get();
//            assertThat(actualTeacher).isEqualTo(expectedTeacher);
//        }
//
//        @Test
//        @DisplayName("with teacher id=4 should write new teacher with id from sequence")
//        void testUpdateNonExistingTeacher_CreateNewTeacher() {
//            Department expectedDepartment = createTestDepartment(FACULTY_ID1);
//            Teacher expectedTeacher = Teacher.builder()
//                .id(4)
//                .firstName(TEST_TEACHER_FIRST_NAME)
//                .patronymic(TEST_TEACHER_PATRONYMIC)
//                .lastName(TEST_TEACHER_LAST_NAME)
//                .active(false)
//                .department(expectedDepartment)
//                .build();
//
//            dao.update(expectedTeacher);
//
//            Teacher actualTeacher = dao.getById(101).get();
//            assertThat(actualTeacher.getFirstName()).isEqualTo(TEST_TEACHER_FIRST_NAME);
//            assertThat(actualTeacher.getPatronymic()).isEqualTo(TEST_TEACHER_PATRONYMIC);
//            assertThat(actualTeacher.getLastName()).isEqualTo(TEST_TEACHER_LAST_NAME);
//            assertThat(actualTeacher.isActive()).isEqualTo(false);
//            assertThat(actualTeacher.getDepartment()).isEqualTo(expectedDepartment);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Test
//        @DisplayName("with teacher_id3 should delete one record and number " +
//            "records table should one less before")
//        void testDeleteExistingTeacher_ReduceNumberRowsInTable() {
//            int expectedRows = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//            Teacher teacher = dao.getById(TEACHER_ID3).get();
//
//            dao.delete(teacher);
//
//            List<Teacher> teachers = dao.getAll();
//            assertThat(teachers).hasSize(expectedRows);
//            assertThat(teachers).extracting(Teacher::getFirstName)
//                .doesNotContain(NAME_THIRD_TEACHER);
//        }
//
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method with parameter id")
//    class DeleteIdTest {
//
//        @Test
//        @DisplayName("with teacher_id3 should delete one record and number " +
//            "records table should equals 2")
//        void testDeleteExistingTeacher_ReduceNumberRowsInTable() {
//            int expectedRows = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//
//            dao.delete(TEACHER_ID3);
//
//            List<Teacher> teachers = dao.getAll();
//            assertThat(teachers).hasSize(expectedRows);
//            assertThat(teachers).extracting("firstName")
//                .doesNotContain(NAME_THIRD_TEACHER);
//        }
//
//        @Test
//        @DisplayName("with teacher id=4 should throw DaoException with " +
//            "expected message")
//        void testDeleteNonExistingTeacher_ThrowDaoException() {
//           assertThatThrownBy(() -> dao.delete(4))
//               .isInstanceOf(DaoException.class)
//               .hasMessageContaining(MESSAGE_DELETE_EXCEPTION);
//        }
//
//    }

    @Nested
    @DisplayName("test 'getAllByDepartment' method")
    class GetAllByDepartmentTest {

        @Test
        @DisplayName("with department_id1 should return List with size = 2")
        void testGetAllTeachersByDepartmentId1() {
            List<Teacher> actualTeachers = dao.findAllByDepartmentId(DEPARTMENT_ID1);

            assertThat(actualTeachers).hasSize(2);
            assertThat(actualTeachers).extracting(Teacher::getFirstName)
                    .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER);
        }

        @Test
        @DisplayName("with department id=4 should return empty List")
        void testGetAllTeachersByDepartmentId4() {
            assertThat(dao.findAllByDepartmentId(4)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAllByFaculty' method")
    class GetAllByFacultyTest {

        @Test
        @DisplayName("with faculty_id1 should return List with size = 3")
        void testGetAllTeachersByFacultyId1() {
            int expectedQuantityTeachers =
                JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME);
            List<Teacher> actualTeachers = dao.findAllByFacultyId(FACULTY_ID1);
            assertThat(actualTeachers).hasSize(expectedQuantityTeachers);
            assertThat(actualTeachers).extracting(Teacher::getFirstName)
                .containsOnly(NAME_FIRST_TEACHER, NAME_SECOND_TEACHER, NAME_THIRD_TEACHER);
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetAllTeachersByFacultyId2() {
            assertThat(dao.findAllByFacultyId(ID2)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getFreeTeachersOnLessonTime' method")
    class GetFreeTeachersOnLessonTimeTest {

        @Test
        @DisplayName("when new lesson starts at 14:30 12-09-2021 then return " +
            "list with one teacher (id3)")
        void testReturnEmptyList() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 14, 30);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Teacher> actualTeachers =
                dao.findFreeTeachersOnLessonTime(startTime, endTime);
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
                dao.findFreeTeachersOnLessonTime(startTime, endTime);

            assertThat(actualTeachers).hasSize(2);
            assertThat(actualTeachers).extracting(Teacher::getId)
                .containsOnly(TEACHER_ID1, TEACHER_ID3)
                .doesNotContain(TEACHER_ID2);

        }
    }

}