package ua.com.foxminded.university.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {

    private Integer id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private int groupId;
    private String groupName;
    private boolean isActive;
    private String fullName;
}
