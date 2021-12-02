package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.springconfig.TestMapperConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static ua.com.foxminded.university.TestObjects.*;

@SpringJUnitConfig(TestMapperConfig.class)
class LessonDtoMapperTest {

    private static final String COURSE_NAME = "Test course name";
    private static final String FIRST_NAME_TEACHER = "FirstNameTeacher";
    private static final String PATRONYMIC_TEACHER = "PatronymicTeacher";
    private static final String LAST_NAME_TEACHER = "LastNameTeacher";
    private static final String FULL_NAME_TEACHER = "LastNameTeacher, F.P.";
    private static final String GROUP_NAME = "Test group name";
    private static final String FIRST_NAME_STUDENT = "FirstNameStudent";
    private static final String PATRONYMIC_STUDENT = "PatronymicStudent";
    private static final String LAST_NAME_STUDENT = "LastNameStudent";
    private static final String BUILDING_NAME = "BuildingName";
    private static final String NUMBER_ROOM = "546";
    private static final String BUILDING_AND_NUMBER_ROOM = "BuildingName - 546";
    private static final int DURATION_LESSON_IN_MINUTES = 90;
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final LocalDateTime TIME_START = LocalDateTime.of(2021, 8, 10, 10, 0);
    private static final LocalDateTime TIME_END = TIME_START.plusMinutes(DURATION_LESSON_IN_MINUTES);

    @Autowired
    private LessonDtoMapper mapper;

    @Nested
    @DisplayName("When we convert lesson to lessonDto")
    class LessonToLessonDtoTest {

        @Test
        @DisplayName("if lesson with full properties then should return " +
            "lessonDto with expected properties")
        void testExpectedLessonDto() {
            Lesson lesson = createTestLesson();

            LessonDto lessonDto = mapper.toLessonDto(lesson);

            assertThat(lessonDto.getId()).isEqualTo(lesson.getId());
            assertThat(lessonDto.getCourseId()).isEqualTo(lesson.getCourse().getId());
            assertThat(lessonDto.getCourseName()).isEqualTo(lesson.getCourse().getName());
            assertThat(lessonDto.getTeacherId()).isEqualTo(lesson.getTeacher().getId());
            assertThat(lessonDto.getTeacherFullName()).
                isEqualTo(lesson.getTeacher().getFullName());
            assertThat(lessonDto.getRoomId()).isEqualTo(lesson.getRoom().getId());
            assertThat(lessonDto.getBuildingAndRoom()).isEqualTo(lesson.getRoom().getBuildingAndRoom());
            assertThat(lessonDto.getTimeStart()).isEqualTo(lesson.getTimeStart());
            assertThat(lessonDto.getTimeEnd()).isEqualTo(lesson.getTimeEnd());

            Set<StudentDto> students = lessonDto.getStudents();
            assertThat(students).hasSize(2);
            assertThat(students).extracting(StudentDto::getId)
                .containsOnly(STUDENT_ID1, STUDENT_ID2);
        }
    }

    @Nested
    @DisplayName("When we convert lessonDto to lesson")
    class LessonDtoToLessonTest {

        @Test
        @DisplayName("if lessonDto with full properties then should return " +
            "lesson with expected properties")
        void testExpectedLesson() {

            LessonDto lessonDto = createTestLessonDto(LESSON_ID1);

            Lesson lesson = mapper.toLesson(lessonDto);

            assertThat(lesson.getId()).isEqualTo(lessonDto.getId());
            assertThat(lesson.getCourse().getId()).isEqualTo(lessonDto.getCourseId());
            assertThat(lesson.getCourse().getName()).isEqualTo(lessonDto.getCourseName());
            assertThat(lesson.getTeacher().getId()).isEqualTo(lessonDto.getTeacherId());
            assertThat(lesson.getRoom().getId()).isEqualTo(lessonDto.getRoomId());
            assertThat(lesson.getTimeStart()).isEqualTo(lessonDto.getTimeStart());
            assertThat(lesson.getTimeEnd()).isEqualTo(lessonDto.getTimeEnd());

            Set<Student> students = lesson.getStudents();
            assertThat(students).hasSize(2);
            assertThat(students).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT);
        }
    }

    @Nested
    @DisplayName("test 'toLessonDtos' method")
    class ToLessonDtosTest {
        @Test
        @DisplayName("if list lesson has size 2 then should return list with size 2")
        void ifListLessonHasSize2_ReturnListWithSize2() {
            List<Lesson> lessons = createTestLessons();

            List<LessonDto> lessonDtos = mapper.toLessonDtos(lessons);

            assertThat(lessonDtos).hasSize(2);
            assertThat(lessonDtos).extracting(LessonDto::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2);
        }
    }

}