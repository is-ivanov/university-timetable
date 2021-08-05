package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeacherDtoMapperTest {
    //TODO
    @Test
    public void test() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        teacher.setFirstName("First name");
        teacher.setPatronymic("Patronymic");
        teacher.setLastName("LastName");
        teacher.setActive(true);
        Department department = new Department();
        department.setId(2);
        department.setName("department name");
        teacher.setDepartment(department);
        TeacherDto teacherDto = TeacherDtoMapper.INSTANCE
            .teacherToTeacherDto(teacher);
        assertEquals("First name", teacherDto.getFirstName());
        assertEquals("Patronymic", teacherDto.getPatronymic());
        assertEquals("LastName", teacherDto.getLastName());
        assertEquals(1, teacherDto.getId());
        assertEquals("department name", teacherDto.getDepartmentName());
        assertEquals("", teacherDto.getStatus());
        assertEquals("", teacherDto.getFullName());
    }

}