package ua.com.foxminded.university.ui.restcontroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.MediaType;
import ua.com.foxminded.university.domain.entity.Group;
import ua.com.foxminded.university.domain.entity.Student;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ua.com.foxminded.university.TestObjects.*;
import static ua.com.foxminded.university.TestObjects.NAME_FIRST_FACULTY;

class GroupRestControllerTest {
    @Captor
    ArgumentCaptor<Group> groupCaptor;


//    @Nested
//    @DisplayName("test 'createGroup' method")
//    class CreateGroupTest {
//
//        @Test
//        @DisplayName("when POST request with parameters 'name', 'active' and " +
//            "'faculty.id' then should call groupService.add once")
//        void postRequestWithParametersNameActiveFacultyId() throws Exception {
//            mockMvc.perform(post(URI_GROUPS)
//                    .param("name", NAME_FIRST_GROUP)
//                    .param("active", ON)
//                    .param("faculty.id", String.valueOf(ID1)))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(groupServiceMock, times(1)).create(groupCaptor.capture());
//            Group expectedGroup = groupCaptor.getValue();
//            assertThat(expectedGroup.getFaculty().getId(), is(equalTo(ID1)));
//            assertThat(expectedGroup.getName(), is(equalTo(NAME_FIRST_GROUP)));
//            assertThat(expectedGroup.isActive(), is(true));
//        }
//    }

//    @Nested
//    @DisplayName("test 'getGroup' method")
//    class GetGroupTest {
//
//        @Test
//        @DisplayName("when GET request with @PathParameter 'id' then should call " +
//            "groupService.getById once and return JSON with expected group")
//        void getRequestWithParameterId() throws Exception {
//            int groupId = GROUP_ID1;
//            Group testGroup = createTestGroup();
//
//            when(groupServiceMock.findById(groupId)).thenReturn(testGroup);
//
//            mockMvc.perform(get(URI_GROUPS_ID, groupId))
//                .andDo(print())
//                .andExpectAll(
//                    status().isOk(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$.id", is(groupId)),
//                    jsonPath("$.name", is(NAME_FIRST_GROUP)),
//                    jsonPath("$.active", is(true)),
//                    jsonPath("$.facultyId", is(FACULTY_ID1)),
//                    jsonPath("$.facultyName", is(NAME_FIRST_FACULTY))
//                );
//            verify(groupServiceMock,times(1)).findById(groupId);
//        }
//    }

//    @Nested
//    @DisplayName("test 'updateGroup' method")
//    class UpdateGroupTest {
//
//        @Test
//        @DisplayName("when PUT request with parameters 'id', 'name', 'active' and " +
//            "'faculty.id' then should call groupService.update once")
//        void putRequestWithParameters() throws Exception {
//            int groupId = anyInt();
//            mockMvc.perform(put(URI_GROUPS_ID, groupId)
//                    .param("name", NAME_SECOND_GROUP)
//                    .param("active", ON)
//                    .param("faculty.id", String.valueOf(ID2)))
//                .andDo(print())
//                .andExpect(status().is2xxSuccessful());
//
//            verify(groupServiceMock, times(1)).update(1, groupCaptor.capture());
//            Group expectedGroup = groupCaptor.getValue();
//            assertThat(expectedGroup.getId(), is(equalTo(groupId)));
//            assertThat(expectedGroup.getName(), is(equalTo(NAME_SECOND_GROUP)));
//            assertThat(expectedGroup.isActive(), is(true));
//            assertThat(expectedGroup.getFaculty().getId(), is(equalTo(ID2)));
//        }
//    }
//
//    @Nested
//    @DisplayName("test 'getFreeStudentsFromGroup' method")
//    class GetFreeStudentsFromGroupTest {
//
//        @Test
//        @DisplayName("when GET request with parameters 'id', 'time_start' and " +
//            "'time_end' then should return expected list StudentDtos")
//        void getRequestWithPathParameterId() throws Exception {
//            int groupId = 5;
//            LocalDateTime startTime = LocalDateTime.of(2021, 10, 12, 13, 15);
//            LocalDateTime endTime = LocalDateTime.of(2021, 10, 12, 14, 45);
//
//            List<Student> testStudents = createTestStudents();
//
//            when(studentServiceMock.getFreeStudentsFromGroup(groupId, startTime,
//                endTime)).thenReturn(testStudents);
//
//            mockMvc.perform(get(URI_GROUPS_ID_STUDENTS_FREE, groupId)
//                    .param(TIME_START, "2021-10-12 13:15")
//                    .param(TIME_END, "2021-10-12 14:45"))
//                .andDo(print())
//                .andExpectAll(
//                    status().isOk(),
//                    content().contentType(MediaType.APPLICATION_JSON),
//                    jsonPath("$", hasSize(testStudents.size())),
//                    jsonPath("$[0].id", is(STUDENT_ID1)),
//                    jsonPath("$[0].firstName", is(NAME_FIRST_STUDENT)),
//                    jsonPath("$[0].patronymic", is(PATRONYMIC_FIRST_STUDENT)),
//                    jsonPath("$[0].lastName", is(LAST_NAME_FIRST_STUDENT)),
//                    jsonPath("$[0].active", is(true)),
//                    jsonPath("$[0].groupId", is(groupId)),
//                    jsonPath("$[0].groupName", is(NAME_FIRST_GROUP)),
//                    jsonPath("$[1].id", is(STUDENT_ID2)),
//                    jsonPath("$[1].firstName", is(NAME_SECOND_STUDENT)),
//                    jsonPath("$[1].patronymic", is(PATRONYMIC_SECOND_STUDENT)),
//                    jsonPath("$[1].lastName", is(LAST_NAME_SECOND_STUDENT)),
//                    jsonPath("$[1].active", is(false)),
//                    jsonPath("$[1].groupId", is(groupId)),
//                    jsonPath("$[1].groupName", is(NAME_FIRST_GROUP))
//                );
//            verify(studentServiceMock, times(1))
//                .getFreeStudentsFromGroup(groupId, startTime, endTime);
//        }
//    }


}