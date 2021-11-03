package ua.com.foxminded.university.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {

    private Integer id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private boolean active;
    private String fullName;
    private int departmentId;
    private String departmentName;
}
