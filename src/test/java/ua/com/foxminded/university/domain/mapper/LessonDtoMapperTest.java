package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.LessonDto;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Room;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class LessonDtoMapperTest {

    private static final String COURSE_NAME = "Test course name";
    private static final String FIRST_NAME_TEACHER = "FirstNameTeacher";
    private static final String PATRONYMIC_TEACHER = "PatronymicTeacher";
    private static final String LAST_NAME_TEACHER = "LastNameTeacher";
    private static final String FULL_NAME_TEACHER = "LastNameTeacher, F.P.";
    private static final String BUILDING_NAME = "BuildingName";
    private static final String NUMBER_ROOM = "546";
    private static final String BUILDING_AND_NUMBER_ROOM = "BuildingName - 546";
    private static final int DURATION_LESSON_IN_MINUTES = 90;

    private LessonDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LessonDtoMapperImpl();
    }

    @Nested
    @DisplayName("When we convert lesson to lessonDto")
    class lessonToLessonDtoTest {

        @Test
        @DisplayName("if lesson with full properties then should return " +
            "lessonDto with expected properties")
        void testExpectedLesson() {

            Course course = new Course();
            course.setName(COURSE_NAME);
            Teacher teacher = new Teacher();
            teacher.setFirstName(FIRST_NAME_TEACHER);
            teacher.setPatronymic(PATRONYMIC_TEACHER);
            teacher.setLastName(LAST_NAME_TEACHER);
            Room room = new Room();
            room.setBuilding(BUILDING_NAME);
            room.setNumber(NUMBER_ROOM);
            LocalDateTime timeStart = LocalDateTime.of(2021, 8, 10, 10, 0);
            LocalDateTime timeEnd =
                timeStart.plusMinutes(DURATION_LESSON_IN_MINUTES);
            Lesson lesson = Lesson.builder()
                .id(1)
                .course(course)
                .teacher(teacher)
                .room(room)
                .timeStart(timeStart)
                .timeEnd(timeEnd)
                .build();

            LessonDto lessonDto = mapper.lessonToLessonDto(lesson);

            assertThat(lessonDto.getId(), is(equalTo(1)));
            assertThat(lessonDto.getCourseName(), is(equalTo(COURSE_NAME)));
            assertThat(lessonDto.getTeacherFullName(),
                is(equalTo(FULL_NAME_TEACHER)));
            assertThat(lessonDto.getBuildingAndRoom(),
                is(equalTo(BUILDING_AND_NUMBER_ROOM)));
            assertThat(lessonDto.getTimeStart(), is(equalTo(timeStart)));
            assertThat(lessonDto.getTimeEnd(), is(equalTo(timeEnd)));
        }
    }

}