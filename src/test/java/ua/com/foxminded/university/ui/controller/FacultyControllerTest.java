package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.PageSequenceCreator;
import ua.com.foxminded.university.ui.util.MappingConstants;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.createTestFaculties;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    public static final String URI_FACULTIES_ID = "/faculties/{id}";
    public static final String FACULTY_NAME = "name";

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private PageSequenceCreator pageSequenceCreatorMock;

    @InjectMocks
    private FacultyController facultyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(facultyController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Nested
    @DisplayName("test 'showFaculties' method")
    class ShowFacultiesTest {

        @Test
        @DisplayName("when GET request without parameters then should use " +
            "@PageableDefault values")
        void getRequestWithoutParameters() throws Exception {
            int totalPages = 1;
            int currentPage = 0;
            Pageable pageable = PageRequest.of(currentPage, 10,
                Sort.by(FACULTY_NAME));
            List<Faculty> expectedFaculties = createTestFaculties();
            Page<Faculty> pageFaculties = new PageImpl<>(expectedFaculties,
                pageable, totalPages);
            List<Integer> pages = Collections.singletonList(1);

            when(facultyServiceMock.findAll(pageable))
                .thenReturn(pageFaculties);
            when(pageSequenceCreatorMock.createPageSequence(totalPages, currentPage + 1))
                .thenReturn(pages);

            mockMvc.perform(get(MappingConstants.FACULTIES))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("faculty"),
                    model().attributeExists("faculties", "page", "uri",
                        "newFaculty", "pages"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("page", pageFaculties),
                    model().attribute("pages", pages)
                );
        }

        @Test
        @DisplayName("when GET request with parameter page = 3 then should use " +
            "this value and the rest of the parameters by default")
        void getRequestWithPage3() throws Exception {
            int currentPage = 3;
            int totalPages = 5;
            Pageable pageable = PageRequest.of(currentPage, 10,
                Sort.by(FACULTY_NAME));
            List<Faculty> expectedFaculties = createTestFaculties();
            Page<Faculty> pageFaculties = new PageImpl<>(expectedFaculties,
                pageable, totalPages);

            when(facultyServiceMock.findAll(pageable))
                .thenReturn(pageFaculties);

            mockMvc.perform(get(MappingConstants.FACULTIES)
                    .param("page", String.valueOf(currentPage)))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("faculty"),
                    model().attribute("faculties", expectedFaculties),
                    model().attribute("page", pageFaculties)
                );
        }

        @Test
        @DisplayName("when GET request with parameters page, size and sort then " +
            "should use this parameters")
        void getRequestWithPageSizeAndSort() throws Exception {
            int page = 2;
            int size = 10;
            String sort = "faculty_id";
            int totalPages = 15;

            Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.asc("faculty_id")));
            List<Faculty> faculties = createTestFaculties();
            Page<Faculty> pageFaculties = new PageImpl<>(faculties,
                pageable, totalPages);

            when(facultyServiceMock.findAll(pageable))
                .thenReturn(pageFaculties);

            mockMvc.perform(get(MappingConstants.FACULTIES)
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .param("sort", sort))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    view().name("faculty")
                );
        }
    }

    @Nested
    @DisplayName("test 'deleteFaculty' method")
    class DeleteFacultyTest {

        @Test
        @DisplayName("when DELETE request with @PathVariable 'id' then should " +
            "call facultyService.delete once and redirect")
        void deleteRequestWithId() throws Exception {
            int facultyId = anyInt();
            mockMvc.perform(delete(URI_FACULTIES_ID, facultyId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(facultyServiceMock).delete(facultyId);
        }
    }

}