package ua.com.foxminded.university.dao.jpaimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.dao.interfaces.CourseDao;
import ua.com.foxminded.university.domain.entity.Course;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ua.com.foxminded.university.TestObjects.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql("/sql/hibernate/course-test-data.sql")
class JpaCourseDaoImplTest extends IntegrationTestBase {

    private static final String TEST_COURSE_NAME = "New course";
    private static final String MESSAGE_DELETE_COURSE_NOT_FOUND =
        "Can't delete because course id(4) not found";

    @Autowired
    private CourseDao dao;

    @Nested
    @DisplayName("test 'add' method")
    class AddTest {

        @Test
        @DisplayName("add test course should CountRowsTable = 4")
        void testAddCourse() {
            Course course = new Course(TEST_COURSE_NAME);
            int expectedRowsInTable = dao.countAll() + 1;

            dao.add(course);

            int actualRowsInTable = dao.countAll();
            assertThat(actualRowsInTable).isEqualTo(expectedRowsInTable);
        }
    }

    @Nested
    @DisplayName("test 'getById' method")
    class GetByIdTest {

        @Test
        @DisplayName("with id = 1 should return course (1, English)")
        void testGetByIdCourse() {
            Optional<Course> courseOptional = dao.getById(ID1);
            Course course = courseOptional.get();
            assertThat(course.getId()).isEqualTo(ID1);
            assertThat(course.getName()).isEqualTo(NAME_FIRST_COURSE);
        }

        @Test
        @DisplayName("with id=4 should return empty Optional")
        void testGetByIdCourseException() {
            Optional<Course> courseOptional = dao.getById(4);
            assertThat(courseOptional).isEmpty();
        }
    }

    @Nested
    @DisplayName("test 'getAll' method")
    class GetAllTest {

        @Test
        @DisplayName("should return List with size = 3")
        void testGetAllCourses() {
            int expectedQuantityCourses = dao.countAll();
            List<Course> courses = dao.getAll();
            assertThat(courses).hasSize(expectedQuantityCourses);
            assertThat(courses).extracting(Course::getName)
                .contains(NAME_FIRST_COURSE, NAME_SECOND_COURSE, NAME_THIRD_COURSE);
        }
    }

    @Nested
    @DisplayName("test 'update' method")
    class UpdateTest {

        @Test
        @DisplayName("with course id=1 should write new name and getById(1) " +
            "return this name")
        void testUpdateExistingCourse_WriteNewName() {
            Course course = new Course(ID1, TEST_COURSE_NAME);
            dao.update(course);
            String actualName = dao.getById(ID1).get().getName();
            assertThat(actualName).isEqualTo(TEST_COURSE_NAME);
        }

        @Test
        @DisplayName("with course id=4 should write new course")
        void testUpdateNonExistingCourse_CreateNewCourse() {
            Course course = new Course(4, TEST_COURSE_NAME);
            dao.update(course);

            Course actualCourse = dao.getById(4).get();
            assertThat(dao.getAll()).hasSize(4);
            assertThat(actualCourse).isEqualTo(course);
        }
    }

    @Nested
    @DisplayName("test 'delete' method")
    class DeleteTest {

        @Nested
        @DisplayName("delete(course) method")
        class DeleteCourseTest {

            @Test
            @DisplayName("with course id=1 should delete one record and number " +
                "records table should equals 2")
            void testDeleteExistingCourse_ReduceNumberRowsInTable() {
                int expectedQuantityCourses = dao.countAll() - 1;
                Course course = dao.getById(ID1).get();
                dao.delete(course);
                int actualQuantityCourses = dao.countAll();
                assertThat(actualQuantityCourses).isEqualTo(expectedQuantityCourses);
            }
        }

        @Nested
        @DisplayName("delete(courseId) method")
        class DeleteCourseIdTest {

            @Test
            @DisplayName("with course id=1 should delete one record and number " +
                "records table should equals 2")
            void testDeleteExistingCourseId1_ReduceNumberRowsInTable() {
                int expectedQuantityCourses = dao.countAll() - 1;
                dao.delete(ID1);
                int actualQuantityCourses = dao.countAll();
                assertThat(actualQuantityCourses).isEqualTo(expectedQuantityCourses);
            }

            @Test
            @DisplayName("with course id=4 should write new log.warn and throw " +
                "new DAOException")
            void testDeleteNonExistingCourse_ExceptionAndWriteLogWarn() {
                assertThatThrownBy(() -> dao.delete(4))
                    .isInstanceOf(DaoException.class)
                    .hasMessageContaining(MESSAGE_DELETE_COURSE_NOT_FOUND);
            }
        }

    }

    @Nested
    @DisplayName("test 'countAll' method")
    class CountAllTest {

        @Test
        @DisplayName("should return number 3")
        void testReturnCountAllRecords() {
            assertThat(dao.countAll()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("test 'getAllSortedPaginated' method")
    class GetAllSortedPaginatedTest {

        final long totalCourses = 3L;

        @Test
        @DisplayName("when pageable size = 1 without sort then return page with " +
            "first record sorted by course name")
        void test_Size1WithoutSorting() {
            int pageNumber = 0;
            int pageSize = 1;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            Page<Course> page = dao.getAllSortedPaginated(pageable);

            assertThat(page.getSize()).isEqualTo(pageSize);
            assertThat(page.getTotalElements()).isEqualTo(totalCourses);
            assertThat(page.getNumber()).isEqualTo(pageNumber);

            Course course = page.getContent().get(0);
            assertThat(course.getId()).isEqualTo(ID3);
            assertThat(course.getName()).isEqualTo(NAME_THIRD_COURSE);
        }

        @Test
        @DisplayName("when pageable with sort by ID then return sorted by ID page")
        void test_Size2WithSortingById() {
            int pageNumber = 0;
            int pageSize = 2;
            Sort sort = Sort.by(Sort.Direction.ASC, "course_id");
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<Course> page = dao.getAllSortedPaginated(pageable);

            assertThat(page.getSize()).isEqualTo(pageSize);
            assertThat(page.getTotalElements()).isEqualTo(totalCourses);
            assertThat(page.getNumber()).isEqualTo(pageNumber);

            List<Course> courses = page.getContent();

            assertThat(courses).extracting(Course::getId)
                .containsExactly(ID1, ID2);
        }
    }
}