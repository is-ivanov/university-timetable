package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.TeacherRepository;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    @Mock
    private TeacherRepository teacherRepoMock;

    @Mock
    private TeacherDtoMapper mapperMock;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    @DisplayName("test 'save' when call add method then should call Repository once")
    void testSave_CallDaoOnce() {
        Teacher teacher = new Teacher();
        Department department = new Department();
        department.setName(anyString());
        teacher.setDepartment(department);
        teacherService.save(teacher);
        verify(teacherRepoMock).save(teacher);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Teacher then method " +
            "should return this TeacherDto")
        void testReturnExpectedTeacher() {
            Teacher teacher = createTestTeacher();
            TeacherDto teacherDto = createTestTeacherDto();

            when(teacherRepoMock.findById(ID1)).thenReturn(Optional.of(teacher));
            when(mapperMock.toTeacherDto(teacher)).thenReturn(teacherDto);

            assertThat(teacherService.getById(ID1)).isEqualTo(teacherDto);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw " +
            "new EntityNotFoundException")
        void testReturnEmptyTeacher() {
            when(teacherRepoMock.findById(ID1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> teacherService.getById(ID1))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Teacher id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'getAll' when Repository return List teachers then method " +
        "should return this List")
    void testGetAll_ReturnListTeachers() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);
        List<TeacherDto> teacherDtos = createTestTeacherDtos(FACULTY_ID1);

        when(teacherRepoMock.findAll()).thenReturn(teachers);
        when(mapperMock.toTeacherDtos(teachers)).thenReturn(teacherDtos);

        assertThat(teacherService.getAll()).isEqualTo(teacherDtos);
    }

//    @Test
//    @DisplayName("test 'update' when call update method then should call " +
//        "teacherDao once")
//    void testUpdate_CallDaoOnce() {
//        Teacher teacher = new Teacher();
//        teacherService.update(teacher);
//        verify(teacherRepositoryMock).save(teacher);
//    }

    @Nested
    @DisplayName("test 'deactivateTeacher' method")
    class DeactivateStudentTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            verify(teacherRepoMock).save(teacher);
        }

        @Test
        @DisplayName("should update teacher with active = false")
        void testSetTeacherActiveFalse() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepoMock).save(captor.capture());
            assertFalse(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'activateTeacher' method")
    class ActivateTeacherTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            verify(teacherRepoMock).save(teacher);
        }

        @Test
        @DisplayName("should update teacher with active = true")
        void testSetTeacherActiveTrue() {
            teacherService.activateTeacher(new Teacher());
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepoMock).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'transferTeacherToDepartment' method")
    class TransferTeacherToDepartmentTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.transferTeacherToDepartment(teacher,
                new Department());
            verify(teacherRepoMock).save(teacher);
        }

        @Test
        @DisplayName("should update Teacher with Departments from parameter")
        void testSetTeacherDepartmentEqualsExpectedDepartment() {
            Department expectedDepartment = new Department();
            expectedDepartment.setId(ID1);
            expectedDepartment.setName("Test department name");
            Teacher teacher = new Teacher();
            teacherService.transferTeacherToDepartment(teacher, expectedDepartment);
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherRepoMock).save(captor.capture());
            assertEquals(expectedDepartment, captor.getValue().getDepartment());
        }
    }

    @Test
    @DisplayName("Test 'getAllByDepartment' when Repository return List teachers " +
        "then method should return this list")
    void testGetAllByDepartment() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);
        List<TeacherDto> teacherDtos = createTestTeacherDtos(FACULTY_ID1);

        when(teacherRepoMock.findAllByDepartmentId(DEPARTMENT_ID1)).thenReturn(teachers);
        when(mapperMock.toTeacherDtos(teachers)).thenReturn(teacherDtos);

        assertThat(teacherService.getAllByDepartment(DEPARTMENT_ID1)).isEqualTo(teacherDtos);
    }

    @Test
    @DisplayName("Test 'getAllByFaculty' when Repository return List teachers then " +
        "method should return this list")
    void testGetAllByFaculty() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);
        List<TeacherDto> teacherDtos = createTestTeacherDtos(FACULTY_ID1);

        when(teacherRepoMock.findAllByFaculty(FACULTY_ID1)).thenReturn(teachers);
        when(mapperMock.toTeacherDtos(teachers)).thenReturn(teacherDtos);

        assertThat(teacherService.getAllByFaculty(FACULTY_ID1)).isEqualTo(teacherDtos);
    }

}