package ua.com.foxminded.university.domain.mapper;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@AllArgsConstructor
@ExtendWith(SpringExtension.class)
class TeacherDtoMapperTest {

    public static final String FIRST_NAME = "First name";
    public static final String PATRONYMIC = "Patronymic";
    public static final String LAST_NAME = "LastName";
    public static final String FULL_NAME = "LastName, F.P.";
    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String DEPARTMENT_NAME = "department name";

    private final TeacherDtoMapper mapper;

    @Nested
    @DisplayName("When we convert teacher into teacherDto")
    class teacherToTeacherDtoTest {

        @Test
        @DisplayName("if teacher.isActive=true with full properties then " +
            "should return teacherDto with expected properties")
        void testActiveTeacher() {
            Teacher teacher = new Teacher();
            teacher.setId(ID1);
            teacher.setFirstName(FIRST_NAME);
            teacher.setPatronymic(PATRONYMIC);
            teacher.setLastName(LAST_NAME);
            teacher.setActive(true);
            Department department = new Department();
            department.setId(ID2);
            department.setName(DEPARTMENT_NAME);
            teacher.setDepartment(department);

            TeacherDto teacherDto = mapper.teacherToTeacherDto(teacher);

            assertThat(teacherDto.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(teacherDto.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(teacherDto.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(teacherDto.getId(), is(equalTo(ID1)));
            assertThat(teacherDto.getDepartmentName(),
                is(equalTo(DEPARTMENT_NAME)));
            assertThat(teacherDto.isActive(), is(true));
            assertThat(teacherDto.getFullName(), is(equalTo(FULL_NAME)));
        }

        @Test
        @DisplayName("if teacher teacher.isActive=false without departments " +
            "then should return teacherDto with null department name ")
        void testInactiveTeacherWithoutDepartment() {
            Teacher teacher = new Teacher();
            teacher.setActive(false);

            TeacherDto teacherDto = mapper.teacherToTeacherDto(teacher);

            assertThat(teacherDto.isActive(), is(false));
            assertThat(teacherDto.getDepartmentName(), nullValue());
        }
    }

    @Nested
    @DisplayName("When we convert list teachers into list teacherDto")
    class teachersToTeacherDtosTest {

        @Test
        @DisplayName("should return expected list teacherDtos")
        void testListTeachers() {
            Teacher teacher1 = new Teacher();
            teacher1.setId(ID1);
            teacher1.setFirstName(FIRST_NAME);
            teacher1.setPatronymic(PATRONYMIC);
            teacher1.setLastName(LAST_NAME);
            teacher1.setActive(true);
            Department department = new Department();
            department.setId(ID2);
            department.setName(DEPARTMENT_NAME);
            teacher1.setDepartment(department);
            Teacher teacher2 = new Teacher();
            teacher2.setId(ID2);
            teacher2.setActive(false);
            List<Teacher> teachers = new ArrayList<>();
            teachers.add(teacher1);
            teachers.add(teacher2);

            List<TeacherDto> teacherDtos = mapper.teachersToTeacherDtos(teachers);
            assertThat(teacherDtos.get(0).getId(), is(equalTo(ID1)));
            assertThat(teacherDtos.get(0).isActive(), is(true));
            assertThat(teacherDtos.get(1).isActive(), is(false));
            assertThat(teacherDtos, hasSize(2));

        }
    }
}