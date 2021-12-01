package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Lesson;
import ua.com.foxminded.university.domain.entity.Student;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ua.com.foxminded.university.TestObjects.*;

class StudentDtoMapperTest {

    private static final String FIRST_NAME = "First name";
    private static final String PATRONYMIC = "Patronymic";
    private static final String LAST_NAME = "LastName";
    private static final String FULL_NAME = "LastName, F.P.";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final String GROUP_NAME = "Group name";

    private StudentDtoMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new StudentDtoMapperImpl();
    }

    @Test
    void testConvertListStudentsToStudentDtos() {
        Student student = new Student();
        student.setId(ID1);
        student.setFirstName(FIRST_NAME);
        student.setPatronymic(PATRONYMIC);
        student.setLastName(LAST_NAME);
        student.setActive(true);
        Group group = new Group();
        group.setId(ID2);
        group.setName(GROUP_NAME);
        student.setGroup(group);

        List<Student> students = Collections.singletonList(student);
        List<StudentDto> studentDtos = mapper.studentsToStudentDtos(students);

        StudentDto studentDto = studentDtos.get(0);

        assertThat(studentDtos, hasSize(1));
        assertThat(studentDto.getId(), is(equalTo(ID1)));
        assertThat(studentDto.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(studentDto.getPatronymic(), is(equalTo(PATRONYMIC)));
        assertThat(studentDto.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(studentDto.isActive(), is(true));
        assertThat(studentDto.getFullName(), is(equalTo(FULL_NAME)));
        assertThat(studentDto.getGroupId(), is(equalTo(ID2)));
        assertThat(studentDto.getGroupName(), is(equalTo(GROUP_NAME)));
    }

    @Test
    void testConvertListStudentDtosToStudents() {
        StudentDto studentDto = StudentDto.builder()
            .id(ID1)
            .firstName(FIRST_NAME)
            .patronymic(PATRONYMIC)
            .lastName(LAST_NAME)
            .active(true)
            .fullName(FULL_NAME)
            .groupId(ID2)
            .groupName(GROUP_NAME)
            .build();

        List<StudentDto> studentDtos = Collections.singletonList(studentDto);
        List<Student> students = mapper.studentDtosToStudents(studentDtos);

        Student student = students.get(0);
        assertThat(students, hasSize(1));
        assertThat(student.getId(), is(equalTo(ID1)));
        assertThat(student.getFirstName(), is(equalTo(FIRST_NAME)));
        assertThat(student.getPatronymic(), is(equalTo(PATRONYMIC)));
        assertThat(student.getLastName(), is(equalTo(LAST_NAME)));
        assertThat(student.isActive(), is(equalTo(true)));
        assertThat(student.getFullName(), is(equalTo(FULL_NAME)));
        assertThat(student.getGroup().getId(), is(equalTo(ID2)));
        assertThat(student.getGroup().getName(), is(equalTo(GROUP_NAME)));
    }

    @Nested
    @DisplayName("When we convert student to studentDto")
    class StudentToStudentDtoTest {

        @Test
        @DisplayName("if student with full properties then should return " +
            "studentDto with expected properties")
        void testActiveStudent() {
            Group group = new Group();
            group.setId(ID2);
            group.setName(GROUP_NAME);
            Student student = Student.builder()
                .id(STUDENT_ID1)
                .firstName(FIRST_NAME)
                .patronymic(PATRONYMIC)
                .lastName(LAST_NAME)
                .active(true)
                .group(group)
                .build();

            Lesson lesson1 = createTestLesson(LESSON_ID1);
            Lesson lesson2 = createTestLesson(LESSON_ID2);
            Set<Lesson> lessons = new HashSet<>(Arrays.asList(lesson1, lesson2));
            student.setLessons(lessons);

            StudentDto studentDto = mapper.studentToStudentDto(student);

            assertThat(studentDto.getId(), is(equalTo(STUDENT_ID1)));
            assertThat(studentDto.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(studentDto.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(studentDto.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(studentDto.isActive(), is(true));
            assertThat(studentDto.getFullName(), is(equalTo(FULL_NAME)));
            assertThat(studentDto.getGroupId(), is(equalTo(ID2)));
            assertThat(studentDto.getGroupName(), is(equalTo(GROUP_NAME)));
        }

        @Test
        @DisplayName("if student.isActive=false without group then should " +
            "return studentDto with empty groups fields ")
        void testInactiveStudentWithoutGroup() {
            Student student = new Student();
            student.setActive(false);

            StudentDto studentDto = mapper.studentToStudentDto(student);

            assertThat(studentDto.isActive(), is(false));
            assertThat(studentDto.getGroupId(), is(equalTo(0)));
            assertThat(studentDto.getGroupName(), nullValue());
        }
    }

    @Nested
    @DisplayName("When we convert studentDto to student")
    class StudentDtoToStudentTest {

        @Test
        @DisplayName("if studentDto with full properties then should return " +
            "student with expected properties")
        void testActiveStudentDto() {
            StudentDto studentDto = StudentDto.builder()
                .id(ID1)
                .firstName(FIRST_NAME)
                .patronymic(PATRONYMIC)
                .lastName(LAST_NAME)
                .active(true)
                .fullName(FULL_NAME)
                .groupId(ID2)
                .groupName(GROUP_NAME)
                .build();

            Student student = mapper.studentDtoToStudent(studentDto);

            assertThat(student.getId(), is(equalTo(ID1)));
            assertThat(student.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(student.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(student.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(student.isActive(), is(equalTo(true)));
            assertThat(student.getFullName(), is(equalTo(FULL_NAME)));
            assertThat(student.getGroup().getId(), is(equalTo(ID2)));
            assertThat(student.getGroup().getName(), is(equalTo(GROUP_NAME)));
        }
    }
}