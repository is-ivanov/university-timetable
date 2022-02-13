package ua.com.foxminded.university.domain.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.dao.StudentRepository;
import ua.com.foxminded.university.domain.dto.StudentDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.domain.mapper.StudentDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.exception.MyEntityNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    public static final String MESSAGE_STUDENT_NOT_FOUND = "Student with id(79) not found";

    @Mock
    private StudentRepository studentRepoMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private StudentDtoMapper mapperMock;

    @Mock
    private Validator validatorMock;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Nested
    @DisplayName("test 'create' method")
    class CreateTest {

        @Test
        @DisplayName("when validator return empty set without violations then method " +
            "should call Repository once")
        void testSaveWithoutViolations_CallDaoOnce() {
            Student student = Student.builder()
                .firstName(NAME_FIRST_STUDENT)
                .lastName(LAST_NAME_FIRST_STUDENT)
                .active(true)
                .build();
            Group groupInStudent = Group.builder()
                .id(GROUP_ID1)
                .build();
            student.setGroup(groupInStudent);

            Group groupFromDb = createTestGroup();
            Set<ConstraintViolation<Group>> emptyViolations = new HashSet<>();

            when(groupServiceMock.findById(GROUP_ID1)).thenReturn(groupFromDb);
            when(validatorMock.validate(groupFromDb)).thenReturn(emptyViolations);

            studentService.create(student);

            verify(studentRepoMock).save(student);
        }
    }

    @Test
    @DisplayName("test 'findAll' when Repository return List students then method " +
        "should return this List")
    void testFindAll_ReturnListStudents() {
        List<Student> testStudents = createTestStudents();

        when(studentRepoMock.findAll()).thenReturn(testStudents);

        assertThat(studentService.findAll()).isEqualTo(testStudents);
    }

    @Nested
    @DisplayName("test 'findById' method")
    class FindByIdTest {

        @Test
        @DisplayName("when Repository return Optional with Student then method " +
            "should return this Student")
        void testReturnExpectedStudent() {
            Student testStudent = createTestStudent();

            when(studentRepoMock.findById(ID1)).thenReturn(Optional.of(testStudent));

            assertThat(studentService.findById(ID1)).isEqualTo(testStudent);
        }

        @Test
        @DisplayName("when Repository return empty Optional then method should " +
            "return empty Student")
        void testReturnEmptyStudent() {
            Optional<Student> optional = Optional.empty();

            when(studentRepoMock.findById(STUDENT_ID2)).thenReturn(optional);

            assertThatThrownBy(() -> studentService.findById(STUDENT_ID2))
                .isInstanceOf(MyEntityNotFoundException.class)
                .hasMessage(MESSAGE_STUDENT_NOT_FOUND);
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
            verify(studentRepoMock).save(student);
        }

        @Test
        @DisplayName("should update student with active = false")
        void testSetStudentActiveFalse() {
            Student student = new Student();
            studentService.deactivateStudent(student);
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentRepoMock).save(captor.capture());
            assertThat(captor.getValue().isActive()).isFalse();
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
            verify(studentRepoMock).save(student);
        }

        @Test
        @DisplayName("should update student with active = true")
        void testSetStudentActiveTrue() {
            studentService.activateStudent(new Student(), new Group());
            ArgumentCaptor<Student> captor =
                ArgumentCaptor.forClass(Student.class);
            verify(studentRepoMock).save(captor.capture());
            assertThat(captor.getValue().isActive()).isTrue();
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
            verify(studentRepoMock).save(student);
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
            verify(studentRepoMock).save(captor.capture());
            assertThat(captor.getValue().getGroup()).isEqualTo(expectedGroup);
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

            when(studentRepoMock.findAllByGroup(group)).thenReturn(students);
            when(mapperMock.toDtos(students)).thenReturn(studentDtos);

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

            when(studentRepoMock.findAllByGroup(group)).thenReturn(students);
            when(mapperMock.toDtos(students)).thenReturn(studentDtos);

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

            when(studentRepoMock.findAllByFaculty(new Faculty(ID1, null)))
                .thenReturn(students);
            when(mapperMock.toDtos(students)).thenReturn(studentDtos);

            List<StudentDto> actualStudents = studentService.getStudentsByFaculty(ID1);

            assertThat(actualStudents).isEqualTo(studentDtos);
        }
    }
}