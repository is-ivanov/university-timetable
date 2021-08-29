package ua.com.foxminded.university.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class Person {

    public static final String MASK_FULL_NAME = "%s, %.1s.%.1s.";

    private Integer id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private boolean active;

    public String getFullName() {
        return String.format(MASK_FULL_NAME, this.lastName,
            this.firstName, this.patronymic);
    }
}
