package ua.com.foxminded.university.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    private final List<Violation> violations;

    public ValidationErrorResponse(String message, int status, String error,
                                   ZonedDateTime timestamp,
                                   List<Violation> violations) {
        super(message, status, error, timestamp);
        this.violations = violations;
    }
}
