package ua.com.foxminded.university.domain.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LessonsTimeValidator implements ConstraintValidator<LessonsTime, LocalDateTime> {

    private LocalTime from;

    private LocalTime to;

    @Override
    public void initialize(LessonsTime annotation) {
        this.from = LocalTime.parse(annotation.from());
        this.to = LocalTime.parse(annotation.to());
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalTime validatedTime = value.toLocalTime();
        return !validatedTime.isBefore(from) && !validatedTime.isAfter(to);
    }
}
