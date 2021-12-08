package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.Value;

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
}
