package ua.com.foxminded.university.domain.entity;

import lombok.Data;

@Data
public abstract class Person {

    private int id;
    private String firstName;
    private String patronymic;
    private String lastName;

}
