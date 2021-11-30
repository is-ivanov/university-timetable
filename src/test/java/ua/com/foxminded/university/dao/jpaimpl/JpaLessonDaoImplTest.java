package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.dao.interfaces.LessonDao;
import ua.com.foxminded.university.dao.interfaces.RoomDao;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/lesson-test-data.sql")
class JpaLessonDaoImplTest extends IntegrationTestBase {

    private static final String TABLE_LESSONS = "lessons";
    private static final String TABLE_STUDENTS_LESSON = "students_lessons";
    private static final String MESSAGE_EXCEPTION = "Lesson id(5) not found";
    private static final String MESSAGE_UPDATE_MASK =
        "Can't update lesson id(%s)";
    private static final String MESSAGE_DELETE_MASK =
        "Can't delete lesson id(%s)";
    private static final String MESSAGE_UPDATE_EXCEPTION =
        "Can't update because lesson id(5) not found";
    private static final String MESSAGE_DELETE_EXCEPTION =
        "Can't delete because lesson id(5) not found";
    private static final int YEAR = 2021;
    private static final int MONTH = 6;
    private static final int DAY = 12;
    private static final int HOUR = 14;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;


    @Autowired
    private LessonDao dao;

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DisplayName("test 'add' method")
    class addTest {

        @Test
        @DisplayName("add test lesson should CountRowsTable must be one more than it was")
        void testAddLesson() {
            Lesson lesson = createTestLesson();
            int expectedRowsInTable = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_LESSONS) + 1;

            dao.add(lesson);

            List<Lesson> lessons = dao.getAll();
            assertThat(lessons).hasSize(expectedRowsInTable);
            Lesson savedLesson = dao.getById(101).get();
            assertThat(savedLesson.getCourse()).isEqualTo(lesson.getCourse());
            assertThat(savedLesson.getRoom()).isEqualTo(lesson.getRoom());
            assertThat(savedLesson.getTeacher()).isEqualTo(lesson.getTeacher());
            assertThat(savedLesson.getTimeStart()).isEqualTo(lesson.getTimeStart());
            assertThat(savedLesson.getTimeEnd()).isEqualTo(lesson.getTimeEnd());

        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("with id=1 should return expected lesson)")
        void testGetByIdLesson() {
            Lesson actualLesson = dao.getById(LESSON_ID1).get();

            assertThat(actualLesson.getTimeStart()).isEqualTo(DATE_START_FIRST_LESSON);
            assertThat(actualLesson.getTimeEnd()).isEqualTo(DATE_END_FIRST_LESSON);

            Teacher actualTeacher = actualLesson.getTeacher();
            assertThat(actualTeacher.getId()).isEqualTo(TEACHER_ID1);
            assertThat(actualTeacher.getFullName()).isEqualTo(FULL_NAME_FIRST_TEACHER);

            Room actualRoom = actualLesson.getRoom();
            assertThat(actualRoom.getId()).isEqualTo(ROOM_ID1);
            assertThat(actualRoom.getBuildingAndRoom())
                .isEqualTo(BUILDING_AND_NUMBER_FIRST_ROOM);

            Course actualCourse = actualLesson.getCourse();
            assertThat(actualCourse.getId()).isEqualTo(COURSE_ID1);
            assertThat(actualCourse.getName()).isEqualTo(NAME_FIRST_COURSE);

        }

        @Test
        @DisplayName("with id=5 should return empty Optional")
        void testGetByIdLesson_EmptyOptional() {
            Optional<Lesson> lessonOptional = dao.getById(5);
            assertThat(lessonOptional).isEmpty();
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
            List<Lesson> lessons = dao.getAll();
            assertThat(lessons).hasSize(expectedQuantityLessons);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2, LESSON_ID3);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class updateTest {

        @Test
        @DisplayName("with lesson id=1 should write new fields and getById(1) " +
            "return this fields")
        void testUpdateExistingLesson_WriteNewFields() {
            Lesson lessonForChange = dao.getById(LESSON_ID1).get();

            Teacher newTeacher = teacherDao.getById(TEACHER_ID2).get();
            Course newCourse = courseDao.getById(COURSE_ID2).get();
            Room newRoom = roomDao.getById(ROOM_ID2).get();

            lessonForChange.setCourse(newCourse);
            lessonForChange.setRoom(newRoom);
            lessonForChange.setTeacher(newTeacher);

            dao.update(lessonForChange);

            Lesson actualLesson = dao.getById(LESSON_ID1).get();

            assertThat(actualLesson.getCourse()).isEqualTo(newCourse);
            assertThat(actualLesson.getRoom()).isEqualTo(newRoom);
            assertThat(actualLesson.getTeacher()).isEqualTo(newTeacher);
        }

        @Test
        @DisplayName("with lesson id=5 should write new lesson with id from sequence")
        void testUpdateNonExistingLesson_CreateNewLesson() {
            Lesson expectedLesson = createTestLesson(5);

            dao.update(expectedLesson);

            Lesson actualLesson = dao.getById(101).get();
            assertThat(actualLesson.getCourse()).isEqualTo(expectedLesson.getCourse());
            assertThat(actualLesson.getRoom()).isEqualTo(expectedLesson.getRoom());
            assertThat(actualLesson.getTeacher()).isEqualTo(expectedLesson.getTeacher());
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class deleteTest {

        @Test
        @DisplayName("delete lesson_id3 should delete one record and number " +
            "records table should equals 2")
        void testDeleteLesson() {
            int expectedQuantityLessons = JdbcTestUtils
                .countRowsInTable(jdbcTemplate, TABLE_LESSONS) - 1;
            Lesson lesson = dao.getById(LESSON_ID3).get();

            dao.delete(lesson);

            List<Lesson> actualLessons = dao.getAll();
            assertThat(actualLessons).hasSize(expectedQuantityLessons);
            assertThat(actualLessons)
        }

//        @Test
//        @DisplayName("with lesson id=5 should write new log.warn with " +
//            "expected message")
//        void testDeleteNonExistingLesson_ExceptionWriteLogWarn() {
//            LogCaptor logCaptor = LogCaptor.forClass(LessonDaoImpl.class);
//            Lesson lesson = new Lesson();
//            lesson.setId(ID5);
//            String expectedLog = String.format(MESSAGE_DELETE_MASK, ID5);
//            Exception ex = assertThrows(DaoException.class,
//                () -> dao.delete(lesson));
//            assertEquals(expectedLog, logCaptor.getWarnLogs().get(0));
//            assertEquals(MESSAGE_DELETE_EXCEPTION, ex.getMessage());
//        }
    }
//
//    @Nested
//    @DisplayName("test 'addStudentToLesson' method")
//    class addStudentToLessonTest {
//
//        @Test
//        @DisplayName("after add studentId=1 to lessonId=2 should CountRowsTable must be one more than it was")
//        void testAddStudentToLesson() {
//            int expectedRowsInTable = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) + 1;
//            dao.addStudentToLesson(ID2, ID1);
//            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
//                TABLE_STUDENTS_LESSON);
//            assertEquals(expectedRowsInTable, actualRowsInTable);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'deleteAllStudentsFromLesson' method")
//    class deleteStudentsFromLessonTest {
//
//        @Test
//        @DisplayName("after delete students from lesson_id=2 should CountRowsTable must be two less than it was")
//        void testDeleteAllStudentsFromLesson() {
//            int expectedRowsInTable = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) - 2;
//            dao.deleteAllStudentsFromLesson(ID2);
//            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
//                TABLE_STUDENTS_LESSON);
//            assertEquals(expectedRowsInTable, actualRowsInTable);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'deleteStudentFromLesson' method")
//    class deleteStudentFromLessonTest {
//
//        @Test
//        @DisplayName("after delete student_id=1 from lesson_id=1 should CountRowsTable must be one less than it was")
//        void testDeleteStudentFromLesson() {
//            int expectedRowsInTable = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_STUDENTS_LESSON) - 1;
//            dao.removeStudentFromLesson(ID1, ID1);
//            int actualRowsInTable = JdbcTestUtils.countRowsInTable(jdbcTemplate,
//                TABLE_STUDENTS_LESSON);
//            assertEquals(expectedRowsInTable, actualRowsInTable);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllByTeacher' method")
//    class getAllByTeacherTest {
//
//        @Test
//        @DisplayName("when teacher_id = 1 then should return 2 lessons")
//        void testGetAllByTeacherId1ReturnTwoLessons() {
//            assertEquals(2, dao.getAllForTeacher(ID1).size());
//        }
//
//        @Test
//        @DisplayName("when teacher_id = 2 then should return 1 lesson")
//        void testGetAllByTeacherId2ReturnOneLessons() {
//            assertEquals(1, dao.getAllForTeacher(ID2).size());
//        }
//
//        @Test
//        @DisplayName("when teacher_id = 3 then should return empty List")
//        void testGetAllByTeacherId3ReturnEmptyList() {
//            assertTrue(dao.getAllForTeacher(3).isEmpty());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllByRoom' method")
//    class getAllByRoomTest {
//
//        @Test
//        @DisplayName("when room_id = 1 then should return 3 lessons")
//        void testRoomId1_ReturnThreeLessons() {
//            assertEquals(3, dao.getAllForRoom(ID1).size());
//        }
//
//        @Test
//        @DisplayName("when room_id = 2 then should return empty List")
//        void testRoomId1_ReturnEmptyListLessons() {
//            assertTrue(dao.getAllForRoom(ID2).isEmpty());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllByStudent' method")
//    class getAllByStudentTest {
//
//        @Test
//        @DisplayName("when student_id = 1 then should return 1 lesson")
//        void testStudentId1_ReturnOneLessons() {
//            assertEquals(1, dao.getAllForStudent(ID1).size());
//        }
//
//        @Test
//        @DisplayName("when student_id = 5 then should return empty List")
//        void testStudentId5_ReturnEmptyListLessons() {
//            assertTrue(dao.getAllForStudent(ID5).isEmpty());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAllWithFilter' method")
//    class getAllWithFilterTest {
//
//        @Test
//        @DisplayName("when filter only for faculty should return list lessons" +
//            " size 3")
//        void testFilterOnlyFacultyId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(ID1);
//            List<Lesson> lessons = dao.getAllWithFilter(filter);
//            assertThat(lessons, hasSize(3));
//        }
//
//        @Test
//        @DisplayName("when filter only for facultyId=2 should return empty list")
//        void testFilterOnlyFacultyId2() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(ID2);
//            List<Lesson> lessons = dao.getAllWithFilter(filter);
//            assertThat(lessons, empty());
//        }
//
//        @Test
//        @DisplayName("when filter facultyId and departmentId then filtering " +
//            "should be only for department and with departmentId=1 should " +
//            "return list size 3")
//        void testFilterFacultyAndDepartmentId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(ID2);
//            filter.setDepartmentId(ID1);
//            List<Lesson> lessons = dao.getAllWithFilter(filter);
//            assertThat(lessons, hasSize(3));
//        }
//
//        @Test
//        @DisplayName("when filter facultyId, departmentId and teacherId then " +
//            "filtering should be only for teacher and with teacherId=1 should" +
//            " return list size 2")
//        void testFilterFacultyDepartmentAndTeacherId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(ID2);
//            filter.setDepartmentId(ID2);
//            filter.setTeacherId(ID1);
//            List<Lesson> lessons = dao.getAllWithFilter(filter);
//            assertThat(lessons, hasSize(2));
//            assertThat(lessons.get(0).getTeacher().getId(), equalTo(ID1));
//        }
//
//        @Test
//        @DisplayName("when filter dateFrom and dateTo then filtering should " +
//            "return lessons between this dates")
//        void testFilterDateFromAndDateTo() {
//            LessonFilter filter = new LessonFilter();
//            filter.setDateFrom(LocalDateTime.of(2021, 6, 9, 8, 0));
//            filter.setDateTo(LocalDateTime.of(2021, 6, 11, 20, 0));
//            List<Lesson> lessons = dao.getAllWithFilter(filter);
//            assertThat(lessons, hasSize(1));
//            assertThat(lessons.get(0).getTimeStart(),
//                is(equalTo(LocalDateTime.of(2021, 6, 10, 14, 0))));
//        }
//    }

}