package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.*;
import ua.com.foxminded.university.springconfig.TestRootConfig;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestRootConfig.class)
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

            Course course = new Course(ID2, COURSE_NAME);
            Teacher teacher = Teacher.builder()
                .id(ID2)
                .firstName(FIRST_NAME_TEACHER)
                .patronymic(PATRONYMIC_TEACHER)
                .lastName(LAST_NAME_TEACHER)
                .active(true)
                .department(new Department())
                .build();
            Room room = new Room(ID1, BUILDING_NAME, NUMBER_ROOM);
            Group group = new Group();
            group.setName(GROUP_NAME);
            Student student = Student.builder()
                .firstName(FIRST_NAME_STUDENT)
                .patronymic(PATRONYMIC_STUDENT)
                .lastName(LAST_NAME_STUDENT)
                .active(true)
                .group(group)
                .build();
            List<Student> students = Collections.singletonList(student);
            Lesson lesson = Lesson.builder()
                .id(ID1)
                .course(course)
                .teacher(teacher)
                .room(room)
                .timeStart(TIME_START)
                .timeEnd(TIME_END)
                .students(students)
                .build();

            LessonDto lessonDto = mapper.lessonToLessonDto(lesson);
            StudentDto studentDto = lessonDto.getStudents().get(0);
            assertThat(lessonDto.getId(), is(equalTo(ID1)));
            assertThat(lessonDto.getCourseId(), is(equalTo(ID2)));
            assertThat(lessonDto.getCourseName(), is(equalTo(COURSE_NAME)));
            assertThat(lessonDto.getTeacherId(), is(equalTo(ID2)));
            assertThat(lessonDto.getTeacherFullName(),
                is(equalTo(FULL_NAME_TEACHER)));
            assertThat(lessonDto.getRoomId(), is(equalTo(ID1)));
            assertThat(lessonDto.getBuildingAndRoom(),
                is(equalTo(BUILDING_AND_NUMBER_ROOM)));
            assertThat(lessonDto.getTimeStart(), is(equalTo(TIME_START)));
            assertThat(lessonDto.getTimeEnd(), is(equalTo(TIME_END)));
            assertThat(studentDto.getFirstName(), is(equalTo(FIRST_NAME_STUDENT)));
            assertThat(studentDto.getPatronymic(), is(equalTo(PATRONYMIC_STUDENT)));
            assertThat(studentDto.getLastName(), is(equalTo(LAST_NAME_STUDENT)));
            assertThat(studentDto.isActive(), is(true));
            assertThat(studentDto.getGroupName(), is(equalTo(GROUP_NAME)));
        }
    }

    @Nested
    @DisplayName("When we convert lessonDto to lesson")
    class LessonDtoToLessonTest {

        @Test
        @DisplayName("if lessonDto with full properties then should return " +
            "lesson with expected properties")
        void testExpectedLesson() {

            LessonDto lessonDto = LessonDto.builder()
                .id(ID1)
                .courseId(ID2)
                .courseName(COURSE_NAME)
                .teacherId(ID1)
                .roomId(ID2)
                .timeStart(TIME_START)
                .timeEnd(TIME_END)
                .build();

            Lesson lesson = mapper.lessonDtoToLesson(lessonDto);

            assertThat(lesson.getId(), is(equalTo(ID1)));
            assertThat(lesson.getCourse().getId(), is(equalTo(ID2)));
            assertThat(lesson.getCourse().getName(), is(equalTo(COURSE_NAME)));
            assertThat(lesson.getTeacher().getId(), is(equalTo(ID1)));
            assertThat(lesson.getRoom().getId(), is(equalTo(ID2)));
            assertThat(lesson.getTimeStart(), is(equalTo(TIME_START)));
            assertThat(lesson.getTimeEnd(), is(equalTo(TIME_END)));
        }
    }

}