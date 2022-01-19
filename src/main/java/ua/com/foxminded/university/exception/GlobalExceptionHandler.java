package ua.com.foxminded.university.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@SuppressWarnings("NullableProblems")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String VALIDATION_ERROR_MESSAGE = "Validation error";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        return handleValidationExceptions(ex);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        return handleValidationExceptions(ex);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
        log.warn("Validation error caught: {}", ex.getMessage(), ex);
        List<Violation> listViolations = new ArrayList<>();
        if (ex instanceof BindException) {
            listViolations = getViolationsFromBindException((BindException) ex);
        } else if (ex instanceof ConstraintViolationException) {
            listViolations = getViolationsFromConstraintViolationException(
                (ConstraintViolationException) ex);
        }

        ValidationErrorResponse errorResponseBody =
            new ValidationErrorResponse(VALIDATION_ERROR_MESSAGE,
            BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
            getNow(), listViolations);
        return createResponse(errorResponseBody, BAD_REQUEST);
    }

    @ExceptionHandler({
            MyEntityNotFoundException.class,
            EmptyResultDataAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex) {
        return createErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler({
            MyPageNotFoundException.class,
            IllegalArgumentException.class
        })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex) {
        return createErrorResponse(ex, BAD_REQUEST);
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

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, HttpStatus status){
        log.warn("error caught: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
            status.value(), status.getReasonPhrase(), getNow());
        return createResponse(errorResponse, status);
    }

    private <T> ResponseEntity<T> createResponse(T body, HttpStatus status) {
        log.debug("Responding with a status of {}", status);
        return new ResponseEntity<>(body, status);
    }

    private ZonedDateTime getNow() {
        return ZonedDateTime.now(ZoneId.of("+3"));
    }

}