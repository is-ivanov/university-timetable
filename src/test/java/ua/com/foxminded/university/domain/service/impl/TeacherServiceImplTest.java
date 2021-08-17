package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.TeacherDao;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapperImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

    @Mock
    private TeacherDao teacherDaoMock;

    @Mock
    private TeacherDtoMapper teacherDtoMapperMock;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Teacher teacher = new Teacher();
        Department department = new Department();
        department.setName(anyString());
        teacher.setDepartment(department);
        teacherService.add(teacher);
        verify(teacherDaoMock).add(teacher);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Teacher then method " +
            "should return this Teacher")
        void testReturnExpectedTeacher() {
            Teacher expectedTeacher = new Teacher();
            expectedTeacher.setId(ID1);
            expectedTeacher.setFirstName(FIRST_NAME);
            expectedTeacher.setLastName(LAST_NAME);
            expectedTeacher.setActive(true);
            expectedTeacher.setDepartment(new Department());
            when(teacherDaoMock.getById(ID1)).thenReturn(Optional.of(expectedTeacher));
            assertEquals(expectedTeacher, teacherService.getById(ID1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Teacher")
        void testReturnEmptyTeacher() {
            Optional<Teacher> optional = Optional.empty();
            when(teacherDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Teacher(), teacherService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List teachers then method " +
        "should return this List")
    void testGetAll_ReturnListTeachers() {
        Faculty faculty = new Faculty();
        faculty.setId(ID1);
        Department department = new Department();
        department.setId(ID1);
        department.setFaculty(faculty);
        Teacher teacher1 = new Teacher();
        teacher1.setId(ID1);
        teacher1.setActive(true);
        teacher1.setFirstName(FIRST_NAME);
        teacher1.setDepartment(department);
        List<Teacher> expectedTeachers = new ArrayList<>();
        expectedTeachers.add(teacher1);
        Teacher teacher2 = new Teacher();
        teacher2.setId(ID2);
        teacher2.setActive(false);
        teacher2.setDepartment(department);
        expectedTeachers.add(teacher2);

        when(teacherDaoMock.getAll()).thenReturn(expectedTeachers);
        assertEquals(expectedTeachers, teacherService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "teacherDao once")
    void testUpdate_CallDaoOnce() {
        Teacher teacher = new Teacher();
        teacherService.update(teacher);
        verify(teacherDaoMock).update(teacher);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "teacherDao once")
    void testDelete_CallDaoOnce() {
        Teacher teacher = new Teacher();
        teacherService.delete(teacher);
        verify(teacherDaoMock).delete(teacher);
    }

    @Nested
    @DisplayName("test 'deactivateTeacher' method")
    class deactivateStudentTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            verify(teacherDaoMock).update(teacher);
        }

        @Test
        @DisplayName("should update teacher with active = false")
        void testSetTeacherActiveFalse() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherDaoMock).update(captor.capture());
            assertFalse(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'activateTeacher' method")
    class activateTeacherTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.deactivateTeacher(teacher);
            verify(teacherDaoMock).update(teacher);
        }

        @Test
        @DisplayName("should update teacher with active = true")
        void testSetTeacherActiveTrue() {
            teacherService.activateTeacher(new Teacher());
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherDaoMock).update(captor.capture());
            assertTrue(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'transferTeacherToDepartment' method")
    class transferTeacherToDepartmentTest {

        @Test
        @DisplayName("should call teacherDao.update once")
        void testCallTeacherDaoOnce() {
            Teacher teacher = new Teacher();
            teacherService.transferTeacherToDepartment(teacher,
                new Department());
            verify(teacherDaoMock).update(teacher);
        }

        @Test
        @DisplayName("should update Teacher with Departments from parameter")
        void testSetTeacherDepartmentEqualsExpectedDepartment(){
            Department expectedDepartment = new Department();
            expectedDepartment.setId(ID1);
            expectedDepartment.setName("Test department name");
            Teacher teacher = new Teacher();
            teacherService.transferTeacherToDepartment(teacher, expectedDepartment);
            ArgumentCaptor<Teacher> captor =
                ArgumentCaptor.forClass(Teacher.class);
            verify(teacherDaoMock).update(captor.capture());
            assertEquals(expectedDepartment, captor.getValue().getDepartment());
        }
    }

    @Test
    @DisplayName("Test 'getAllByDepartment' when Dao return List teachers " +
        "then method should return this list")
    void testGetAllByDepartment() {
        Faculty faculty = new Faculty();
        faculty.setId(ID1);
        Department department = new Department();
        department.setId(ID1);
        department.setFaculty(faculty);
        Teacher teacher1 = new Teacher();
        teacher1.setId(ID1);
        teacher1.setFirstName(FIRST_NAME);
        teacher1.setDepartment(department);
        List<Teacher> expectedTeachers = new ArrayList<>();
        expectedTeachers.add(teacher1);

        when(teacherDaoMock.getAllByDepartment(ID1)).thenReturn(expectedTeachers);
        assertEquals(expectedTeachers, teacherService.getAllByDepartment(ID1));
    }

    @Test
    @DisplayName("Test 'getAllByFaculty' when Dao return List teachers then " +
        "method should return this list")
    void testGetAllByFaculty() {
        Teacher teacher = new Teacher();
        teacher.setId(ID1);
        teacher.setFirstName(FIRST_NAME);
        List<Teacher> expectedTeachers = Arrays.asList(teacher);

        when(teacherDaoMock.getAllByFaculty(ID1)).thenReturn(expectedTeachers);
        assertEquals(expectedTeachers, teacherService.getAllByFaculty(ID1));
    }

    @Test
    @DisplayName("Test convertListTeachersToDtos when mapper return List " +
        "teacherDto then method should return this List")
    void testConvertListTeachersToDtos() {
        TeacherDto teacherDto1 = new TeacherDto();
        teacherDto1.setId(ID1);
        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(ID2);
        List<TeacherDto> dtos = Arrays.asList(teacherDto1, teacherDto2);

        when(teacherDtoMapperMock.teachersToTeacherDtos(any())).thenReturn(dtos);
        assertEquals(dtos, teacherService.convertListTeachersToDtos(any()));
    }
}