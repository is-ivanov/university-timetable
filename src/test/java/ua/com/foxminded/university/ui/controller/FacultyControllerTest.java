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
import ua.com.foxminded.university.domain.dto.TeacherDto;
import ua.com.foxminded.university.domain.entity.Department;
import ua.com.foxminded.university.domain.entity.Faculty;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Teacher;
import ua.com.foxminded.university.domain.mapper.TeacherDtoMapper;
import ua.com.foxminded.university.domain.service.interfaces.DepartmentService;
import ua.com.foxminded.university.domain.service.interfaces.FacultyService;
import ua.com.foxminded.university.domain.service.interfaces.GroupService;
import ua.com.foxminded.university.domain.service.interfaces.TeacherService;
import ua.com.foxminded.university.ui.PageSequenceCreator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.ui.controller.FacultyController.URI_FACULTIES;

@ExtendWith(MockitoExtension.class)
class FacultyControllerTest {

    public static final String URI_FACULTIES_ID = "/faculties/{id}";
    public static final String URI_FACULTIES_ID_GROUPS = "/faculties/{id}/groups";
    public static final String URI_FACULTIES_ID_DEPARTMENTS = "/faculties/{id}/departments";
    public static final String URI_FACULTIES_ID_TEACHERS = "/faculties/{id}/teachers";
    public static final String URI_FACULTIES_ID_GROUPS_FREE = "/faculties/{id}/groups/free";
    public static final String FACULTY_NAME = "faculty_name";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";

    private MockMvc mockMvc;

    @Captor
    ArgumentCaptor<Faculty> facultyCaptor;

    @Mock
    private FacultyService facultyServiceMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private DepartmentService departmentServiceMock;

    @Mock
    private TeacherService teacherServiceMock;

    @Mock
    private TeacherDtoMapper teacherDtoMapperMock;

    @Mock
    private PageSequenceCreator pageSequenceCreatorMock;

    @InjectMocks
    private FacultyController facultyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(facultyController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
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

            mockMvc.perform(get(URI_FACULTIES))
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

            mockMvc.perform(get(URI_FACULTIES)
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

            mockMvc.perform(get(URI_FACULTIES)
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
            "facultyService.add once and redirect")
        void postRequestWithParameterName() throws Exception {
            mockMvc.perform(post(URI_FACULTIES)
                    .param("name", NAME_FIRST_FACULTY))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

            verify(facultyServiceMock, times(1)).add(facultyCaptor.capture());
            assertThat(facultyCaptor.getValue().getName(), is(equalTo(NAME_FIRST_FACULTY)));
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

            when(facultyServiceMock.getById(facultyId)).thenReturn(testFaculty);

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
    class UpdateFaculty {

        @Test
        @DisplayName("when PUT request with parameters 'id' and 'name' then should " +
            "call facultyService.update call and redirect")
        void putRequestWithIdAndName() throws Exception {
            int facultyId = anyInt();
            Faculty faculty = new Faculty(facultyId, NAME_FIRST_FACULTY);

            mockMvc.perform(put(URI_FACULTIES_ID, facultyId)
                    .param("name", NAME_FIRST_FACULTY))
                .andDo(print())
                .andExpectAll(
                    status().is3xxRedirection()
                );

            verify(facultyServiceMock, times(1)).update(faculty);
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
            List<Group> testGroups = createTestGroups(facultyId);

            when(groupServiceMock.getAll()).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].faculty.id", is(facultyId)),
                    jsonPath("$[0].faculty.name", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].faculty.id", is(facultyId)),
                    jsonPath("$[1].faculty.name", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock, times(1)).getAll();
        }

        @Test
        @DisplayName("when GET request with parameter id != 0 then should call " +
            "groupService.getAllByFacultyId once and return JSON with groups")
        void getRequestWithId2() throws Exception {
            int facultyId = 3;
            List<Group> testGroups = createTestGroups(facultyId);
            when(groupServiceMock.getAllByFacultyId(facultyId)).thenReturn(testGroups);

            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].faculty.id", is(facultyId)),
                    jsonPath("$[0].faculty.name", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].faculty.id", is(facultyId)),
                    jsonPath("$[1].faculty.name", is(NAME_FIRST_FACULTY))
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
            LocalDateTime startTime = LocalDateTime.of(2021, 5, 25, 10, 30);
            LocalDateTime endTime = LocalDateTime.of(2021, 5, 25, 11, 0);

            List<Group> testGroups = createTestGroups(facultyId);

            when(groupServiceMock.getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime))
                .thenReturn(testGroups);
            mockMvc.perform(get(URI_FACULTIES_ID_GROUPS_FREE, facultyId)
                    .param(TIME_START, "2021-05-25 10:30")
                    .param(TIME_END, "2021-05-25 11:00"))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testGroups.size())),
                    jsonPath("$[0].id", is(ID1)),
                    jsonPath("$[0].name", is(NAME_FIRST_GROUP)),
                    jsonPath("$[0].faculty.id", is(facultyId)),
                    jsonPath("$[0].faculty.name", is(NAME_FIRST_FACULTY)),
                    jsonPath("$[1].id", is(ID2)),
                    jsonPath("$[1].name", is(NAME_SECOND_GROUP)),
                    jsonPath("$[1].faculty.id", is(facultyId)),
                    jsonPath("$[1].faculty.name", is(NAME_FIRST_FACULTY))
                );
            verify(groupServiceMock, times(1))
                .getFreeGroupsByFacultyOnLessonTime(facultyId, startTime, endTime);
        }
    }

    @Nested
    @DisplayName("test 'getDepartmentsByFaculty' method")
    class GetDepartmentsByFaculty {

        @Test
        @DisplayName("when GET request with parameter id = 0 then should call " +
            "departmentService.getAll once and return JSON with departments")
        void getRequestWithParameterIdEquals0() throws Exception {
            int facultyId = 0;
            List<Department> testDepartments = createTestDepartments(facultyId);

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
            List<Department> testDepartments = createTestDepartments(facultyId);

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
    class GetTeachersByFaculty {

        @Test
        @DisplayName("when GET request with parameter id then should call " +
            "teacherDtoMapper and teacherService once")
        void getRequestWithParameterIdEquals4() throws Exception {
            int facultyId = 4;
            List<Teacher> testTeachers = createTestTeachers(facultyId);
            List<TeacherDto> testTeacherDtos = createTestTeacherDtos(facultyId);

            when(teacherServiceMock.getAllByFaculty(facultyId))
                .thenReturn(testTeachers);
            when(teacherDtoMapperMock.teachersToTeacherDtos(testTeachers))
                .thenReturn(testTeacherDtos);

            mockMvc.perform(get(URI_FACULTIES_ID_TEACHERS, facultyId))
                .andDo(print())
                .andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$", hasSize(testTeachers.size())),
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