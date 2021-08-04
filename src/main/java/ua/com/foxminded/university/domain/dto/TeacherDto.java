package ua.com.foxminded.university.domain.dto;

import lombok.Data;

@Data
public class TeacherDto {

    private int id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private String departmentName;
    private boolean active;

}
