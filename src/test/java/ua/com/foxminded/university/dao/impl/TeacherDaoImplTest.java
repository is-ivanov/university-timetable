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

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/teacher-test-data.sql" })
class TeacherDaoImplTest {

    private static final String TABLE_NAME = "teachers";
    private static final String TEST_DEPARTMENT_NAME = "DepartmentName";
    private static final String TEST_TEACHER_FIRST_NAME = "John";
    private static final String TEST_TEACHER_LAST_NAME = "Dou";
    private static final String TEST_TEACHER_PATRONYMIC = "Ivanovich";
    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;
    private static final String FIRST_DEPARTMENT_NAME = "Chemistry";
    private static final String SECOND_DEPARTMENT_NAME = "Oil Technology";
    private static final String FIRST_FACULTY_NAME = "Chemical Technology";
    private static final String FIRST_TEACHER_NAME = "Mike";
    private static final String FIRST_TEACHER_LAST_NAME = "Smith";
    private static final String FIRST_TEACHER_PATRONYMIC = "Jr";
    private static final boolean FIRST_TEACHER_ACTIVE = true;
    private static final String MESSAGE_EXCEPTION = "Teacher not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    TeacherDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test teacher should CountRowsTable = 3")
        void testAddTeacher() {
            Department department = new Department();
            department.setId(FIRST_ID);
            department.setName(TEST_DEPARTMENT_NAME);

            Teacher teacher = new Teacher();
            teacher.setFirstName(TEST_TEACHER_FIRST_NAME);
            teacher.setLastName(TEST_TEACHER_LAST_NAME);
            teacher.setPatronymic(TEST_TEACHER_PATRONYMIC);
            teacher.setActive(FIRST_TEACHER_ACTIVE);
            teacher.setDepartment(department);

            dao.add(teacher);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return expected teacher)")
        void testGetByIdTeacher() throws DAOException {
            Faculty expectedFaculty = new Faculty();
            expectedFaculty.setId(FIRST_ID);
            expectedFaculty.setName(FIRST_FACULTY_NAME);

            Department expectedDepartment = new Department();
            expectedDepartment.setId(FIRST_ID);
            expectedDepartment.setName(FIRST_DEPARTMENT_NAME);
            expectedDepartment.setFaculty(expectedFaculty);

            Teacher expectedTeacher = new Teacher();
            expectedTeacher.setId(FIRST_ID);
            expectedTeacher.setFirstName(FIRST_TEACHER_NAME);
            expectedTeacher.setLastName(FIRST_TEACHER_LAST_NAME);
            expectedTeacher.setPatronymic(FIRST_TEACHER_PATRONYMIC);
            expectedTeacher.setActive(FIRST_TEACHER_ACTIVE);
            expectedTeacher.setDepartment(expectedDepartment);

            Teacher actualTeacher = dao.getById(FIRST_ID).get();
            assertEquals(expectedTeacher, actualTeacher);
        }

        @Test
        @DisplayName("with id=4 should return DAOException 'Teacher not found: 4'")
        void testGetByIdTeacherException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                    () -> dao.getById(4));
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
        @DisplayName("update properties teacher id=1 should write new fields and getById(1) return this fields")
        void testUpdateTeacher() throws DAOException {
            Faculty expectedFaculty = new Faculty(FIRST_ID,
                    FIRST_FACULTY_NAME);
            Department expectedDepartment = new Department(SECOND_ID,
                    SECOND_DEPARTMENT_NAME, expectedFaculty);
            Teacher expectedTeacher = new Teacher();
            expectedTeacher.setId(FIRST_ID);
            expectedTeacher.setFirstName(TEST_TEACHER_FIRST_NAME);
            expectedTeacher.setLastName(TEST_TEACHER_LAST_NAME);
            expectedTeacher.setPatronymic(TEST_TEACHER_PATRONYMIC);
            expectedTeacher.setDepartment(expectedDepartment);
            dao.update(expectedTeacher);
            Teacher actualTeacher = dao.getById(FIRST_ID).get();
            assertEquals(expectedTeacher, actualTeacher);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete teacher id=1 should delete one record and number records table should equals 1")
        void testDeleteTeacher() {
            int expectedQuantityTeachers = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Teacher teacher = new Teacher();
            teacher.setId(FIRST_ID);
            dao.delete(teacher);
            int actualQuantityStudents = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityTeachers, actualQuantityStudents);
        }
    }
}
