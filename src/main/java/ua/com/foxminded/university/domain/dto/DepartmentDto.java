package ua.com.foxminded.university.domain.dto;

import lombok.Value;

@Value
public class DepartmentDto {

    Integer id;
    String name;
    Integer facultyId;
    String facultyName;

}
