package ua.com.foxminded.university.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {

    private Integer id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private String departmentName;
    private String status;
    private String fullName;
}
