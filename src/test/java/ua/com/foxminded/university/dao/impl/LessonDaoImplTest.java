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
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "/schema.sql", "/lesson-test-data.sql"})
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
    private static final String MESSAGE_EXCEPTION = "Lesson id(5) not found";
    private static final String MESSAGE_UPDATE_MASK = "Can't update lesson " +
        "id(%s)";
    private static final String MESSAGE_DELETE_MASK = "Can't delete lesson " +
        "id(%s)";
    private static final String MESSAGE_UPDATE_EXCEPTION = "Can't update " +
        "because lesson id(5) not found";
    private static final String MESSAGE_DELETE_EXCEPTION = "Can't delete " +
        "because lesson id(5) not found";
    private static final String BUILDING = "building-1";
    private static final String ROOM_NUMBER = "812b";
    private static final boolean ACTIVE = true;
    private static final int YEAR = 2021;
    private static final int MONTH = 6;
    private static final int DAY = 12;
    private static final int HOUR = 14;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LessonDaoImpl dao;

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

            Lesson actualLesson = dao.getById(ID1).orElse(null);
            assertEquals(expectedLesson, actualLesson);
        }

        @Test
        @DisplayName("with id=5 should return DAOException")
        void testGetByIdLessonException() throws DAOException {
            DAOException exception = assertThrows(DAOException.class,
                () -> dao.getById(ID5));
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
        @DisplayName("with lesson id=1 should write new fields and getById(1) " +
            "return this fields")
        void testUpdateExistingLesson_WriteNewFields() throws DAOException {
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

            Lesson actualLesson = dao.getById(ID1).orElse(null);
            assertEquals(expectedLesson, actualLesson);
        }

        @Test
        @DisplayName("with lesson id=5 should write new log.warn with " +
            "expected message")
        void testUpdateNonExistingLesson_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(LessonDaoImpl.class);
            Lesson lesson = new Lesson();
            lesson.setId(ID5);
            Teacher teacher = new Teacher();
            teacher.setId(ID1);
            lesson.setTeacher(teacher);
            Course course = new Course();
            course.setId(ID2);
            lesson.setCourse(course);
            Room room = new Room();
            room.setId(ID3);
            lesson.setRoom(room);
            String expectedLog = String.format(MESSAGE_UPDATE_MASK, ID5);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.update(lesson));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_UPDATE_EXCEPTION, ex.getMessage());

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

        @Test
        @DisplayName("with lesson id=5 should write new log.warn with " +
            "expected message")
        void testDeleteNonExistingLesson_ExceptionWriteLogWarn() {
            LogCaptor logCaptor = LogCaptor.forClass(LessonDaoImpl.class);
            Lesson lesson = new Lesson();
            lesson.setId(ID5);
            String expectedLog = String.format(MESSAGE_DELETE_MASK, ID5);
            Exception ex = assertThrows(DAOException.class,
                () -> dao.delete(lesson));
            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("test 'addStudentToLesson' method")
    class addStudentToLessonTest {

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
            dao.removeStudentFromLesson(ID1, ID1);
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
            assertEquals(2, dao.getAllForTeacher(ID1).size());
        }

        @Test
        @DisplayName("when teacher_id = 2 then should return 1 lesson")
        void testGetAllByTeacherId2ReturnOneLessons() {
            assertEquals(1, dao.getAllForTeacher(ID2).size());
        }

        @Test
        @DisplayName("when teacher_id = 3 then should return empty List")
        void testGetAllByTeacherId3ReturnEmptyList() {
            assertTrue(dao.getAllForTeacher(3).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getAllByRoom' method")
    class getAllByRoomTest {

        @Test
        @DisplayName("when room_id = 1 then should return 3 lessons")
        void testRoomId1_ReturnThreeLessons() {
            assertEquals(3, dao.getAllForRoom(ID1).size());
        }

        @Test
        @DisplayName("when room_id = 2 then should return empty List")
        void testRoomId1_ReturnEmptyListLessons() {
            assertTrue(dao.getAllForRoom(ID2).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getAllByStudent' method")
    class getAllByStudentTest {

        @Test
        @DisplayName("when student_id = 1 then should return 1 lesson")
        void testStudentId1_ReturnOneLessons() {
            assertEquals(1, dao.getAllForStudent(ID1).size());
        }

        @Test
        @DisplayName("when student_id = 5 then should return empty List")
        void testStudentId5_ReturnEmptyListLessons() {
            assertTrue(dao.getAllForStudent(ID5).isEmpty());
        }
    }

    @Nested
    @DisplayName("test 'getAllWithFilter' method")
    class getAllWithFilterTest {

        @Test
        @DisplayName("when filter only for faculty should return list lessons" +
            " size 3")
        void testFilterOnlyFacultyId1() {
            LessonFilter filter = new LessonFilter();
            filter.setFacultyId(ID1);
            List<Lesson> lessons = dao.getAllWithFilter(filter);
            assertThat(lessons, hasSize(3));
        }

        @Test
        @DisplayName("when filter only for facultyId=2 should return empty list")
        void testFilterOnlyFacultyId2() {
            LessonFilter filter = new LessonFilter();
            filter.setFacultyId(ID2);
            List<Lesson> lessons = dao.getAllWithFilter(filter);
            assertThat(lessons, empty());
        }

        @Test
        @DisplayName("when filter facultyId and departmentId then filtering " +
            "should be only for department and with departmentId=1 should " +
            "return list size 3")
        void testFilterFacultyAndDepartmentId1() {
            LessonFilter filter = new LessonFilter();
            filter.setFacultyId(ID2);
            filter.setDepartmentId(ID1);
            List<Lesson> lessons = dao.getAllWithFilter(filter);
            assertThat(lessons, hasSize(3));
        }

        @Test
        @DisplayName("when filter facultyId, departmentId and teacherId then " +
            "filtering should be only for teacher and with teacherId=1 should" +
            " return list size 2")
        void testFilterFacultyDepartmentAndTeacherId1() {
            LessonFilter filter = new LessonFilter();
            filter.setFacultyId(ID2);
            filter.setDepartmentId(ID2);
            filter.setTeacherId(ID1);
            List<Lesson> lessons = dao.getAllWithFilter(filter);
            assertThat(lessons, hasSize(2));
            assertThat(lessons.get(0).getTeacher().getId(), equalTo(ID1));
        }

        @Test
        @DisplayName("when filter dateFrom and dateTo then filtering should " +
            "return lessons between this dates")
        void testFilterDateFromAndDateTo() {
            LessonFilter filter = new LessonFilter();
            filter.setDateFrom(LocalDateTime.of(2021, 6, 9, 8, 0));
            filter.setDateTo(LocalDateTime.of(2021, 6, 11, 20, 0));
            List<Lesson> lessons = dao.getAllWithFilter(filter);
            assertThat(lessons, hasSize(1));
            assertThat(lessons.get(0).getTimeStart(),
                is(equalTo(LocalDateTime.of(2021, 6, 10, 14, 0))));
        }
    }
}
