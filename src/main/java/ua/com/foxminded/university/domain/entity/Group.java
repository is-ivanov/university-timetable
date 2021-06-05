package ua.com.foxminded.university.domain.entity;

import lombok.Data;

@Data
public class Group {

    private int id;
    private String name;
    private Faculty faculty;

}
