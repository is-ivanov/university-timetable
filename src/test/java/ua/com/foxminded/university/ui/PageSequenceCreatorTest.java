package ua.com.foxminded.university.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class PageSequenceCreatorTest {

    private PageSequenceCreator creator;

    @BeforeEach
    void setUp() {
        creator = new PageSequenceCreator();
    }

    @Test
    @DisplayName("totalPages = 1 and currentPage = 1")
    void testTotalPages1AndCurrentPage1() {
        List<Integer> expectedList = Collections.singletonList(1);
        assertThat(creator.createPageSequence(1, 1),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 2 and currentPage = 1")
    void testTotalPages2AndCurrentPage1() {
        List<Integer> expectedList = Arrays.asList(1, 2);
        assertThat(creator.createPageSequence(2, 1),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 5 and currentPage = 3")
    void testTotalPages5AndCurrentPage3() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(creator.createPageSequence(5, 3),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 6 and currentPage = 1")
    void testTotalPages6AndCurrentPage1() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3, -1, 6);
        assertThat(creator.createPageSequence(6, 1),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 6 and currentPage = 3")
    void testTotalPages6AndCurrentPage3() {
        List<Integer> expectedList = Arrays.asList(1, 2, 3, 4, -1, 6);
        assertThat(creator.createPageSequence(6, 3),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 6 and currentPage = 6")
    void testTotalPages6AndCurrentPage6() {
        List<Integer> expectedList = Arrays.asList(1, -1, 4, 5, 6);
        assertThat(creator.createPageSequence(6, 6),
            is(equalTo(expectedList)));
    }

    @Test
    @DisplayName("totalPages = 7 and currentPage = 4")
    void testTotalPages7AndCurrentPage4() {
        List<Integer> expectedList = Arrays.asList(1, -1, 3, 4, 5, -1, 7);
        assertThat(creator.createPageSequence(7, 4),
            is(equalTo(expectedList)));
    }

}