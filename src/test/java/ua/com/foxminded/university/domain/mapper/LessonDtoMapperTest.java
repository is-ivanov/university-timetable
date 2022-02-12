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
import ua.com.foxminded.university.ui.restcontroller.link.StudentDtoAssembler;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

@SpringJUnitConfig(TestMapperConfig.class)
class LessonDtoMapperTest {

    @Autowired
    private LessonDtoMapper mapper;

    @Autowired
    private StudentDtoAssembler studentAssembler;

    @Nested
    @DisplayName("When we convert lesson to lessonDto")
    class LessonToLessonDtoTest {

        @Test
        @DisplayName("if lesson with full properties then should return " +
            "lessonDto with expected properties")
        void testExpectedLessonDto() {
            Lesson lesson = createTestLesson();

            LessonDto lessonDto = mapper.toDto(lesson);

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

            Lesson lesson = mapper.toEntity(lessonDto);

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

            List<LessonDto> lessonDtos = mapper.toDtos(lessons);

            assertThat(lessonDtos).hasSize(2);
            assertThat(lessonDtos).extracting(LessonDto::getId)
                .containsOnly(LESSON_ID1, LESSON_ID2);
        }
    }

}