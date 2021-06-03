package ua.com.foxminded.university.domain;

import lombok.Data;

@Data
public class Group {

    private int id;
    private String name;
    private Faculty faculty;

}
