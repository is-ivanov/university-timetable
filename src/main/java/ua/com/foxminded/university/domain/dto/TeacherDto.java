package ua.com.foxminded.university.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TeacherDto {

    Integer id;
    String firstName;
    String patronymic;
    String lastName;
    boolean active;
    String fullName;
    int departmentId;
    String departmentName;
}
