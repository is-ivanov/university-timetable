package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.exception.DAOException;
import ua.com.foxminded.university.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    private StudentServiceImpl studentService;

    @Mock
    private StudentDao studentDaoMock;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(studentDaoMock);
    }

    @Test
    @DisplayName("test 'add' when call add method then should call Dao once")
    void testAdd_CallDaoOnce() {
        Student student = new Student();
        studentService.add(student);
        verify(studentDaoMock).add(student);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Student then method " +
            "should return this Student")
        void testReturnExpectedStudent() throws Exception {
            Student expectedStudent = new Student();
            expectedStudent.setId(1);
            expectedStudent.setFirstName(FIRST_NAME);
            expectedStudent.setLastName(LAST_NAME);
            expectedStudent.setActive(true);
            expectedStudent.setGroup(new Group());
            when(studentDaoMock.getById(1)).thenReturn(Optional.of(expectedStudent));
            assertEquals(expectedStudent, studentDaoMock.getById(1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Student")
        void testReturnEmptyStudent() throws Exception {
            Optional<Student> optional = Optional.empty();
            when(studentDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Student(), studentService.getById(anyInt()));
        }

        @Test
        @DisplayName("when Dao throw DAOException then method should throw " +
            "ServiceException")
        void testThrowException() throws Exception {
            when(studentDaoMock.getById(anyInt())).thenThrow(DAOException.class);
            assertThrows(ServiceException.class,
                () -> studentService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List students then method " +
        "should return this List")
    void testGetAll_ReturnListStudents() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        Group group = new Group();
        group.setId(1);
        group.setFaculty(faculty);
        Student student1 = new Student();
        student1.setId(1);
        student1.setActive(true);
        student1.setFirstName(FIRST_NAME);
        student1.setGroup(group);
        List<Student> expectedStudents = new ArrayList<>();
        expectedStudents.add(student1);
        Student student2 = new Student();
        student2.setId(2);
        student2.setActive(false);
        student2.setGroup(group);
        expectedStudents.add(student2);

        when(studentDaoMock.getAll()).thenReturn(expectedStudents);
        assertEquals(expectedStudents, studentService.getAll());
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "studentDao once")
    void testUpdate_CallDaoOnce() {
        Student student = new Student();
        studentService.update(student);
        verify(studentDaoMock).update(student);
    }

    @Test
    @DisplayName("test 'delete' when call delete method then should call " +
        "studentDao once")
    void testDelete_CallDaoOnce() {
        Student student = new Student();
        studentService.delete(student);
        verify(studentDaoMock).delete(student);
    }

    @Test
    void deactivateStudent() {
    }

    @Test
    void activateStudent() {
    }

    @Test
    void transferStudentToGroup() {
    }
}