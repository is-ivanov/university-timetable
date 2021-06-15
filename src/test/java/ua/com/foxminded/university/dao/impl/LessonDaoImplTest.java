package ua.com.foxminded.university.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.entity.mapper.LessonExtractor;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestDbConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDbConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
        "/schema.sql", "/lesson-test-data.sql" })
class LessonDaoImplTest {

    private static final String TABLE_NAME = "lessons";
    private static final int FIRST_ID = 1;
    private static final int SECOND_ID = 2;
    private static final int THIRD_ID = 3;
    private static final String FIRST_GROUP_NAME = "21Chem-1";
    private static final String FACULTY_NAME = "Chemical Technology";
    private static final String DEPARTMENT_NAME = "Chemistry";
    private static final String COURSE_NAME = "Organic Chemistry";
    private static final String FIRST_TEACHER_FIRST_NAME = "Mike";
    private static final String FIRST_TEACHER_LAST_NAME = "Smith";
    private static final String FIRST_TEACHER_PATRONYMIC = "Jr";
    private static final String SECOND_TEACHER_FIRST_NAME = "Alan";
    private static final String SECOND_TEACHER_LAST_NAME = "Coppola";
    private static final String SECOND_TEACHER_PATRONYMIC = "Ford";
    private static final String FIRST_STUDENT_FIRST_NAME = "Dean";
    private static final String FIRST_STUDENT_LAST_NAME = "Thompson";
    private static final String FIRST_STUDENT_PATRONYMIC = "Ivanovich";
    private static final String SECOND_STUDENT_FIRST_NAME = "Petr";
    private static final String SECOND_STUDENT_LAST_NAME = "Petrov";
    private static final String SECOND_STUDENT_PATRONYMIC = "Petrovich";
    private static final String MESSAGE_EXCEPTION = "Lesson not found: 4";
    private static final String BUILDING = "building-1";
    private static final String ROOM_NUMBER = "812b";
    private static final int YEAR = 2021;
    private static final int MONTH = 6;
    private static final int DAY = 12;
    private static final int HOUR = 14;
    private static final int MINUTE = 00;
    private static final int SECOND = 00;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    LessonExtractor lessonExtractor;

    @Autowired
    LessonDaoImpl dao;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {


        @Test
        @DisplayName("add test lesson should CountRowsTable must be one more than it was")
        void testAddLesson() {
            Teacher teacher = new Teacher();
            teacher.setId(FIRST_ID);
            Course course = new Course();
            course.setId(FIRST_ID);
            Room room = new Room();
            room.setId(FIRST_ID);
            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH, DAY, HOUR,
                    MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(1).plusMinutes(30);

            Lesson lesson = Lesson.builder().teacher(teacher).course(course)
                    .room(room).timeStart(timeStart).timeEnd(timeEnd).build();

            int expectedRowsInTable = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) + 1;
            dao.add(lesson);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_NAME);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {


        @Test
        @DisplayName("with id=1 should return expected lesson)")
        void testGetByIdLesson() throws DAOException {
            Faculty faculty = new Faculty();
            faculty.setId(FIRST_ID);
            faculty.setName(FACULTY_NAME);

            Department department = new Department(FIRST_ID,
                    DEPARTMENT_NAME,
                    faculty);

            Teacher teacher = new Teacher();
            teacher.setId(FIRST_ID);
            teacher.setFirstName(FIRST_TEACHER_FIRST_NAME);
            teacher.setLastName(FIRST_TEACHER_LAST_NAME);
            teacher.setPatronymic(FIRST_TEACHER_PATRONYMIC);
            teacher.setDepartment(department);

            Course course = new Course(FIRST_ID, COURSE_NAME);
            Room room = new Room(FIRST_ID, BUILDING, ROOM_NUMBER);

            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH, DAY, HOUR,
                    MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(1).plusMinutes(30);

            Group group = new Group();
            group.setId(FIRST_ID);
            group.setName(FIRST_GROUP_NAME);
            group.setFaculty(faculty);

            List<Student> students = new LinkedList<>();
            Student firstStudent = new Student();
            firstStudent.setId(FIRST_ID);
            firstStudent.setFirstName(FIRST_STUDENT_FIRST_NAME);
            firstStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            firstStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            firstStudent.setGroup(group);
            students.add(firstStudent);
            Student secondStudent = new Student();
            secondStudent.setId(SECOND_ID);
            secondStudent.setFirstName(SECOND_STUDENT_FIRST_NAME);
            secondStudent.setLastName(SECOND_STUDENT_LAST_NAME);
            secondStudent.setPatronymic(SECOND_STUDENT_PATRONYMIC);
            secondStudent.setGroup(group);
            students.add(secondStudent);

            Lesson expectedLesson = Lesson.builder()
                    .id(FIRST_ID)
                    .teacher(teacher)
                    .course(course)
                    .room(room)
                    .students(students)
                    .timeStart(timeStart)
                    .timeEnd(timeEnd)
                    .build();

            Lesson actualLesson = dao.getById(FIRST_ID).get();
            assertEquals(expectedLesson, actualLesson);
        }

        @Test
        @DisplayName("with id=4 should return DAOException 'Lesson not found: 4'")
        void testGetByIdLessonException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                    () -> dao.getById(4));
            assertEquals(MESSAGE_EXCEPTION, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class getAllTest {

        @Test
        @DisplayName("should return List with size = 4")
        void testGetAllLessons() {
            int expectedQuantityLessons = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            int actualQuantityLessons = dao.getAll().size();
            assertEquals(expectedQuantityLessons, actualQuantityLessons);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("update properties lesson id=1 should write new fields and getById(1) return this fields")
        void testUpdateLesson() throws DAOException {
            Faculty faculty = new Faculty(FIRST_ID, FACULTY_NAME);
            Department department = new Department(FIRST_ID, DEPARTMENT_NAME,
                    faculty);
            Teacher teacher = new Teacher();
            teacher.setId(SECOND_ID);
            teacher.setFirstName(SECOND_TEACHER_FIRST_NAME);
            teacher.setLastName(SECOND_TEACHER_LAST_NAME);
            teacher.setPatronymic(SECOND_TEACHER_PATRONYMIC);
            teacher.setDepartment(department);

            Course course = new Course(FIRST_ID, COURSE_NAME);
            Room room = new Room(FIRST_ID, BUILDING, ROOM_NUMBER);
            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH + 1, DAY - 1,
                    HOUR - 2, MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(2);

            Group group = new Group();
            group.setId(FIRST_ID);
            group.setName(FIRST_GROUP_NAME);
            group.setFaculty(faculty);

            List<Student> students = new ArrayList<>();
            Student firstStudent = new Student();
            firstStudent.setId(FIRST_ID);
            firstStudent.setFirstName(FIRST_STUDENT_FIRST_NAME);
            firstStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            firstStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            firstStudent.setGroup(group);
            students.add(firstStudent);
            Student secondStudent = new Student();
            secondStudent.setId(SECOND_ID);
            secondStudent.setFirstName(SECOND_STUDENT_FIRST_NAME);
            secondStudent.setLastName(SECOND_STUDENT_LAST_NAME);
            secondStudent.setPatronymic(SECOND_STUDENT_PATRONYMIC);
            secondStudent.setGroup(group);
            students.add(secondStudent);

            Lesson expectedLesson = Lesson.builder()
                    .id(FIRST_ID)
                    .teacher(teacher)
                    .students(students)
                    .course(course)
                    .room(room)
                    .timeStart(timeStart)
                    .timeEnd(timeEnd)
                    .build();
            dao.update(expectedLesson);

            Lesson actualLesson = dao.getById(FIRST_ID).get();
            assertEquals(expectedLesson, actualLesson);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete lesson id=3 should delete one record and number records table should equals 2")
        void testDeleteLesson() {
            int expectedQuantityLessons = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME) - 1;
            Lesson lesson = new Lesson();
            lesson.setId(THIRD_ID);
            dao.delete(lesson);
            int actualQuantityLessons = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_NAME);
            assertEquals(expectedQuantityLessons, actualQuantityLessons);
        }
    }
}
