package ua.com.foxminded.university.domain.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import ua.com.foxminded.university.domain.validator.CapitalLetter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Person {

    public static final String MASK_FULL_NAME = "%s, %.1s.%.1s.";

    @Id
    @GeneratedValue
    private Integer id;

    @Size(max = 100)
    @NotBlank(message = "{person.name.not.blank}")
    @CapitalLetter
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "patronymic", length = 100)
    private String patronymic;

    @Size(max = 100)
    @NotBlank(message = "{person.name.not.blank}")
    @CapitalLetter
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    private boolean active;

    public String getFullName() {
        return String.format(MASK_FULL_NAME, this.lastName,
            this.firstName, this.patronymic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Person person = (Person) o;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
