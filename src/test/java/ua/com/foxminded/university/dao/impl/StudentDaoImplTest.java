package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/student-test-data.sql" })
class StudentDaoImplTest {

    private static final String TABLE_NAME = "students";
    private static final String TEST_GROUP_NAME = "GroupName";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final String TEST_STUDENT_FIRST_NAME = "Student First Name";
    private static final String TEST_STUDENT_LAST_NAME = "Student Last Name";
    private static final String TEST_STUDENT_PATRONYMIC = "Student patronymic";
    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;
    private static final String FIRST_GROUP_NAME = "20Eng-1";
    private static final String SECOND_GROUP_NAME = "21Ger-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String FIRST_STUDENT_NAME = "Mike";
    private static final String FIRST_STUDENT_LAST_NAME = "Smith";
    private static final String FIRST_STUDENT_PATRONYMIC = "Jr";
    private static final String MESSAGE_EXCEPTION = "Student not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    StudentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test student should CountRowsTable = 4")
        void testAddStudent() {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
            faculty.setName(TEST_FACULTY_NAME);

            Group group = new Group();
            group.setId(FIRST_ID);
            group.setName(TEST_GROUP_NAME);
            group.setFaculty(faculty);

            Student student = new Student();
            student.setFirstName(TEST_STUDENT_FIRST_NAME);
            student.setLastName(TEST_STUDENT_LAST_NAME);
            student.setPatronymic(TEST_STUDENT_PATRONYMIC);
            student.setGroup(group);

            dao.add(student);
            int expectedRowsInTable = 4;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return expected student)")
        void testGetByIdStudent() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group expectedGroup = new Group();
            expectedGroup.setId(FIRST_ID);
            expectedGroup.setName(FIRST_GROUP_NAME);
            expectedGroup.setFaculty(expectedFaculty);

            Student expectedStudent = new Student();
            expectedStudent.setId(FIRST_ID);
            expectedStudent.setFirstName(FIRST_STUDENT_NAME);
            expectedStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            expectedStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            expectedStudent.setGroup(expectedGroup);

            Student actualStudent = dao.getById(FIRST_ID).get();
            assertEquals(expectedStudent, actualStudent);
        }

        @Test
        @DisplayName("with id=4 should return DAOException 'Student not found: 4'")
        void testGetByIdStudentException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                    () -> dao.getById(4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllStudents() {
            int expectedQuantityStudents = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityStudents = dao.getAll().size();
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update properties student id=1 should write new fields and getById(1) return this fields")
        void testUpdateStudent() throws DAOException {
            Faculty expectedFaculty = new Faculty(SECOND_ID,
                    SECOND_FACULTY_NAME);
            Group expectedGroup = new Group(SECOND_ID, SECOND_GROUP_NAME,
                    expectedFaculty);
            Student expectedStudent = new Student();
            expectedStudent.setId(FIRST_ID);
            expectedStudent.setFirstName(TEST_STUDENT_FIRST_NAME);
            expectedStudent.setLastName(TEST_STUDENT_LAST_NAME);
            expectedStudent.setPatronymic(TEST_STUDENT_PATRONYMIC);
            expectedStudent.setGroup(expectedGroup);
            dao.update(expectedStudent);
            Student actualStudent = dao.getById(FIRST_ID).get();
            assertEquals(expectedStudent, actualStudent);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete student id=1 should delete one record and number records table should equals 1")
        void testDeleteStudent() {
            int expectedQuantityStudents = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Student student = new Student();
            student.setId(FIRST_ID);
            dao.delete(student);
            int actualQuantityStudents = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }
    }

    @Nested
    @DisplayName("test 'getAllForLesson' method")
    class getAllForLessonTest {

        @Test
        @DisplayName("should return List with size = 1")
        void testGetAllStudentsForLesson() {
            Lesson lesson = new Lesson();
            lesson.setId(FIRST_ID);

            int expectedQuantityStudents = 1;
            int actualQuantityStudents = dao.getAllForLesson(lesson).size();
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }
    }
}
