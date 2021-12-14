package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/lesson-test-data.sql")
class LessonJpaRepositoryTest extends IntegrationTestBase {

    @Autowired
    private LessonRepository repo;

    @Nested
    @DisplayName("test 'deleteAllStudentsFromLesson' method")
    class DeleteAllStudentsFromLessonTest {

        @Test
        @DisplayName("after delete students from lesson_id2 Set students in the " +
            "lesson should be empty")
        void testDeleteAllStudentsFromLesson() {
            repo.deleteAllStudentsFromLesson(LESSON_ID2);

            Lesson lesson = repo.findById(LESSON_ID2).get();
            assertThat(lesson.getStudents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findAllByTeacherId' method")
    class FindAllByTeacherIdTest {

        @Test
        @DisplayName("when teacher_id1 then should return 2 lessons")
        void testGetAllByTeacherId1ReturnTwoLessons() {
            List<Lesson> lessonsForTeacher = repo.findAllByTeacherId(TEACHER_ID1);
            assertThat(lessonsForTeacher).hasSize(2);
            assertThat(lessonsForTeacher).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID3);
        }

        @Test
        @DisplayName("when teacher_id2 then should return 1 lesson")
        void testGetAllByTeacherId2ReturnOneLessons() {
            List<Lesson> lessonsForTeacher = repo.findAllByTeacherId(TEACHER_ID2);
            assertThat(lessonsForTeacher).hasSize(1);
            assertThat(lessonsForTeacher).extracting(Lesson::getId)
                .containsOnly(LESSON_ID2);
        }

        @Test
        @DisplayName("when teacher_id3 then should return empty List")
        void testGetAllByTeacherId3ReturnEmptyList() {
            assertThat(repo.findAllByTeacherId(TEACHER_ID3)).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findAllByRoomId' method")
    class FindAllByRoomIdTest {

        @Test
        @DisplayName("when room_id1 then should return 2 lessons")
        void testRoomId1_ReturnThreeLessons() {
            List<Lesson> lessonsForRoom = repo.findAllByRoomId(ROOM_ID1);
            assertThat(lessonsForRoom.size()).isEqualTo(2);
            assertThat(lessonsForRoom).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2);
        }

        @Test
        @DisplayName("when room_id2 then should return 1 lesson")
        void testRoomId1_ReturnEmptyListLessons() {
            List<Lesson> lessonsForRoom = repo.findAllByRoomId(ROOM_ID2);
            assertThat(lessonsForRoom.size()).isEqualTo(1);
            assertThat(lessonsForRoom).extracting(Lesson::getId)
                .containsOnly(LESSON_ID3);
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

            repo.deleteStudentFromLesson(LESSON_ID1, STUDENT_ID1);

            Lesson lessonAfter = repo.findById(LESSON_ID1).get();
            Set<Student> studentsAfter = lessonAfter.getStudents();

            assertThat(studentsAfter).hasSize(expectedNumberStudents);
            assertThat(studentsAfter).extracting(Student::getId)
                .doesNotContain(STUDENT_ID1);
        }
    }

    @Nested
    @DisplayName("test 'getAllForStudentForTimePeriod' method")
    class GetAllForStudentForTimePeriodTest {
        @Test
        @DisplayName("when student_id1 and time between 2021-05-09 and 2021-05-11 " +
            "then should return lesson_id1")
        void whenStudentId1AndTimeBetween20210509And20210511_ReturnLessonId1() {
            LocalDateTime timeFrom = LocalDateTime.of(2021, 5, 9, 0, 0);
            LocalDateTime timeTo = LocalDateTime.of(2021, 5, 11, 0, 0);

            List<Lesson> lessons = repo.findAllForStudentForTimePeriod(STUDENT_ID1,
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

            List<Lesson> lessons = repo.findAllForStudentForTimePeriod(STUDENT_ID1,
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

            List<Lesson> lessons = repo.findAllForTeacherForTimePeriod(TEACHER_ID1,
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

            List<Lesson> lessons = repo.findAllForTeacherForTimePeriod(TEACHER_ID1,
                timeFrom, timeTo);

            assertThat(lessons).hasSize(2);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID3)
                .doesNotContain(LESSON_ID2);
        }
    }

    @Nested
    @DisplayName("test 'findAllByRoomByTimePeriod' method")
    class FindAllByRoomByTimePeriodTest {
        @Test
        @DisplayName("when room_id1 and time between 2021-05-09 and 2021-05-11 " +
            "then should return lesson_id1")
        void whenRoomId1AndTimeBetween20210509And20210511_ReturnLessonId1() {
            LocalDateTime from = LocalDateTime.of(2021, 5, 9, 0, 0);
            LocalDateTime to = LocalDateTime.of(2021, 5, 11, 0, 0);

            List<Lesson> lessons = repo.findAllByRoomByTimePeriod(ROOM_ID1,
                from, to);

            assertThat(lessons).hasSize(1);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1)
                .doesNotContain(LESSON_ID2, LESSON_ID3);
        }

        @Test
        @DisplayName("when room_id1 and time between 2021-01-01 and 2021-12-30 " +
            "then should return 2 lessons")
        void whenRoomId1AndTimeBetween20210101And20211230_Return2Lessons() {
            LocalDateTime from = LocalDateTime.of(2021, 1, 1, 0, 0);
            LocalDateTime to = LocalDateTime.of(2021, 12, 30, 0, 0);

            List<Lesson> lessons = repo.findAllByRoomByTimePeriod(ROOM_ID1,
                from, to);

            assertThat(lessons).hasSize(2);
            assertThat(lessons).extracting(Lesson::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2)
                .doesNotContain(LESSON_ID3);
        }
    }
}