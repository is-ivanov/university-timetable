package ua.com.foxminded.university.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String message;
    private final int status;
    private final String error;
    private final ZonedDateTime timestamp;
}
