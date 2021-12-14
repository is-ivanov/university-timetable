package ua.com.foxminded.university.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.springconfig.IntegrationTestBase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.NAME_FIRST_FACULTY;
import static ua.com.foxminded.university.TestObjects.NAME_SECOND_FACULTY;

@Sql("/sql/hibernate/faculty-test-data.sql")
class FacultyJpaRepositoryTest extends IntegrationTestBase {

    @Autowired
    private FacultyRepository repo;

    @Test
    @DisplayName("test 'findAllByOrderByNameAsc' method")
    void testShouldReturnFacultiesInOrder() {
        List<Faculty> sortedFaculties = repo.findAllByOrderByNameAsc();
        assertThat(sortedFaculties).extracting(Faculty::getName)
                .containsExactly(NAME_SECOND_FACULTY, NAME_FIRST_FACULTY);
    }
}