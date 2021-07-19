package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.StudentDao;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final int ID1 = 1;
    public static final int ID2 = 2;

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
        Group group = new Group();
        group.setName(anyString());
        student.setGroup(group);
        studentService.add(student);
        verify(studentDaoMock).add(student);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class getByIdTest {

        @Test
        @DisplayName("when Dao return Optional with Student then method " +
            "should return this Student")
        void testReturnExpectedStudent() {
            Student expectedStudent = new Student();
            expectedStudent.setId(ID1);
            expectedStudent.setFirstName(FIRST_NAME);
            expectedStudent.setLastName(LAST_NAME);
            expectedStudent.setActive(true);
            expectedStudent.setGroup(new Group());
            when(studentDaoMock.getById(ID1)).thenReturn(Optional.of(expectedStudent));
            assertEquals(expectedStudent, studentService.getById(ID1));
        }

        @Test
        @DisplayName("when Dao return empty Optional then method should " +
            "return empty Student")
        void testReturnEmptyStudent() {
            Optional<Student> optional = Optional.empty();
            when(studentDaoMock.getById(anyInt())).thenReturn(optional);
            assertEquals(new Student(), studentService.getById(anyInt()));
        }
    }

    @Test
    @DisplayName("test 'getAll' when Dao return List students then method " +
        "should return this List")
    void testGetAll_ReturnListStudents() {
        Faculty faculty = new Faculty();
        faculty.setId(ID1);
        Group group = new Group();
        group.setId(ID1);
        group.setFaculty(faculty);
        Student student1 = new Student();
        student1.setId(ID1);
        student1.setActive(true);
        student1.setFirstName(FIRST_NAME);
        student1.setGroup(group);
        List<Student> expectedStudents = new ArrayList<>();
        expectedStudents.add(student1);
        Student student2 = new Student();
        student2.setId(ID2);
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

    @Nested
    @DisplayName("test 'deactivateStudent' method")
    class deactivateStudentTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            verify(studentDaoMock).update(student);
        }

        @Test
        @DisplayName("should update student with active = false")
        void testSetStudentActiveFalse() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentDaoMock).update(captor.capture());
            assertFalse(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'activateStudent' method")
    class activateStudentTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            verify(studentDaoMock).update(student);
        }

        @Test
        @DisplayName("should update student with active = true")
        void testSetStudentActiveTrue() {
            studentService.activateStudent(new Student(), new Group());
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentDaoMock).update(captor.capture());
            assertTrue(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'transferStudentToGroup' method")
    class transferStudentToGroupTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.transferStudentToGroup(student, new Group());
                verify(studentDaoMock).update(student);
        }

        @Test
        @DisplayName("should update student with group from parameter")
        void testSetStudentGroupEqualsExpectedGroup(){
            Group expectedGroup = new Group();
            expectedGroup.setId(1);
            expectedGroup.setName("Test group name");
            Student student = new Student();
            studentService.transferStudentToGroup(student, expectedGroup);
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentDaoMock).update(captor.capture());
            assertEquals(expectedGroup, captor.getValue().getGroup());
        }
    }
}