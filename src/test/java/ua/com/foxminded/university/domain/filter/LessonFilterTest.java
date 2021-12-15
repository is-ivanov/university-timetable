package ua.com.foxminded.university.domain.filter;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.entity.QLesson;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

class LessonFilterTest {

    private LessonFilter filter;

    @BeforeEach
    void setUp() {
        filter = new LessonFilter();
    }

    @Nested
    @DisplayName("test 'getPredicate' method")
    class GetPredicateTest {

        @Test
        @DisplayName("when filter only for faculty should return predicate with " +
            "only faculty.id")
        void testFilterOnlyFacultyId1() {
            filter.setFacultyId(FACULTY_ID1);

            Predicate expectedPredicate =
                QLesson.lesson.teacher.department.faculty.id.eq(FACULTY_ID1);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }


        @Test
        @DisplayName("when filter facultyId and departmentId then should return " +
            "predicate with only department.id")
        void testFilterFacultyAndDepartmentId1() {
            filter.setFacultyId(FACULTY_ID2);
            filter.setDepartmentId(DEPARTMENT_ID1);

            Predicate expectedPredicate =
                QLesson.lesson.teacher.department.id.eq(DEPARTMENT_ID1);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }

        @Test
        @DisplayName("when filter facultyId, departmentId and teacherId then " +
            "should return predicate with only teacher.id")
        void testFilterFacultyDepartmentAndTeacherId1() {
            filter.setFacultyId(FACULTY_ID2);
            filter.setDepartmentId(DEPARTMENT_ID2);
            filter.setTeacherId(TEACHER_ID1);

            Predicate expectedPredicate =
                QLesson.lesson.teacher.id.eq(TEACHER_ID1);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }

        @Test
        @DisplayName("when filter dateFrom and dateTo then should return predicate " +
            "with timeStart.between this dates")
        void testFilterDateFromAndDateTo() {
            LocalDateTime from = LocalDateTime.of(2021, 6, 12, 8, 0);
            filter.setDateFrom(from);
            LocalDateTime to = LocalDateTime.of(2021, 6, 14, 20, 0);
            filter.setDateTo(to);

            Predicate expectedPredicate =
                QLesson.lesson.timeStart.between(from, to);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }

        @Test
        @DisplayName("when filter course_Id1 then should return predicate with " +
            "only course.id")
        void whenFilterCourseId1() {
            filter.setCourseId(COURSE_ID1);

            Predicate expectedPredicate =
                QLesson.lesson.course.id.eq(COURSE_ID1);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }

        @Test
        @DisplayName("when filter room_Id1 then should return predicate with " +
            "only room.id")
        void whenFilterRoomId1_Return2Lessons() {
            filter.setRoomId(ROOM_ID1);

            Predicate expectedPredicate =
                QLesson.lesson.room.id.eq(ROOM_ID1);

            Predicate actualPredicate = filter.getPredicate();

            assertThat(actualPredicate).isEqualTo(expectedPredicate);
        }
    }
}