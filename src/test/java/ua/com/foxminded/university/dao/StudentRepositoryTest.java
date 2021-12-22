package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;
import ua.com.foxminded.university.springconfig.BaseRepositoryIT;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.domain.entity.Assertions.assertThat;

@Sql("/sql/hibernate/student-test-data.sql")
class StudentRepositoryTest extends BaseRepositoryIT {

    @Autowired
    private StudentRepository repo;

    @Nested
    @DisplayName("test 'findAllByGroup' method")
    class FindAllByGroupTest {

        @Test
        @DisplayName("when group_id1 then should return List with size = 2")
        void testGetStudentsByGroup() {
            Group group = new Group();
            group.setId(GROUP_ID1);

            List<Student> studentsByGroup = repo.findAllByGroup(group);

            assertThat(studentsByGroup).hasSize(2);
            assertThat(studentsByGroup).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT);
        }
    }

    @Nested
    @DisplayName("test 'findAllByFaculty' method")
    class FindAllByFacultyTest {

        @Test
        @DisplayName("when facultyId1 then should return 2 expected students")
        void testGetByFacultyId1_ReturnTwoStudents() {
            Faculty faculty = new Faculty();
            faculty.setId(FACULTY_ID1);

            List<Student> studentsByFaculty = repo.findAllByFaculty(faculty);

            assertThat(studentsByFaculty).hasSize(2);
            assertThat(studentsByFaculty).extracting(Student::getFirstName)
                .containsOnly(NAME_FIRST_STUDENT, NAME_SECOND_STUDENT);
        }

        @Test
        @DisplayName("when facultyId=8 then should return empty list")
        void testGetByFacultyId4_ReturnEmptyList() {
            Faculty faculty = new Faculty();
            faculty.setId(8);

            List<Student> actualStudents = repo.findAllByFaculty(faculty);

            assertThat(actualStudents).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'findAllBusyStudents' method")
    class FindAllBusyStudentsTest {
        @Test
        @DisplayName("when time from 2021-06-12 to 2021-06-13 then should return " +
            "one student with student_id3")
        void from20210612to20210613_returnStudentId3() {
            LocalDateTime from = LocalDateTime.of(2021, 6 ,12, 0, 0);
            LocalDateTime to = LocalDateTime.of(2021, 6 ,13, 0, 0);

            List<Student> students = repo.findAllBusyStudents(from, to);

            assertThat(students).hasSize(1);
            assertThat(students.get(0))
                .hasId(STUDENT_ID3)
                .hasFirstName(NAME_THIRD_STUDENT)
                .hasLastName(LAST_NAME_THIRD_STUDENT);
        }
    }
}