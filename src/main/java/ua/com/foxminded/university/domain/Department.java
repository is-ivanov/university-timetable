package ua.com.foxminded.university.domain;

import lombok.Data;

@Data
public class Department {

    private int id;
    private String name;
    private Teacher head;
    private Faculty faculty;

}
