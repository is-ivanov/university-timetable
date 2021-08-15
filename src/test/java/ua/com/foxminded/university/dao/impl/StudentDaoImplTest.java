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
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/student-test-data.sql"})
class StudentDaoImplTest {

    private static final String TABLE_NAME = "students";
    private static final String TEST_GROUP_NAME = "GroupName";
    private static final String TEST_FACULTY_NAME = "FacultyName";
    private static final String TEST_STUDENT_FIRST_NAME = "Student First Name";
    private static final String TEST_STUDENT_LAST_NAME = "Student Last Name";
    private static final String TEST_STUDENT_PATRONYMIC = "Student patronymic";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID4 = 4;
    private static final String FIRST_GROUP_NAME = "20Eng-1";
    private static final String SECOND_GROUP_NAME = "21Ger-1";
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String FIRST_STUDENT_NAME = "Mike";
    private static final String FIRST_STUDENT_LAST_NAME = "Smith";
    private static final String FIRST_STUDENT_PATRONYMIC = "Jr";
    private static final String MESSAGE_EXCEPTION = "Student id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update student " +
        "id(%s)";
    private static final String MESSAGE_DELETE_MASK = "Can't delete student " +
        "id(%s)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because student id(4) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because student id(4) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test student should CountRowsTable = 4")
        void testAddStudent() {
            Faculty faculty = new Faculty();
            faculty.setId(ID1);
            faculty.setName(TEST_FACULTY_NAME);

            Group group = new Group();
            group.setId(ID1);
            group.setName(TEST_GROUP_NAME);
            group.setFaculty(faculty);

            Student student = new Student();
            student.setFirstName(TEST_STUDENT_FIRST_NAME);
            student.setLastName(TEST_STUDENT_LAST_NAME);
            student.setPatronymic(TEST_STUDENT_PATRONYMIC);
            student.setActive(true);
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
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Group expectedGroup = new Group();
            expectedGroup.setId(ID1);
            expectedGroup.setName(FIRST_GROUP_NAME);
            expectedGroup.setFaculty(expectedFaculty);

            Student expectedStudent = new Student();
            expectedStudent.setId(ID1);
            expectedStudent.setFirstName(FIRST_STUDENT_NAME);
            expectedStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            expectedStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            expectedStudent.setActive(true);
            expectedStudent.setGroup(expectedGroup);

            Student actualStudent = dao.getById(ID1).orElse(null);
            assertEquals(expectedStudent, actualStudent);
        }

        @Test
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdStudentException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                () -> dao.getById(ID4));
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
        @DisplayName("with student id=1 should write new fields and " +
            "getById(1) return expected student")
        void testUpdateExistingStudent_WriteNewFields() throws DAOException {
            Faculty expectedFaculty = new Faculty(ID2, SECOND_FACULTY_NAME);
            Group expectedGroup = new Group(ID2, SECOND_GROUP_NAME,
                expectedFaculty, false);
            Student expectedStudent = new Student();
            expectedStudent.setId(ID1);
            expectedStudent.setFirstName(TEST_STUDENT_FIRST_NAME);
            expectedStudent.setLastName(TEST_STUDENT_LAST_NAME);
            expectedStudent.setPatronymic(TEST_STUDENT_PATRONYMIC);
            expectedStudent.setGroup(expectedGroup);
            dao.update(expectedStudent);
            Student actualStudent = dao.getById(ID1).orElse(null);
            assertEquals(expectedStudent, actualStudent);
        }

        @Test
        @DisplayName("with student id=4 should write new log.warn with " +
            "expected message")
        void testUpdateNonExistingStudent_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(StudentDaoImpl.class);
            Student student = new Student();
            student.setId(ID4);
            student.setGroup(new Group(ID1, TEST_GROUP_NAME, new Faculty(),
                true));
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, ID4);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.update(student));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("with student id=1 should delete one record and number " +
            "records table should equals 1")
        void testDeleteExistingStudent_ReduceNumberRowsInTable() {
            int expectedQuantityStudents = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Student student = new Student();
            student.setId(ID1);
            dao.delete(student);
            int actualQuantityStudents = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }

        @Test
        @DisplayName("with student id=4 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingStudent_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(StudentDaoImpl.class);
            Student student = new Student();
            student.setId(ID4);
            student.setGroup(new Group(ID1, TEST_GROUP_NAME, new Faculty(),
                true));
            String expectedLog = String.format(MESSAGE_DELETE_MASK,
                student.getId());
            Exception ex = assertThrows(DAOException.class,
                () -> dao.delete(student));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getStudentsByLesson' method")
    class getStudentsByLessonTest {

        @Test
        @DisplayName("when lesson_id=1 then should return List with size = 1")
        void testGetAllStudentsByLesson() {
            Lesson lesson = new Lesson();
            lesson.setId(ID1);

            int expectedQuantityStudents = 1;
            int actualQuantityStudents = dao.getStudentsByLesson(lesson).size();
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }
    }

    @Nested
    @DisplayName("test 'getStudentsByGroup' method")
    class getStudentsByGroupTest {

        @Test
        @DisplayName("when group _id=1 then should return List with size = 2")
        void testGetStudentsByGroup() {
            Group group = new Group();
            group.setId(ID1);

            int expectedQuantityStudents = 2;
            int actualQuantityStudents = dao.getStudentsByGroup(group).size();
            assertEquals(expectedQuantityStudents, actualQuantityStudents);
        }
    }
}
