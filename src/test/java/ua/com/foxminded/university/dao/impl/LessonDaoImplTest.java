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

    private static final String TABLE_LESSONS = "lessons";
    private static final String TABLE_STUDENTS_LESSON = "students_lessons";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID3 = 3;
    private static final int ID5 = 5;
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
    private static final boolean ACTIVE = true;
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
            teacher.setId(ID1);
            Course course = new Course();
            course.setId(ID1);
            Room room = new Room();
            room.setId(ID1);
            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH, DAY, HOUR,
                    MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(1).plusMinutes(30);

            Lesson lesson = Lesson.builder().teacher(teacher).course(course)
                    .room(room).timeStart(timeStart).timeEnd(timeEnd).build();

            int expectedRowsInTable = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_LESSONS) + 1;
            dao.add(lesson);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_LESSONS);
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
            faculty.setId(ID1);
            faculty.setName(FACULTY_NAME);

            Department department = new Department(ID1,
                    DEPARTMENT_NAME,
                    faculty);

            Teacher teacher = new Teacher();
            teacher.setId(ID1);
            teacher.setFirstName(FIRST_TEACHER_FIRST_NAME);
            teacher.setLastName(FIRST_TEACHER_LAST_NAME);
            teacher.setPatronymic(FIRST_TEACHER_PATRONYMIC);
            teacher.setActive(ACTIVE);
            teacher.setDepartment(department);

            Course course = new Course(ID1, COURSE_NAME);
            Room room = new Room(ID1, BUILDING, ROOM_NUMBER);

            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH, DAY, HOUR,
                    MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(1).plusMinutes(30);

            Group group = new Group();
            group.setId(ID1);
            group.setName(FIRST_GROUP_NAME);
            group.setFaculty(faculty);

            List<Student> students = new LinkedList<>();
            Student firstStudent = new Student();
            firstStudent.setId(ID1);
            firstStudent.setFirstName(FIRST_STUDENT_FIRST_NAME);
            firstStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            firstStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            firstStudent.setActive(ACTIVE);
            firstStudent.setGroup(group);
            students.add(firstStudent);
            Student secondStudent = new Student();
            secondStudent.setId(ID2);
            secondStudent.setFirstName(SECOND_STUDENT_FIRST_NAME);
            secondStudent.setLastName(SECOND_STUDENT_LAST_NAME);
            secondStudent.setPatronymic(SECOND_STUDENT_PATRONYMIC);
            secondStudent.setActive(ACTIVE);
            secondStudent.setGroup(group);
            students.add(secondStudent);

            Lesson expectedLesson = Lesson.builder()
                    .id(ID1)
                    .teacher(teacher)
                    .course(course)
                    .room(room)
                    .students(students)
                    .timeStart(timeStart)
                    .timeEnd(timeEnd)
                    .build();

            Lesson actualLesson = dao.getById(ID1).get();
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
        @DisplayName("should return List with size = 3")
        void testGetAllLessons() {
            int expectedQuantityLessons = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_LESSONS);
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
            Faculty faculty = new Faculty(ID1, FACULTY_NAME);
            Department department = new Department(ID1, DEPARTMENT_NAME,
                    faculty);
            Teacher teacher = new Teacher();
            teacher.setId(ID2);
            teacher.setFirstName(SECOND_TEACHER_FIRST_NAME);
            teacher.setLastName(SECOND_TEACHER_LAST_NAME);
            teacher.setPatronymic(SECOND_TEACHER_PATRONYMIC);
            teacher.setActive(ACTIVE);
            teacher.setDepartment(department);

            Course course = new Course(ID1, COURSE_NAME);
            Room room = new Room(ID1, BUILDING, ROOM_NUMBER);
            LocalDateTime timeStart = LocalDateTime.of(YEAR, MONTH + 1, DAY - 1,
                    HOUR - 2, MINUTE, SECOND);
            LocalDateTime timeEnd = timeStart.plusHours(2);

            Group group = new Group();
            group.setId(ID1);
            group.setName(FIRST_GROUP_NAME);
            group.setFaculty(faculty);

            List<Student> students = new ArrayList<>();
            Student firstStudent = new Student();
            firstStudent.setId(ID1);
            firstStudent.setFirstName(FIRST_STUDENT_FIRST_NAME);
            firstStudent.setLastName(FIRST_STUDENT_LAST_NAME);
            firstStudent.setPatronymic(FIRST_STUDENT_PATRONYMIC);
            firstStudent.setActive(ACTIVE);
            firstStudent.setGroup(group);
            students.add(firstStudent);
            Student secondStudent = new Student();
            secondStudent.setId(ID2);
            secondStudent.setFirstName(SECOND_STUDENT_FIRST_NAME);
            secondStudent.setLastName(SECOND_STUDENT_LAST_NAME);
            secondStudent.setPatronymic(SECOND_STUDENT_PATRONYMIC);
            secondStudent.setActive(ACTIVE);
            secondStudent.setGroup(group);
            students.add(secondStudent);

            Lesson expectedLesson = Lesson.builder()
                    .id(ID1)
                    .teacher(teacher)
                    .students(students)
                    .course(course)
                    .room(room)
                    .timeStart(timeStart)
                    .timeEnd(timeEnd)
                    .build();
            dao.update(expectedLesson);

            Lesson actualLesson = dao.getById(ID1).get();
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
                    .countRowsInTable(jdbcTemplate, TABLE_LESSONS) - 1;
            Lesson lesson = new Lesson();
            lesson.setId(ID3);
            dao.delete(lesson);
            int actualQuantityLessons = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_LESSONS);
            assertEquals(expectedQuantityLessons, actualQuantityLessons);
        }
    }

    @Nested
    @DisplayName("test 'addStudentToLesson' method")
    class addStudentToLessonTest{

        @Test
        @DisplayName("after add studentId=1 to lessonId=2 should CountRowsTable must be one more than it was")
        void testAddStudentToLesson() {
            int expectedRowsInTable = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) + 1;
            dao.addStudentToLesson(ID2, ID1);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_STUDENTS_LESSON);
            assertEquals(expectedRowsInTable, actualRowsInTable);

        }
    }

    @Nested
    @DisplayName("test 'deleteAllStudentsFromLesson' method")
    class deleteStudentsFromLessonTest {

        @Test
        @DisplayName("after delete students from lesson_id=2 should CountRowsTable must be two less than it was")
        void testDeleteAllStudentsFromLesson() {
            int expectedRowsInTable = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) - 2;
            dao.deleteAllStudentsFromLesson(ID2);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_STUDENTS_LESSON);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }
    
    @Nested
    @DisplayName("test 'deleteStudentFromLesson' method")
    class deleteStudentFromLessonTest {

        @Test
        @DisplayName("after delete student_id=1 from lesson_id=1 should CountRowsTable must be one less than it was")
        void testDeleteStudentFromLesson() {
            int expectedRowsInTable = JdbcTestUtils
                    .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) - 1;
            dao.deleteStudentFromLesson(ID1, ID1);
            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
                    TABLE_STUDENTS_LESSON);
            assertEquals(expectedRowsInTable, actualRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getAllByTeacher' method")
    class getAllByTeacherTest {

        @Test
        @DisplayName("when teacher_id = 1 then should return 2 lessons")
        void testGetAllByTeacherId1ReturnTwoLessons() {
            assertEquals(2, dao.getAllByTeacher(ID1).size());
        }

        @Test
        @DisplayName("when teacher_id = 2 then should return 1 lesson")
        void testGetAllByTeacherId2ReturnOneLessons() {
            assertEquals(1, dao.getAllByTeacher(ID2).size());
        }

        @Test
        @DisplayName("when teacher_id = 3 then should return empty List")
        void testGetAllByTeacherId3ReturnEmptyList(){
            List<Lesson> lessons = new ArrayList<>();
            assertEquals(lessons, dao.getAllByTeacher(3));
        }
    }

    @Nested
    @DisplayName("test 'getAllByRoom' method")
    class getAllByRoomTest {

        @Test
        @DisplayName("when room_id = 1 then should return 3 lessons")
        void testRoomId1_ReturnThreeLessons() {
            assertEquals(3, dao.getAllByRoom(ID1).size());
        }

        @Test
        @DisplayName("when room_id = 2 then should return empty List")
        void testRoomId1_ReturnEmptyListLessons() {
            assertEquals(true, dao.getAllByRoom(ID2).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getAllByStudent' method")
    class getAllByStudentTest {

        @Test
        @DisplayName("when student_id = 1 then should return 1 lesson")
        void testStudentId1_ReturnOneLessons() {
            assertEquals(1, dao.getAllByStudent(ID1).size());
        }

        @Test
        @DisplayName("when student_id = 5 then should return empty List")
        void testStudentId5_ReturnEmptyListLessons() {
            assertEquals(true, dao.getAllByStudent(ID5).isEmpty());
        }
    }
}
