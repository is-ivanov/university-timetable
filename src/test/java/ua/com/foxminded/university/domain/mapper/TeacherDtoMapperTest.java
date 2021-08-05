package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TeacherDtoMapperTest {

    public static final String FIRST_NAME = "First name";
    public static final String PATRONYMIC = "Patronymic";
    public static final String LAST_NAME = "LastName";
    public static final String FULL_NAME = "LastName, F.P.";
    public static final int ID1 = 1;
    public static final int ID2 = 2;
    public static final String DEPARTMENT_NAME = "department name";
    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

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

            TeacherDto teacherDto = TeacherDtoMapper.INSTANCE
                .teacherToTeacherDto(teacher);

            assertThat(teacherDto.getFirstName(), is(equalTo(FIRST_NAME)));
            assertThat(teacherDto.getPatronymic(), is(equalTo(PATRONYMIC)));
            assertThat(teacherDto.getLastName(), is(equalTo(LAST_NAME)));
            assertThat(teacherDto.getId(), is(equalTo(ID1)));
            assertThat(teacherDto.getDepartmentName(),
                is(equalTo(DEPARTMENT_NAME)));
            assertThat(teacherDto.getStatus(), is(equalTo(ACTIVE)));
            assertThat(teacherDto.getFullName(), is(equalTo(FULL_NAME)));
        }

        @Test
        @DisplayName("if teacher teacher.isActive=false without departments " +
            "then should return teacherDto inactive with null department name ")
        void testInactiveTeacherWithoutDepartment() {
            Teacher teacher = new Teacher();
            teacher.setActive(false);

            TeacherDto teacherDto = TeacherDtoMapper.INSTANCE
                .teacherToTeacherDto(teacher);

            assertThat(teacherDto.getStatus(), is(equalTo(INACTIVE)));
            assertThat(teacherDto.getDepartmentName(), nullValue());
        }
    }

}