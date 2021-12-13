package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.com.foxminded.university.dao.interfaces.CourseRepository;
import ua.com.foxminded.university.dao.interfaces.LessonRepository;
import ua.com.foxminded.university.dao.interfaces.RoomRepository;
import ua.com.foxminded.university.dao.interfaces.TeacherRepository;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.domain.filter.LessonFilter;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/lesson-test-data.sql")
class LessonJpaRepositoryTest extends IntegrationTestBase {

    private static final String TABLE_LESSONS = "lessons";

    @Autowired
    private LessonRepository dao;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Nested
//    @DisplayName("test 'add' method")
//    class AddTest {
//
//        @Test
//        @DisplayName("add test lesson should CountRowsTable must be one more than it was")
//        void testAddLesson() {
//            Lesson lesson = createTestLesson();
//            int expectedRowsInTable = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_LESSONS) + 1;
//
//            dao.add(lesson);
//
//            List<Lesson> lessons = dao.getAll();
//            assertThat(lessons).hasSize(expectedRowsInTable);
//            Lesson savedLesson = dao.getById(101).get();
//            assertThat(savedLesson.getCourse()).isEqualTo(lesson.getCourse());
//            assertThat(savedLesson.getRoom()).isEqualTo(lesson.getRoom());
//            assertThat(savedLesson.getTeacher()).isEqualTo(lesson.getTeacher());
//            assertThat(savedLesson.getTimeStart()).isEqualTo(lesson.getTimeStart());
//            assertThat(savedLesson.getTimeEnd()).isEqualTo(lesson.getTimeEnd());
//
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getById' method")
//    class GetByIdTest {
//
//        @Test
//        @DisplayName("with id=1 should return expected lesson)")
//        void testGetByIdLesson() {
//            Lesson actualLesson = dao.getById(LESSON_ID1).get();
//
//            assertThat(actualLesson.getTimeStart()).isEqualTo(DATE_START_FIRST_LESSON);
//            assertThat(actualLesson.getTimeEnd()).isEqualTo(DATE_END_FIRST_LESSON);
//
//            Teacher actualTeacher = actualLesson.getTeacher();
//            assertThat(actualTeacher.getId()).isEqualTo(TEACHER_ID1);
//            assertThat(actualTeacher.getFullName()).isEqualTo(FULL_NAME_FIRST_TEACHER);
//
//            Room actualRoom = actualLesson.getRoom();
//            assertThat(actualRoom.getId()).isEqualTo(ROOM_ID1);
//            assertThat(actualRoom.getBuildingAndRoom())
//                .isEqualTo(BUILDING_AND_NUMBER_FIRST_ROOM);
//
//            Course actualCourse = actualLesson.getCourse();
//            assertThat(actualCourse.getId()).isEqualTo(COURSE_ID1);
//            assertThat(actualCourse.getName()).isEqualTo(NAME_FIRST_COURSE);
//
//        }
//
//        @Test
//        @DisplayName("with id=5 should return empty Optional")
//        void testGetByIdLesson_EmptyOptional() {
//            Optional<Lesson> lessonOptional = dao.getById(5);
//            assertThat(lessonOptional).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getAll' method")
//    class GetAllTest {
//
//        @Test
//        @DisplayName("should return List with size = 3")
//        void testGetAllLessons() {
//            int expectedQuantityLessons = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_LESSONS);
//            List<Lesson> lessons = dao.getAll();
//            assertThat(lessons).hasSize(expectedQuantityLessons);
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID2, LESSON_ID3);
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'update' method")
//    class UpdateTest {
//
//        @Test
//        @DisplayName("with lesson id=1 should write new fields and getById(1) " +
//            "return this fields")
//        void testUpdateExistingLesson_WriteNewFields() {
//            Lesson lessonForChange = dao.getById(LESSON_ID1).get();
//
//            Teacher newTeacher = teacherRepository.findById(TEACHER_ID2).get();
//            Course newCourse = courseRepository.getById(COURSE_ID2);
//            Room newRoom = roomRepository.getById(ROOM_ID2);
//
//            lessonForChange.setCourse(newCourse);
//            lessonForChange.setRoom(newRoom);
//            lessonForChange.setTeacher(newTeacher);
//
//            dao.update(lessonForChange);
//
//            Lesson actualLesson = dao.getById(LESSON_ID1).get();
//
//            assertThat(actualLesson.getCourse()).isEqualTo(newCourse);
//            assertThat(actualLesson.getRoom()).isEqualTo(newRoom);
//            assertThat(actualLesson.getTeacher()).isEqualTo(newTeacher);
//        }
//
//        @Test
//        @DisplayName("with lesson id=5 should write new lesson with id from sequence")
//        void testUpdateNonExistingLesson_CreateNewLesson() {
//            Lesson expectedLesson = createTestLesson(5);
//
//            dao.update(expectedLesson);
//
//            Lesson actualLesson = dao.getById(101).get();
//            assertThat(actualLesson.getCourse()).isEqualTo(expectedLesson.getCourse());
//            assertThat(actualLesson.getRoom()).isEqualTo(expectedLesson.getRoom());
//            assertThat(actualLesson.getTeacher()).isEqualTo(expectedLesson.getTeacher());
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'delete' method")
//    class DeleteTest {
//
//        @Test
//        @DisplayName("delete lesson_id3 should delete one record and number " +
//            "records table should equals 2")
//        void testDeleteLesson() {
//            int expectedQuantityLessons = JdbcTestUtils
//                .countRowsInTable(jdbcTemplate, TABLE_LESSONS) - 1;
//            Lesson lesson = dao.getById(LESSON_ID3).get();
//
//            dao.delete(lesson);
//
//            List<Lesson> actualLessons = dao.getAll();
//            assertThat(actualLessons).hasSize(expectedQuantityLessons);
//            assertThat(actualLessons).extracting(Lesson::getId)
//                .doesNotContain(LESSON_ID3);
//        }
//
//    }

    @Nested
    @DisplayName("test 'deleteAllStudentsFromLesson' method")
    class DeleteStudentsFromLessonTest {

        @Test
        @DisplayName("after delete students from lesson_id2 Set students in the " +
            "lesson should be empty")
        void testDeleteAllStudentsFromLesson() {
            dao.deleteAllStudentsFromLesson(LESSON_ID2);

            Lesson lesson = dao.findById(LESSON_ID2).get();
            assertThat(lesson.getStudents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'deleteStudentFromLesson' method")
    class DeleteStudentFromLessonTest {

        @Test
        @DisplayName("after delete student_id1 from lesson_id1 Set students " +
            "from lesson should must be one less than it was")
        void testDeleteStudentFromLesson() {
            int expectedNumberStudents = 1;

            dao.removeStudentFromLesson(LESSON_ID1, STUDENT_ID1);

            Lesson lessonAfter = dao.findById(LESSON_ID1).get();
            Set<Student> studentsAfter = lessonAfter.getStudents();

            assertThat(studentsAfter).hasSize(expectedNumberStudents);
            assertThat(studentsAfter).extracting(Student::getId)
                .doesNotContain(STUDENT_ID1);
        }
    }

    @Nested
    @DisplayName("test 'getAllForTeacher' method")
    class GetAllForTeacherTest {

        @Test
        @DisplayName("when teacher_id1 then should return 2 lessons")
        void testGetAllByTeacherId1ReturnTwoLessons() {
            List<Lesson> lessonsForTeacher = dao.findAllByTeacherId(TEACHER_ID1);
            assertThat(lessonsForTeacher).hasSize(2);
            assertThat(lessonsForTeacher).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID3);
        }

        @Test
        @DisplayName("when teacher_id2 then should return 1 lesson")
        void testGetAllByTeacherId2ReturnOneLessons() {
            List<Lesson> lessonsForTeacher = dao.findAllByTeacherId(TEACHER_ID2);
            assertThat(lessonsForTeacher).hasSize(1);
            assertThat(lessonsForTeacher).extracting(Lesson::getId)
                .containsOnly(LESSON_ID2);
        }

        @Test
        @DisplayName("when teacher_id3 then should return empty List")
        void testGetAllByTeacherId3ReturnEmptyList() {
            assertThat(dao.findAllByTeacherId(TEACHER_ID3)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAllForRoom' method")
    class GetAllForRoomTest {

        @Test
        @DisplayName("when room_id1 then should return 2 lessons")
        void testRoomId1_ReturnThreeLessons() {
            List<Lesson> lessonsForRoom = dao.findAllByRoomId(ROOM_ID1);
            assertThat(lessonsForRoom.size()).isEqualTo(2);
            assertThat(lessonsForRoom).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2);
        }

        @Test
        @DisplayName("when room_id2 then should return 1 lesson")
        void testRoomId1_ReturnEmptyListLessons() {
            List<Lesson> lessonsForRoom = dao.findAllByRoomId(ROOM_ID2);
            assertThat(lessonsForRoom.size()).isEqualTo(1);
            assertThat(lessonsForRoom).extracting(Lesson::getId)
                .containsOnly(LESSON_ID3);
        }
    }

//    @Nested
//    @DisplayName("test 'getAllForStudent' method")
//    class GetAllForStudentTest {
//
//        @Test
//        @DisplayName("when student_id1 then should return 1 lesson_id1")
//        void testStudentId1_ReturnOneLessons() {
//            List<Lesson> lessonsForStudent = dao.getAllForStudent(STUDENT_ID1);
//            assertThat(lessonsForStudent.size()).isEqualTo(1);
//            assertThat(lessonsForStudent).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1);
//        }
//
//        @Test
//        @DisplayName("when student_id = 5 then should return empty List")
//        void testStudentId5_ReturnEmptyListLessons() {
//            assertThat(dao.getAllForStudent(5)).isEmpty();
//        }
//    }

//    @Nested
//    @DisplayName("test 'getAllWithFilter' method")
//    class GetAllWithFilterTest {
//
//        @Test
//        @DisplayName("when filter only for faculty should return list lessons" +
//            " size 3")
//        void testFilterOnlyFacultyId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(FACULTY_ID1);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(3);
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID2, LESSON_ID3);
//        }
//
//        @Test
//        @DisplayName("when filter only for faculty_Id2 should return empty list")
//        void testFilterOnlyFacultyId2() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(FACULTY_ID2);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).isEmpty();
//        }
//
//        @Test
//        @DisplayName("when filter facultyId and departmentId then filtering " +
//            "should be only for department and with department_id1 should " +
//            "return list size 3")
//        void testFilterFacultyAndDepartmentId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(FACULTY_ID2);
//            filter.setDepartmentId(DEPARTMENT_ID1);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(3);
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID2, LESSON_ID3);
//        }
//
//        @Test
//        @DisplayName("when filter facultyId, departmentId and teacherId then " +
//            "filtering should be only for teacher and with teacher_id1 should" +
//            " return list size 2")
//        void testFilterFacultyDepartmentAndTeacherId1() {
//            LessonFilter filter = new LessonFilter();
//            filter.setFacultyId(FACULTY_ID2);
//            filter.setDepartmentId(DEPARTMENT_ID2);
//            filter.setTeacherId(TEACHER_ID1);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(2);
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID3)
//                .doesNotContain(LESSON_ID2);
//        }
//
//        @Test
//        @DisplayName("when filter dateFrom and dateTo then filtering should " +
//            "return lessons between this dates")
//        void testFilterDateFromAndDateTo() {
//            LessonFilter filter = new LessonFilter();
//            filter.setDateFrom(LocalDateTime.of(2021, 6, 12, 8, 0));
//            filter.setDateTo(LocalDateTime.of(2021, 6, 14, 20, 0));
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(1);
//
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID3)
//                .doesNotContain(LESSON_ID1, LESSON_ID2);
//        }
//
//        @Test
//        @DisplayName("when filter course_Id1 then should return 2 lessons")
//        void whenFilterCourseId1_Return2Lessons() {
//            LessonFilter filter = new LessonFilter();
//            filter.setCourseId(COURSE_ID1);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(2);
//
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID3)
//                .doesNotContain(LESSON_ID2);
//        }
//
//        @Test
//        @DisplayName("when filter room_Id1 then should return 2 lessons")
//        void whenFilterRoomId1_Return2Lessons() {
//            LessonFilter filter = new LessonFilter();
//            filter.setRoomId(ROOM_ID1);
//
//            List<Lesson> lessons = dao.findAllWithFilter(filter);
//
//            assertThat(lessons).hasSize(2);
//
//            assertThat(lessons).extracting(Lesson::getId)
//                .containsOnly(LESSON_ID1, LESSON_ID2)
//                .doesNotContain(LESSON_ID3);
//        }
//    }

    @Nested
    @DisplayName("test 'getAllForStudentForTimePeriod' method")
    class GetAllForStudentForTimePeriodTest {
        @Test
        @DisplayName("when student_id1 and time between 2021-05-09 and 2021-05-11 " +
            "then should return lesson_id1")
        void whenStudentId1AndTimeBetween20210509And20210511_ReturnLessonId1() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 5, 9, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 5, 11, 0, 0);

            List<Lesson> lessons = dao.findAllForStudentForTimePeriod(STUDENT_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(1);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1)
                .doesNotContain(LESSON_ID2, LESSON_ID3);
        }

        @Test
        @DisplayName("when student_id1 and time between 2021-10-01 and 2021-10-30 " +
            "then should return empty list lessons")
        void whenStudentId1AndTimeBetween20211001And20211030_ReturnEmptyList() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 10, 1, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 10, 30, 0, 0);

            List<Lesson> lessons = dao.findAllForStudentForTimePeriod(STUDENT_ID1,
                timeFrom, timeTo);

            assertThat(lessons).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAllForTeacherForTimePeriod' method")
    class GetAllForTeacherForTimePeriodTest {
        @Test
        @DisplayName("when teacher_id1 and time between 2021-05-09 and 2021-05-11 " +
            "then should return lesson_id1")
        void whenTeacherId1AndTimeBetween20210509And20210511_ReturnLessonId1() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 5, 9, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 5, 11, 0, 0);

            List<Lesson> lessons = dao.findAllForTeacherForTimePeriod(TEACHER_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(1);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1)
                .doesNotContain(LESSON_ID2, LESSON_ID3);
        }

        @Test
        @DisplayName("when teacher_id1 and time between 2021-01-01 and 2021-12-30 " +
            "then should return 2 lessons")
        void whenTeacherId1AndTimeBetween20210101And20211230_Return2Lessons() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 1, 1, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 12, 30, 0, 0);

            List<Lesson> lessons = dao.findAllForTeacherForTimePeriod(TEACHER_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(2);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID3)
                .doesNotContain(LESSON_ID2);
        }
    }

    @Nested
    @DisplayName("test 'getAllForRoomForTimePeriod' method")
    class GetAllForRoomForTimePeriodTest {
        @Test
        @DisplayName("when room_id1 and time between 2021-05-09 and 2021-05-11 " +
            "then should return lesson_id1")
        void whenRoomId1AndTimeBetween20210509And20210511_ReturnLessonId1() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 5, 9, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 5, 11, 0, 0);

            List<Lesson> lessons = dao.getAllForRoomForTimePeriod(ROOM_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(1);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1)
                .doesNotContain(LESSON_ID2, LESSON_ID3);
        }

        @Test
        @DisplayName("when room_id1 and time between 2021-01-01 and 2021-12-30 " +
            "then should return 2 lessons")
        void whenRoomId1AndTimeBetween20210101And20211230_Return2Lessons() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 1, 1, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 12, 30, 0, 0);

            List<Lesson> lessons = dao.getAllForRoomForTimePeriod(ROOM_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(2);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2)
                .doesNotContain(LESSON_ID3);
        }
    }
}