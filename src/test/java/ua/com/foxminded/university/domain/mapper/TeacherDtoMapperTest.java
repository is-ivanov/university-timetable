package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TeacherDtoMapperTest {

    private static final String FIRST_NAME = "First name";
    private static final String PATRONYMIC = "Patronymic";
    private static final String LAST_NAME = "LastName";
    private static final String FULL_NAME = "LastName, F.P.";
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final String DEPARTMENT_NAME = "department name";

    private TeacherDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TeacherDtoMapperImpl();
    }

    @Nested
    @DisplayName("When we convert teacher to teacherDto")
    class TeacherToTeacherDtoTest {

        @Test
        @DisplayName("if teacher.isActive=true with full properties then " +
            "should return teacherDto with expected properties")
        void testActiveTeacher() {
            Department department = new Department();
            department.setId(ID2);
            department.setName(DEPARTMENT_NAME);
            Teacher teacher = Teacher.builder()
                .id(ID1)
                .firstName(FIRST_NAME)
                .patronymic(PATRONYMIC)
                .lastName(LAST_NAME)
                .active(true)
                .department(department)
                .build();

            TeacherDto teacherDto = mapper.toDto(teacher);

            assertThat(teacherDto.getId(), is(equalTo(ID1)));
            assertThat(teacherDto.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(teacherDto.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(teacherDto.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(teacherDto.isActive(), is(true));
            assertThat(teacherDto.getFullName(), is(equalTo(FULL_NAME)));
            assertThat(teacherDto.getDepartmentId(), is(equalTo(ID2)));
            assertThat(teacherDto.getDepartmentName(),
                is(equalTo(DEPARTMENT_NAME)));
        }

        @Test
        @DisplayName("if teacher teacher.isActive=false without departments " +
            "then should return teacherDto with null department name ")
        void testInactiveTeacherWithoutDepartment() {
            Teacher teacher = new Teacher();
            teacher.setActive(false);

            TeacherDto teacherDto = mapper.toDto(teacher);

            assertThat(teacherDto.isActive(), is(false));
            assertThat(teacherDto.getDepartmentName(), nullValue());
        }
    }

    @Nested
    @DisplayName("When we convert list teachers into list teacherDto")
    class TeachersToTeacherDtosTest {

        @Test
        @DisplayName("should return expected list teacherDtos")
        void testListTeachers() {
            Department department = new Department();
            department.setId(ID2);
            department.setName(DEPARTMENT_NAME);
            Teacher teacher1 = Teacher.builder()
                .id(ID1)
                .firstName(FIRST_NAME)
                .patronymic(PATRONYMIC)
                .lastName(LAST_NAME)
                .active(true)
                .department(department)
                .build();


            Teacher teacher2 = Teacher.builder()
                .id(ID2)
                .active(false)
                .build();
            List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

            List<TeacherDto> teacherDtos = mapper.toDtos(teachers);
            assertThat(teacherDtos.get(0).getId(), is(equalTo(ID1)));
            assertThat(teacherDtos.get(0).isActive(), is(true));
            assertThat(teacherDtos.get(1).isActive(), is(false));
            assertThat(teacherDtos, hasSize(2));

        }
    }

    @Nested
    @DisplayName("When we convert teacherDto to teacher")
    class TeacherDtoToTeacherTest {

        @Test
        @DisplayName("if teacherDto with full properties then should return " +
            "teacher with expected properties")
        void testActiveTeacherDtoToTeacher() {
            TeacherDto teacherDto = TeacherDto.builder()
                .id(ID1)
                .firstName(FIRST_NAME)
                .patronymic(PATRONYMIC)
                .lastName(LAST_NAME)
                .active(true)
                .fullName(FULL_NAME)
                .departmentId(ID2)
                .departmentName(DEPARTMENT_NAME)
                .build();

            Teacher teacher = mapper.toEntity(teacherDto);

            assertThat(teacher.getId(), is(equalTo(ID1)));
            assertThat(teacher.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(teacher.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(teacher.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(teacher.isActive(), is(true));
            assertThat(teacher.getFullName(), is(equalTo(FULL_NAME)));
            assertThat(teacher.getDepartment().getId(), is(equalTo(ID2)));
            assertThat(teacher.getDepartment().getName(), is(equalTo(DEPARTMENT_NAME)));
        }
    }
}