package ua.com.foxminded.university.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onBindException(BindException ex) {
        log.warn("Validation error. Check 'violations' field for details");

        List<Violation> listViolations = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
        return new ValidationErrorResponse(listViolations,
            BAD_REQUEST,
            ZonedDateTime.now(ZoneId.of("+3")));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Validation error. Check 'violations' field for details");

        List<Violation> listViolations = ex.getConstraintViolations().stream()
            .map(error -> new Violation(error.getPropertyPath().toString(),
                error.getMessage()))
            .collect(Collectors.toList());
        return new ValidationErrorResponse(listViolations,
            BAD_REQUEST,
            ZonedDateTime.now(ZoneId.of("+3")));
    }
}
