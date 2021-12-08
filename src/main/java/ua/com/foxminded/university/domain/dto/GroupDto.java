package ua.com.foxminded.university.domain.dto;

import lombok.Value;

@Value
public class GroupDto {

    Integer id;
    String name;
    boolean active;
    Integer facultyId;
    String facultyName;

}
