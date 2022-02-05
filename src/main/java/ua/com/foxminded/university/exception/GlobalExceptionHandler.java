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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@SuppressWarnings("NullableProblems")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String VALIDATION_ERROR_MESSAGE =
        "Validation error. Check 'violations' field for details.";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return handleValidationExceptions(ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        return handleValidationExceptions(ex, request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex,
                                                             WebRequest request) {
        log.warn("Validation error caught: {}", ex.getMessage(), ex);
        List<Violation> listViolations = new ArrayList<>();
        if (ex instanceof BindException) {
            listViolations = getViolationsFromBindException((BindException) ex);
        } else if (ex instanceof ConstraintViolationException) {
            listViolations = getViolationsFromConstraintViolationException(
                (ConstraintViolationException) ex);
        }

//        ValidationErrorResponse errorResponseBody =
//            new ValidationErrorResponse(VALIDATION_ERROR_MESSAGE,
//                BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
//                getNow(), listViolations, ex, request);

        return createErrorResponse(ex, request, listViolations);
    }

    @ExceptionHandler({
        MyEntityNotFoundException.class,
        EmptyResultDataAccessException.class
    })
    public ResponseEntity<Object> handleNotFoundExceptions(Exception ex,
                                                           HttpServletRequest request) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({
        MyPageNotFoundException.class,
        IllegalArgumentException.class,
        ServiceException.class
    })
    public ResponseEntity<Object> handleBadRequestExceptions(Exception ex,
                                                             HttpServletRequest request) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
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

    private ResponseEntity<Object> createErrorResponse(Exception ex,
                                                       HttpStatus status,
                                                       HttpServletRequest request) {
        log.warn("error caught: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex, status, request);
        return createResponse(errorResponse, status);
    }

    private ResponseEntity<Object> createErrorResponse(Exception ex,
                                                       WebRequest request,
                                                       List<Violation> violations) {
        log.warn("error caught: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
        errorResponse.setMessage(VALIDATION_ERROR_MESSAGE);
        errorResponse.setViolations(violations);
        return createResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private <T> ResponseEntity<T> createResponse(T body, HttpStatus status) {
        log.debug("Responding with a status of {}", status);
        return new ResponseEntity<>(body, status);
    }

}