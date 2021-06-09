package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
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
    private static final String FIRST_FACULTY_NAME = "Foreign Language";
    private static final String SECOND_FACULTY_NAME = "Chemical Technology";
    private static final String NAME_DEAN = "Ivan";
    private static final String SURNAME_DEAN = "Petrov";
    private static final String PATRONYMIC_DEAN = "Sergeevich";
    private static final String FIRST_STUDENT_NAME = "Mike";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    StudentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test student should CountRowsTable = 3")
        void testAddStudent() {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
            faculty.setName(TEST_FACULTY_NAME);
            Teacher dean = new Teacher();
            faculty.setDean(dean);

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
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

//    @Nested
//    @DisplayName("test 'getById' method")
//    class getByIdTest {
//
//        @Test
//        @DisplayName("with id=1 should return group (1, '20Eng-1', faculty id=1, dean id=1)")
//        void testGetByIdGroup() throws DAOException {
//            Teacher expectedDean = new Teacher();
//            expectedDean.setId(FIRST_ID);
//            expectedDean.setFirstName(NAME_DEAN);
//            expectedDean.setPatronymic(PATRONYMIC_DEAN);
//            expectedDean.setLastName(SURNAME_DEAN);
//
//            Faculty expectedFaculty = new Faculty();
//            expectedFaculty.setId(FIRST_ID);
//            expectedFaculty.setName(FIRST_FACULTY_NAME);
//            expectedFaculty.setDean(expectedDean);
//
//            Group expectedGroup = new Group(FIRST_ID, FIRST_GROUP_NAME,
//                    expectedFaculty);
//
//            Group actualGroup = dao.getById(FIRST_ID).get();
//            assertEquals(expectedGroup, actualGroup);
//        }
//
//        @Test
//        @DisplayName("with id=4 should return DAOException 'Group not found: 4'")
//        void testGetByIdGroupException() throws DAOException {
//            DAOException exception = assertThrows(DAOException.class,
//                    () -> dao.getById(4));
//            assertEquals("Group not found: 4", exception.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAll' method")
//    class getAllTest {
//
//        @Test
//        @DisplayName("should return List with size = 2")
//        void testGetAllGroups() {
//            int expectedQuantityGroups = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            int actualQuantityGroups = dao.getAll().size();
//            assertEquals(expectedQuantityGroups, actualQuantityGroups);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class updateTest {
//
//        @Test
//        @DisplayName("update name and faculty_id faculty id=1 should write new fields and getById(1) return this fields")
//        void testUpdateGroup() throws DAOException {
//            Teacher dean = new Teacher();
//            Faculty expectedFaculty = new Faculty(SECOND_ID,
//                    SECOND_FACULTY_NAME,
//                    dean);
//            Group expectedGroup = new Group(FIRST_ID, TEST_GROUP_NAME,
//                    expectedFaculty);
//            dao.update(expectedGroup);
//            Group actualGroup = dao.getById(FIRST_ID).get();
//            assertEquals(expectedGroup, actualGroup);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class deleteTest {
//
//        @Test
//        @DisplayName("delete group id=1 should delete one record and number records table should equals 1")
//        void testDeleteGroup() {
//            int expectedQuantityGroups = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
//            Teacher dean = new Teacher();
//            dean.setId(FIRST_ID);
//            dean.setFirstName(NAME_DEAN);
//            dean.setPatronymic(PATRONYMIC_DEAN);
//            dean.setLastName(SURNAME_DEAN);
//
//            Faculty faculty = new Faculty(FIRST_ID, FIRST_FACULTY_NAME, dean);
//
//            Group group = new Group(FIRST_ID, FIRST_GROUP_NAME, faculty);
//            dao.delete(group);
//            int actualQuantityGroups = JdbcTestUtils
//                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
//            assertEquals(expectedQuantityGroups, actualQuantityGroups);
//        }
//    }
}
