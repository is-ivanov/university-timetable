package ua.com.foxminded.university.domain.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ua.com.foxminded.university.domain.dto.GroupDto;
import ua.com.foxminded.university.domain.entity.Group;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ua.com.foxminded.university.TestObjects.*;

class GroupDtoMapperTest {

    private GroupDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GroupDtoMapperImpl();
    }

    @Nested
    @DisplayName("test 'toGroupDto' method")
    class ToGroupDtoTest {
        @Test
        @DisplayName("when group with full fields then should return " +
            "GroupDto with full fields")
        void whenGroupWithFullFields_ReturnGroupDtoWithFullFields() {
            Group group = createTestGroup();

            GroupDto groupDto = mapper.toDto(group);

            assertThat(groupDto.getId()).isEqualTo(group.getId());
            assertThat(groupDto.getName()).isEqualTo(group.getName());
            assertThat(groupDto.isActive()).isEqualTo(group.isActive());
            assertThat(groupDto.getFacultyId()).isEqualTo(group.getFaculty().getId());
            assertThat(groupDto.getFacultyName()).isEqualTo(group.getFaculty().getName());
        }
    }

    @Nested
    @DisplayName("test 'toGroup' method")
    class ToGroupTest {
        @Test
        @DisplayName("when GroupDto with full fields then should return Group with full fields")
        void whenGroupDtoWithFullFields_ReturnGroupWithFullFields() {
            GroupDto groupDto = createTestGroupDto();

            Group group = mapper.toEntity(groupDto);

            assertThat(group.getId()).isEqualTo(groupDto.getId());
            assertThat(group.getName()).isEqualTo(groupDto.getName());
            assertThat(group.isActive()).isEqualTo(groupDto.isActive());
            assertThat(group.getFaculty().getId()).isEqualTo(groupDto.getFacultyId());
            assertThat(group.getFaculty().getName()).isEqualTo(groupDto.getFacultyName());
        }
    }

    @Nested
    @DisplayName("test 'toGroupDtos' method")
    class ToGroupDtosTest {
        @Test
        void testConvertListGroupsToListDtos() {
            List<Group> groups = createTestGroups();

            List<GroupDto> groupDtos = mapper.toDtos(groups);

            assertThat(groupDtos).hasSize(groups.size());
            assertThat(groupDtos).extracting(GroupDto::getId)
                .contains(GROUP_ID1, GROUP_ID2);
            assertThat(groupDtos).extracting(GroupDto::getName)
                .contains(NAME_FIRST_GROUP, NAME_SECOND_GROUP);

        }
    }
}