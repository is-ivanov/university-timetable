package ua.com.foxminded.university.domain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LessonsTimeValidator.class)
@Documented
public @interface LessonsTime {

    String message() default "{LessonTime.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from() default "08:00";

    String to() default "20:00";

}
