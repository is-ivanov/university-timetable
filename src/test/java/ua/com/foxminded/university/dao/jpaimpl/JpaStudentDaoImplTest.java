package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/student-test-data.sql")
class JpaStudentDaoImplTest extends IntegrationTestBase {

    private static final String TABLE_NAME = "students";
    private static final String TEST_STUDENT_FIRST_NAME = "Student First Name";
    private static final String TEST_STUDENT_LAST_NAME = "Student Last Name";
    private static final String TEST_STUDENT_PATRONYMIC = "Student patronymic";

    @Autowired
    private StudentDao dao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test student should CountRowsTable = 4")
        void testAddStudent() {

            Group group = createTestGroup();
            Student student = Student.builder()
                .firstName(TEST_STUDENT_FIRST_NAME)
                .lastName(TEST_STUDENT_LAST_NAME)
                .patronymic(TEST_STUDENT_PATRONYMIC)
                .active(true)
                .group(group)
                .build();
            int expectedRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME) + 1;

            dao.add(student);

            List<Student> students = dao.getAll();
            assertThat(students).hasSize(expectedRowsInTable);
            assertThat(students).extracting(Student::getFirstName)
                .contains(TEST_STUDENT_FIRST_NAME);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with student_id1 should return expected student)")
        void testGetByIdStudent() {
            Student actualStudent = dao.getById(STUDENT_ID1).get();
            assertThat(actualStudent.getFirstName()).isEqualTo(NAME_FIRST_STUDENT);
            assertThat(actualStudent.getLastName()).isEqualTo(LAST_NAME_FIRST_STUDENT);
            assertThat(actualStudent.getPatronymic()).isEqualTo(PATRONYMIC_FIRST_STUDENT);
            assertThat(actualStudent.isActive()).isEqualTo(true);
        }

        @Test
        @DisplayName("with id=4 should return empty Optional")
        void testGetByIdStudent_EmptyOptional() {
            Optional<Student> studentOptional = dao.getById(4);
            assertThat(studentOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllStudents() {
            int expectedQuantityStudents = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            List<Student> students = dao.getAll();
            assertThat(students).hasSize(expectedQuantityStudents);
            assertThat(students).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT, NAME_THIRD_STUDENT);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with student id=1 should write new fields and " +
            "getById(1) return expected student")
        void testUpdateExistingStudent_WriteNewFields() {

            Group expectedGroup = createTestSecondGroup();
            Student expectedStudent = Student.builder()
                .id(STUDENT_ID1)
                .firstName(TEST_STUDENT_FIRST_NAME)
                .patronymic(TEST_STUDENT_PATRONYMIC)
                .lastName(TEST_STUDENT_LAST_NAME)
                .active(true)
                .group(expectedGroup)
                .build();

            dao.update(expectedStudent);

            Student actualStudent = dao.getById(STUDENT_ID1).get();
            assertThat(actualStudent).isEqualTo(expectedStudent);
        }

        @Test
        @DisplayName("with student id=4 should write new student with id from sequence")
        void testUpdateNonExistingStudent_CreateNewStudent() {
            Group expectedGroup = createTestSecondGroup();
            Student expectedStudent = Student.builder()
                .id(4)
                .firstName(TEST_STUDENT_FIRST_NAME)
                .patronymic(TEST_STUDENT_PATRONYMIC)
                .lastName(TEST_STUDENT_LAST_NAME)
                .active(true)
                .group(expectedGroup)
                .build();

            dao.update(expectedStudent);

            Student actualStudent = dao.getById(101).get();
            assertThat(actualStudent.getFirstName()).isEqualTo(TEST_STUDENT_FIRST_NAME);
            assertThat(actualStudent.getPatronymic()).isEqualTo(TEST_STUDENT_PATRONYMIC);
            assertThat(actualStudent.getLastName()).isEqualTo(TEST_STUDENT_LAST_NAME);
            assertThat(actualStudent.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Test
        @DisplayName("with student_id1 should delete one record and number " +
            "records table should equals 1")
        void testDeleteExistingStudent_ReduceNumberRowsInTable() {
            int expectedRows = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Student student = dao.getById(STUDENT_ID1).get();

            dao.delete(student);

            List<Student> students = dao.getAll();
            assertThat(students).hasSize(expectedRows);
            assertThat(students).extracting(Student::getFirstName)
                .doesNotContain(NAME_FIRST_STUDENT);
        }

    }

    @Nested
    @DisplayName("test 'getStudentsByLesson' method")
    class GetStudentsByLessonTest {

        @Test
        @DisplayName("when lesson_id1 then should return List with size = 1")
        void testGetAllStudentsByLesson() {
            Lesson lesson = new Lesson();
            lesson.setId(LESSON_ID1);

            List<Student> studentsByLesson1 = dao.getStudentsByLesson(lesson);
            assertThat(studentsByLesson1).hasSize(1);
            Student student = studentsByLesson1.get(0);
            assertThat(student.getId()).isEqualTo(STUDENT_ID3);
            assertThat(student.getFirstName()).isEqualTo(NAME_THIRD_STUDENT);
            assertThat(student.getPatronymic()).isEqualTo(PATRONYMIC_THIRD_STUDENT);
            assertThat(student.getLastName()).isEqualTo(LAST_NAME_THIRD_STUDENT);
            assertThat(student.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("test 'getStudentsByGroup' method")
    class GetStudentsByGroupTest {

        @Test
        @DisplayName("when group_id1 then should return List with size = 2")
        void testGetStudentsByGroup() {
            Group group = new Group();
            group.setId(GROUP_ID1);

            List<Student> studentsByGroup = dao.getStudentsByGroup(group);

            assertThat(studentsByGroup).hasSize(2);
            assertThat(studentsByGroup).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT);
        }
    }


    @Nested
    @DisplayName("test 'getStudentsByFaculty' method")
    class GetStudentsByFacultyTest {

        @Test
        @DisplayName("when facultyId1 then should return 2 expected students")
        void testGetByFacultyId1_ReturnTwoStudents() {
            Faculty faculty = new Faculty();
            faculty.setId(FACULTY_ID1);

            List<Student> studentsByFaculty = dao.getStudentsByFaculty(faculty);

            assertThat(studentsByFaculty).hasSize(2);
            assertThat(studentsByFaculty).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT);
        }

        @Test
        @DisplayName("when facultyId=8 then should return empty list")
        void testGetByFacultyId4_ReturnEmptyList() {
            Faculty faculty = new Faculty();
            faculty.setId(8);

            List<Student> actualStudents = dao.getStudentsByFaculty(faculty);

            assertThat(actualStudents).isEmpty();
        }
    }
}