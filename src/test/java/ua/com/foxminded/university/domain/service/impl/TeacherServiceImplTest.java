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
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    @Mock
    private TeacherRepository teacherRepoMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    @DisplayName("test 'create' when call add method then should call Repository once")
    void testCreate_CallDaoOnce() {
        Teacher teacher = new Teacher();
        Department existingDepartment = createTestDepartment(FACULTY_ID1);
        teacher.setDepartment(existingDepartment);

        when(departmentServiceMock.findById(DEPARTMENT_ID1)).thenReturn(existingDepartment);

        teacherService.create(teacher);
        verify(teacherRepoMock).save(teacher);
    }

    @Nested
    @DisplayName("test 'findById' method")
    class FindByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Teacher then method " +
            "should return this TeacherDto")
        void testReturnExpectedTeacher() {
            Teacher teacher = createTestTeacher();

            when(teacherRepoMock.findById(ID1)).thenReturn(Optional.of(teacher));

            assertThat(teacherService.findById(ID1)).isEqualTo(teacher);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should throw " +
            "new MyEntityNotFoundException")
        void testReturnEmptyTeacher() {
            when(teacherRepoMock.findById(ID1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> teacherService.findById(ID1))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessageContaining("Teacher with id(1) not found");
        }
    }

    @Test
    @DisplayName("test 'findAll' when Repository return List teachers then method " +
        "should return this List")
    void testFindAll_ReturnListTeachers() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

        when(teacherRepoMock.findAll()).thenReturn(teachers);

        assertThat(teacherService.findAll()).isEqualTo(teachers);
    }

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
            assertThat(captor.getValue().isActive()).isFalse();
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
            assertThat(captor.getValue().isActive()).isTrue();
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
            assertThat(captor.getValue().getDepartment()).isEqualTo(expectedDepartment);
        }
    }

    @Test
    @DisplayName("Test 'getAllByDepartment' when Repository return List teachers " +
        "then method should return this list")
    void testGetAllByDepartment() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

        when(teacherRepoMock.findAllByDepartmentId(DEPARTMENT_ID1)).thenReturn(teachers);

        assertThat(teacherService.getAllByDepartment(DEPARTMENT_ID1)).isEqualTo(teachers);
    }

    @Test
    @DisplayName("Test 'getAllByFaculty' when Repository return List teachers then " +
        "method should return this list")
    void testGetAllByFaculty() {
        List<Teacher> teachers = createTestTeachers(FACULTY_ID1);

        when(teacherRepoMock.findByDepartment_Faculty_IdIs(FACULTY_ID1)).thenReturn(teachers);

        assertThat(teacherService.getAllByFaculty(FACULTY_ID1)).isEqualTo(teachers);
    }

}