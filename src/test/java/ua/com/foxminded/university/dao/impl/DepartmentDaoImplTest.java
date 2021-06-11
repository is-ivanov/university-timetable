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

import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/department-test-data.sql" })
class DepartmentDaoImplTest {

    private static final String TABLE_NAME = "departments";
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
    private static final String NAME_DEAN = "Ivan";
    private static final String SURNAME_DEAN = "Petrov";
    private static final String PATRONYMIC_DEAN = "Sergeevich";
    private static final String TEST_DEPARTMENT_NAME = "Test Department";
    private static final String FIRST_STUDENT_LAST_NAME = "Smith";
    private static final String FIRST_STUDENT_PATRONYMIC = "Jr";
    private static final String MESSAGE_EXCEPTION = "Student not found: 4";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    DepartmentDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test department should CountRowsTable = 3")
        void testAddDepartment() {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
            faculty.setName(FIRST_FACULTY_NAME);
            Department department = new Department();
            department.setName(TEST_DEPARTMENT_NAME);
            department.setFaculty(faculty);

            dao.add(department);
            int expectedRowsInTable = 3;
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

}
