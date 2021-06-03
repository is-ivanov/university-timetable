package ua.com.foxminded.university.domain;

import lombok.Data;

@Data
public class Faculty {

    private int id;
    private String name;
    private Teacher dean;

}
