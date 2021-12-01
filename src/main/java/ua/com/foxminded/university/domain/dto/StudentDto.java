package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class StudentDto {

    Integer id;
    String firstName;
    String patronymic;
    String lastName;
    boolean active;
    String fullName;
    int groupId;
    String groupName;
    Set<LessonDto> lessons = new HashSet<>();
}
