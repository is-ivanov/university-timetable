package ua.com.foxminded.university.domain.entity;

import lombok.Data;

@Data
public abstract class Person {

    private Integer id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private boolean active;

}
