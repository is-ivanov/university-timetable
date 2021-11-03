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
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
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
    "/schema.sql", "/teacher-test-data.sql"})
class TeacherDaoImplTest {

    private static final String TABLE_NAME = "teachers";
    private static final String TEST_DEPARTMENT_NAME = "DepartmentName";
    private static final String TEST_TEACHER_FIRST_NAME = "John";
    private static final String TEST_TEACHER_LAST_NAME = "Dou";
    private static final String TEST_TEACHER_PATRONYMIC = "Ivanovich";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;
    private static final int ID4 = 4;
    private static final String FIRST_DEPARTMENT_NAME = "Chemistry";
    private static final String SECOND_DEPARTMENT_NAME = "Oil Technology";
    private static final String FIRST_FACULTY_NAME = "Chemical Technology";
    private static final String FIRST_TEACHER_NAME = "Mike";
    private static final String FIRST_TEACHER_LAST_NAME = "Smith";
    private static final String FIRST_TEACHER_PATRONYMIC = "Jr";
    private static final boolean FIRST_TEACHER_ACTIVE = true;
    private static final String MESSAGE_EXCEPTION = "Teacher id(4) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update teacher id(%s)";
    private static final String MESSAGE_DELETE_MASK = "Can't delete teacher id(%s)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because teacher id(4) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because teacher id(4) not found";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TeacherDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test teacher should add one row in table")
        void testAddTeacher() {
            Department department = new Department();
            department.setId(ID1);
            department.setName(TEST_DEPARTMENT_NAME);

            Teacher teacher = new Teacher();
            teacher.setFirstName(TEST_TEACHER_FIRST_NAME);
            teacher.setLastName(TEST_TEACHER_LAST_NAME);
            teacher.setPatronymic(TEST_TEACHER_PATRONYMIC);
            teacher.setActive(FIRST_TEACHER_ACTIVE);
            teacher.setDepartment(department);

            int rowsInTableBeforeAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);

            dao.add(teacher);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                TABLE_NAME);
            assertEquals(rowsInTableBeforeAdding + 1, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return expected teacher)")
        void testGetByIdTeacher() throws DaoException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(ID1);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Department expectedDepartment = new Department();
            expectedDepartment.setId(ID1);
            expectedDepartment.setName(FIRST_DEPARTMENT_NAME);
            expectedDepartment.setFaculty(expectedFaculty);

            Teacher expectedTeacher = new Teacher();
            expectedTeacher.setId(ID1);
            expectedTeacher.setFirstName(FIRST_TEACHER_NAME);
            expectedTeacher.setLastName(FIRST_TEACHER_LAST_NAME);
            expectedTeacher.setPatronymic(FIRST_TEACHER_PATRONYMIC);
            expectedTeacher.setActive(FIRST_TEACHER_ACTIVE);
            expectedTeacher.setDepartment(expectedDepartment);

            Teacher actualTeacher = dao.getById(ID1).orElse(null);
            assertEquals(expectedTeacher, actualTeacher);
        }

        @Test
        @DisplayName("with id=4 should return DAOException")
        void testGetByIdTeacherException() throws DaoException {
            DaoException exception = assertThrows(DaoException.class,
                () -> dao.getById(ID4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 2")
        void testGetAllTeachers() {
            int expectedQuantityTeachers = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityTeachers = dao.getAll().size();
            assertEquals(expectedQuantityTeachers, actualQuantityTeachers);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("with teacher id=1 should write new fields and " +
            "getById(1) return expected teacher")
        void testUpdateExistingTeacher_WriteNewFields() throws DaoException {
            Faculty expectedFaculty = new Faculty(ID1, FIRST_FACULTY_NAME);
            Department expectedDepartment = new Department(ID2,
                SECOND_DEPARTMENT_NAME, expectedFaculty);
            Teacher expectedTeacher = new Teacher();
            expectedTeacher.setId(ID1);
            expectedTeacher.setFirstName(TEST_TEACHER_FIRST_NAME);
            expectedTeacher.setLastName(TEST_TEACHER_LAST_NAME);
            expectedTeacher.setPatronymic(TEST_TEACHER_PATRONYMIC);
            expectedTeacher.setDepartment(expectedDepartment);
            dao.update(expectedTeacher);
            Teacher actualTeacher = dao.getById(ID1).orElse(null);
            assertEquals(expectedTeacher, actualTeacher);
        }

        @Test
        @DisplayName("with teacher id=4 should write new log.warn with " +
            "expected message")
        void testUpdateNonExistingTeacher_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(TeacherDaoImpl.class);
            Teacher teacher = new Teacher();
            teacher.setId(ID4);
            teacher.setDepartment(new Department(ID1, TEST_DEPARTMENT_NAME,
                new Faculty()));
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, ID4);
            Exception ex = assertThrows(DaoException.class,
                () -> dao.update(teacher));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("with teacher id=3 should delete one record and number " +
            "records table should one less before")
        void testDeleteExistingTeacher_ReduceNumberRowsInTable() {
            int expectedQuantityTeachers = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Teacher teacher = new Teacher();
            teacher.setId(ID3);
            dao.delete(teacher);
            int actualQuantityStudents = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityTeachers, actualQuantityStudents);
        }

        @Test
        @DisplayName("with teacher id=4 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingTeacher_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(TeacherDaoImpl.class);
            Teacher teacher = new Teacher();
            teacher.setId(ID4);
            teacher.setDepartment(new Department(ID1, TEST_DEPARTMENT_NAME,
                new Faculty()));
            String expectedLog = String.format(MESSAGE_DELETE_MASK, ID4);
            DaoException ex = assertThrows(DaoException.class,
                () -> dao.delete(teacher));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }

    }

    @Nested
    @DisplayName("test 'delete' method with parameter id")
    class deleteIdTest {

        @Test
        @DisplayName("with teacher id=3 should delete one record and number " +
            "records table should equals 2")
        void testDeleteExistingTeacher_ReduceNumberRowsInTable() {
            int expectedQuantityTeachers = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            dao.delete(ID3);
            int actualQuantityStudents = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityTeachers, actualQuantityStudents);
        }

        @Test
        @DisplayName("with teacher id=4 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingTeacher_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(TeacherDaoImpl.class);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, ID4);
            DaoException ex = assertThrows(DaoException.class,
                () -> dao.delete(ID4));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }

    }

    @Nested
    @DisplayName("test 'getAllByDepartment' method")
    class getAllByDepartmentTest {

        @Test
        @DisplayName("with department id=1 should return List with size = 2")
        void testGetAllTeachersByDepartmentId1() {
            int expectedQuantityTeachers = 2;
            List<Teacher> actualTeachers = dao.getAllByDepartment(ID1);
            assertEquals(expectedQuantityTeachers, actualTeachers.size());
            assertEquals(FIRST_TEACHER_NAME, actualTeachers.get(0).getFirstName());
            assertEquals(FIRST_TEACHER_LAST_NAME,
                actualTeachers.get(0).getLastName());
        }

        @Test
        @DisplayName("with department id=4 should return empty List")
        void testGetAllTeachersByDepartmentId4() {
            assertTrue(dao.getAllByDepartment(ID4).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getAllByFaculty' method")
    class getAllByFacultyTest {

        @Test
        @DisplayName("with faculty id=1 should return List with size = 3")
        void testGetAllTeachersByFacultyId1() {
            int expectedQuantityTeachers =
                JdbcTestUtils.countRowsInTable(jdbcTemplate, TABLE_NAME);
            List<Teacher> actualTeachers = dao.getAllByFaculty(ID1);
            assertEquals(expectedQuantityTeachers, actualTeachers.size());
        }

        @Test
        @DisplayName("with faculty id=2 should return empty List")
        void testGetAllTeachersByFacultyId2() {
            assertTrue(dao.getAllByFaculty(ID2).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getFreeTeachersOnLessonTime' method")
    class GetFreeTeachersOnLessonTimeTest {

        @Test
        @DisplayName("when new lesson starts at 14:30 12-09-2021 then return " +
            "list with one teacher (id=3)")
        void testReturnEmptyList() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 14, 30);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Teacher> actualTeachers =
                dao.getFreeTeachersOnLessonTime(startTime, endTime);
            assertThat(actualTeachers, hasSize(1));
            Teacher freeTeacher = actualTeachers.get(0);
            assertThat(freeTeacher.getId(), is(equalTo(3)));
        }

        @Test
        @DisplayName("when new lesson starts at 15:45 12-09-2021 then return " +
            "list with two teachers (id=1 and id=3)")
        void testReturnOneTeacher() {
            LocalDateTime startTime = LocalDateTime.of(2021, 9, 12, 15, 45);
            LocalDateTime endTime = startTime.plusMinutes(90);

            List<Teacher> actualTeachers =
                dao.getFreeTeachersOnLessonTime(startTime, endTime);

            assertThat(actualTeachers, hasSize(2));

            List<Integer> freeTeachersIds = actualTeachers.stream()
                .map(Teacher::getId)
                .collect(Collectors.toList());
            assertThat(freeTeachersIds, hasItem(ID1));
            assertThat(freeTeachersIds, hasItem(ID3));
            assertThat(freeTeachersIds, not(hasItem(ID2)));
        }
    }

}
