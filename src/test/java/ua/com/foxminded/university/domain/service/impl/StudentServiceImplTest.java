package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.interfaces.StudentRepository;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    public static final String MESSAGE_STUDENT_NOT_FOUND = "Student id(78) not found";

    @Mock
    private StudentRepository studentRepositoryMock;

    @Mock
    private StudentDtoMapper mapperMock;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    @DisplayName("test 'add' when call add method then should call Repository once")
    void testAdd_CallDaoOnce() {
        Student student = new Student();
        Group group = new Group();
        group.setName(anyString());
        student.setGroup(group);
        studentService.add(student);
        verify(studentRepositoryMock).save(student);
    }

    @Test
    @DisplayName("test 'getAll' when Repository return List students then method " +
        "should return this List")
    void testGetAll_ReturnListStudents() {
        List<Student> testStudents = createTestStudents();
        List<StudentDto> testStudentDtos = createTestStudentDtos(GROUP_ID1);

        when(studentRepositoryMock.findAll()).thenReturn(testStudents);
        when(mapperMock.toStudentDtos(testStudents)).thenReturn(testStudentDtos);

        assertThat(studentService.getAll()).isEqualTo(testStudentDtos);
    }

    @Test
    @DisplayName("test 'update' when call update method then should call " +
        "studentDao once")
    void testUpdate_CallDaoOnce() {
        Student student = new Student();
        studentService.update(student);
        verify(studentRepositoryMock).save(student);
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Student then method " +
            "should return this Student")
        void testReturnExpectedStudent() {
            Student testStudent = createTestStudent();
            StudentDto testStudentDto = createTestStudentDto();

            when(studentRepositoryMock.findById(ID1)).thenReturn(Optional.of(testStudent));
            when(mapperMock.toStudentDto(testStudent)).thenReturn(testStudentDto);

            assertThat(studentService.getById(ID1)).isEqualTo(testStudentDto);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should " +
            "return empty Student")
        void testReturnEmptyStudent() {
            Optional<Student> optional = Optional.empty();
            when(studentRepositoryMock.findById(STUDENT_ID2)).thenReturn(optional);
            EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                () -> studentService.getById(STUDENT_ID2));
            assertThat(e.getMessage()).isEqualTo(MESSAGE_STUDENT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("test 'deactivateStudent' method")
    class DeactivateStudentTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            verify(studentRepositoryMock).save(student);
        }

        @Test
        @DisplayName("should update student with active = false")
        void testSetStudentActiveFalse() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentRepositoryMock).save(captor.capture());
            assertFalse(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'activateStudent' method")
    class ActivateStudentTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            verify(studentRepositoryMock).save(student);
        }

        @Test
        @DisplayName("should update student with active = true")
        void testSetStudentActiveTrue() {
            studentService.activateStudent(new Student(), new Group());
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentRepositoryMock).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("test 'transferStudentToGroup' method")
    class TransferStudentToGroupTest {

        @Test
        @DisplayName("should call studentDao.update once")
        void testCallStudentDaoOnce() {
            Student student = new Student();
            studentService.transferStudentToGroup(student, new Group());
            verify(studentRepositoryMock).save(student);
        }

        @Test
        @DisplayName("should update student with group from parameter")
        void testSetStudentGroupEqualsExpectedGroup() {
            Group expectedGroup = new Group();
            expectedGroup.setId(1);
            expectedGroup.setName("Test group name");
            Student student = new Student();
            studentService.transferStudentToGroup(student, expectedGroup);
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentRepositoryMock).save(captor.capture());
            assertEquals(expectedGroup, captor.getValue().getGroup());
        }
    }

    @Nested
    @DisplayName("test getStudentsByGroup")
    class TestGetStudentsByGroup {

        @Test
        @DisplayName("When dao return list students then should return this list")
        void group_WhenDaoReturnListStudentsThenShouldReturnThisList() {
            List<Student> students = createTestStudents();
            List<StudentDto> studentDtos = createTestStudentDtos(GROUP_ID1);

            Group group = new Group();
            group.setId(ID1);

            when(studentRepositoryMock.findAllByGroup(group)).thenReturn(students);
            when(mapperMock.toStudentDtos(students)).thenReturn(studentDtos);

            List<StudentDto> actualStudents = studentService.getStudentsByGroup(group);
            assertThat(actualStudents).isEqualTo(studentDtos);
        }

        @Test
        @DisplayName("When dao return list students then should return this list")
        void groupId_WhenDaoReturnListStudentsThenShouldReturnThisList() {
            List<Student> students = createTestStudents();
            List<StudentDto> studentDtos = createTestStudentDtos(GROUP_ID1);

            Group group = new Group();
            group.setId(ID1);

            when(studentRepositoryMock.findAllByGroup(group)).thenReturn(students);
            when(mapperMock.toStudentDtos(students)).thenReturn(studentDtos);

            List<StudentDto> actualStudents = studentService.getStudentsByGroup(ID1);

            assertThat(actualStudents).isEqualTo(studentDtos);
        }
    }

    @Nested
    @DisplayName("test getStudentsByFaculty")
    class TestGetStudentsByFaculty {

        @Test
        @DisplayName("When dao return list students then should return this list")
        void whenDaoReturnListStudentsThenShouldReturnThisList() {
            List<Student> students = createTestStudents();
            List<StudentDto> studentDtos = createTestStudentDtos(GROUP_ID1);

            when(studentRepositoryMock.findAllByFaculty(new Faculty(ID1, null)))
                .thenReturn(students);
            when(mapperMock.toStudentDtos(students)).thenReturn(studentDtos);

            List<StudentDto> actualStudents = studentService.getStudentsByFaculty(ID1);

            assertThat(actualStudents).isEqualTo(studentDtos);
        }
    }
}