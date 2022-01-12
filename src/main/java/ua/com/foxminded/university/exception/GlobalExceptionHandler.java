package ua.com.foxminded.university.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_MESSAGE = "Validation error";

    @ExceptionHandler({
        BindException.class,
        ConstraintViolationException.class,
        MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(Exception ex) {
        log.warn("Validation error. Check 'violations' field for details");
        List<Violation> listViolations = new ArrayList<>();
        if (ex instanceof BindException) {
            listViolations = getViolationsFromBindException((BindException) ex);
        } else if (ex instanceof ConstraintViolationException) {
            listViolations = getViolationsFromConstraintViolationException(
                (ConstraintViolationException) ex);
        }
        return ResponseEntity.badRequest()
            .body(new ValidationErrorResponse(VALIDATION_ERROR_MESSAGE,
            BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
            getNow(), listViolations));
    }

    @ExceptionHandler(MyEntityNotFoundException.class)
    ResponseEntity<ErrorResponse> onEntityNotFoundException(MyEntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
            NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), getNow());
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    private List<Violation> getViolationsFromBindException(BindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
    }

    private List<Violation> getViolationsFromConstraintViolationException(
        ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
            .map(error -> new Violation(error.getPropertyPath().toString(),
                error.getMessage()))
            .collect(Collectors.toList());
    }

    private ZonedDateTime getNow() {
        return ZonedDateTime.now(ZoneId.of("+3"));
    }

}