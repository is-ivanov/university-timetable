package ua.com.foxminded.university.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.domain.dto.DepartmentDto;
import ua.com.foxminded.university.domain.dto.FacultyDto;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.exception.GlobalExceptionHandler;
import ua.com.foxminded.university.ui.PageSequenceCreator;
import ua.com.foxminded.university.ui.util.Mappings;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    public static final String URI_FACULTIES_ID = "/faculties/{id}";
    public static final String URI_FACULTIES_ID_GROUPS = "/faculties/{id}/groups";
    public static final String URI_FACULTIES_ID_DEPARTMENTS = "/faculties/{id}/departments";
    public static final String URI_FACULTIES_ID_TEACHERS = "/faculties/{id}/teachers";
    public static final String URI_FACULTIES_ID_GROUPS_FREE = "/faculties/{id}/groups/free";
    public static final String FACULTY_NAME = "name";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";

    @Captor
    ArgumentCaptor<Faculty> facultyCaptor;

    private MockMvc mockMvc;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

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

            when(facultyServiceMock.getAllSortedPaginated(pageable))
                .thenReturn(pageFaculties);
            when(pageSequenceCreatorMock.createPageSequence(totalPages, currentPage + 1))
                .thenReturn(pages);

            mockMvc.perform(get(Mappings.FACULTIES))
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

            when(facultyServiceMock.getAllSortedPaginated(pageable))
                .thenReturn(pageFaculties);

            mockMvc.perform(get(Mappings.FACULTIES)
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

            when(facultyServiceMock.getAllSortedPaginated(pageable))
                .thenReturn(pageFaculties);

            mockMvc.perform(get(Mappings.FACULTIES)
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
    @DisplayName("test 'createFaculty' method")
    class CreateFacultyTest {

        @Test
        @DisplayName("when POST request with parameter name then should call " +
            "facultyService.add once")
        void postRequestWithParameterName() throws Exception {
            mockMvc.perform(post(Mappings.FACULTIES)
                    .param("name", NAME_FIRST_FACULTY))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            verify(facultyServiceMock, times(1)).save(facultyCaptor.capture());
            assertThat(facultyCaptor.getValue().getName(), is(equalTo(NAME_FIRST_FACULTY)));
        }

        @Test
        @DisplayName("when POST request with fail parameter (name with first " +
            "letter lower case) then should return error 400.BAD_REQUEST")
        void whenPostRequestWithFailParameter() throws Exception {
            mockMvc.perform(post(Mappings.FACULTIES)
                    .param("name", FAIL_NAME_FIRST_FACULTY))
                .andDo(print())
                .andExpectAll(
                    status().isBadRequest(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.violations[0].field", is("name")),
                    jsonPath("$.violations[0].message", is(MESSAGE_FIRST_CAPITAL_LETTER)));
        }
    }

    @Nested
    @DisplayName("test 'getFaculty' method")
    class GetFacultyTest {

        @Test
        @DisplayName("when GET request with @PathVariable 'id' then should return " +
            "JSON with expected faculty")
        void getRequestWithId() throws Exception {
            int facultyId = anyInt();
            Faculty testFaculty = createTestFaculty(facultyId);
            FacultyDto testFacultyDto = createTestFacultyDto(facultyId);

            when(facultyServiceMock.getById(facultyId)).thenReturn(testFacultyDto);

            mockMvc.perform(get(URI_FACULTIES_ID, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id", is(facultyId)),
                    jsonPath("$.name", is(NAME_FIRST_FACULTY))
                );
            verify(facultyServiceMock, times(1)).getById(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'updateFaculty' method")
    class UpdateFacultyTest {

        @Test
        @DisplayName("when PUT request with parameters 'id' and 'name' then should " +
            "call facultyService.update call")
        void putRequestWithIdAndName() throws Exception {
            int facultyId = anyInt();
            Faculty faculty = new Faculty(facultyId, NAME_FIRST_FACULTY);

            mockMvc.perform(put(URI_FACULTIES_ID, facultyId)
                    .param("name", NAME_FIRST_FACULTY))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

            verify(facultyServiceMock, times(1)).save(faculty);
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

            verify(facultyServiceMock, times(1)).delete(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'getGroupsByFaculty' method")
    class GetGroupsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id = 0 then should call " +
            "groupService.getAll once and return JSON with groups")
        void getRequestWithId0() throws Exception {
            int facultyId = 0;
            List<GroupDto> testGroups = createTestGroupDtos(facultyId);

            when(groupServiceMock.getAll()).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(GROUP_ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].facultyId", is(facultyId)),
                    jsonPath("$[0].facultyName", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(GROUP_ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].facultyId", is(facultyId)),
                    jsonPath("$[1].facultyName", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock, times(1)).getAll();
        }

        @Test
        @DisplayName("when GET request with parameter id != 0 then should call " +
            "groupService.getAllByFacultyId once and return JSON with groups")
        void getRequestWithId2() throws Exception {
            int facultyId = 3;
            List<GroupDto> testGroups = createTestGroupDtos(facultyId);
            when(groupServiceMock.getAllByFacultyId(facultyId)).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(GROUP_ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].facultyId", is(facultyId)),
                    jsonPath("$[0].facultyName", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(GROUP_ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].facultyId", is(facultyId)),
                    jsonPath("$[1].facultyName", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock, times(1)).getAllByFacultyId(facultyId);
        }
    }

    @Nested
    @DisplayName("test 'getFreeGroupsByFaculty' method")
    class GetFreeGroupsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameters then should call " +
            "groupService.getFreeGroupsByFacultyOnLessonTime once return JSON with groups")
        void getRequestWithParameters() throws Exception {
            int facultyId = 4;

            List<GroupDto> testGroups = createTestGroupDtos(facultyId);

            when(groupServiceMock.getFreeGroupsByFacultyOnLessonTime(facultyId,
                DATE_FROM, DATE_TO)).thenReturn(testGroups);
            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS_FREE, facultyId)
                    .param(TIME_START, TEXT_DATE_FROM)
                    .param(TIME_END, TEXT_DATE_TO))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(GROUP_ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].facultyId", is(facultyId)),
                    jsonPath("$[0].facultyName", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(GROUP_ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].facultyId", is(facultyId)),
                    jsonPath("$[1].facultyName", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock, times(1))
                .getFreeGroupsByFacultyOnLessonTime(facultyId, DATE_FROM, DATE_TO);
        }
    }

    @Nested
    @DisplayName("test 'getDepartmentsByFaculty' method")
    class GetDepartmentsByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id = 0 then should call " +
            "departmentService.getAll once and return JSON with departments")
        void getRequestWithParameterIdEquals0() throws Exception {
            int facultyId = 0;
            List<DepartmentDto> testDepartments = createTestDepartmentDtos();

            when(departmentServiceMock.getAll()).thenReturn(testDepartments);

            mockMvc.perform(get(URI_FACULTIES_ID_DEPARTMENTS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testDepartments.size())),
                    jsonPath("$[0].name", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[1].name", is(NAME_SECOND_DEPARTMENT))
                );

            verify(departmentServiceMock, times(0)).getAllByFaculty(facultyId);
        }

        @Test
        @DisplayName("when GET request with parameter id > 0 then should call " +
            "departmentService.getAllByFaculty once and return JSON with departments")
        void getRequestWithParameterIdEquals8() throws Exception {
            int facultyId = 8;
            List<DepartmentDto> testDepartments = createTestDepartmentDtos();

            when(departmentServiceMock.getAllByFaculty(facultyId)).thenReturn(testDepartments);

            mockMvc.perform(get(URI_FACULTIES_ID_DEPARTMENTS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testDepartments.size())),
                    jsonPath("$[0].name", is(NAME_FIRST_DEPARTMENT)),
                    jsonPath("$[1].name", is(NAME_SECOND_DEPARTMENT))
                );
            verify(departmentServiceMock, times(0)).getAll();
        }
    }

    @Nested
    @DisplayName("test 'getTeachersByFaculty' method")
    class GetTeachersByFacultyTest {

        @Test
        @DisplayName("when GET request with parameter id then should call " +
            "teacherDtoMapper and teacherService once")
        void getRequestWithParameterIdEquals4() throws Exception {
            int facultyId = 4;
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_FACULTIES_ID_TEACHERS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testTeacherDtos.size())),
                    jsonPath("$[0].firstName", is(NAME_FIRST_TEACHER)),
                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_TEACHER)),
                    jsonPath("$[0].patronymic", is(PATRONYMIC_FIRST_TEACHER)),
                    jsonPath("$[1].firstName", is(NAME_SECOND_TEACHER)),
                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_TEACHER)),
                    jsonPath("$[1].patronymic", is(PATRONYMIC_SECOND_TEACHER))
                );
        }
    }

}