package ua.com.foxminded.university.domain.entity;

import lombok.Data;

@Data
public class Faculty {

    private int id;
    private String name;
    private Teacher dean;

}
