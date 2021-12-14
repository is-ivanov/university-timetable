package ua.com.foxminded.university.domain.filter;

import static org.junit.jupiter.api.Assertions.*;

class LessonFilterTest {

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
}