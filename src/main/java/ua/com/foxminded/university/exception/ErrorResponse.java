package ua.com.foxminded.university.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    public static final String TRACE = "trace";

    private String message;
    private final int status;
    private final String error;
    private final LocalDateTime timestamp;
    private String stackTrace;
    private String path;
    private List<Violation> violations;

    public ErrorResponse(Exception ex, HttpStatus status) {
        this.message = ex.getMessage();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Exception ex, HttpStatus status, HttpServletRequest request) {
        this(ex, status);
        this.path = request.getRequestURI();
        if (isTraceOn(request)) {
            this.stackTrace = ExceptionUtils.getStackTrace(ex);
        }
    }

    public ErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        this(ex, status);
        this.path = ((ServletWebRequest)request).getRequest().getRequestURI();
        if (isTraceOn(request)) {
            this.stackTrace = ExceptionUtils.getStackTrace(ex);
        }
    }

    private boolean isTraceOn(WebRequest request) {
        String [] value = request.getParameterValues(TRACE);
        return isTraceOn(value);
    }

    private boolean isTraceOn(HttpServletRequest request) {
        String [] value = request.getParameterValues(TRACE);
        return isTraceOn(value);
    }

    private boolean isTraceOn(String [] value){
        return Objects.nonNull(value)
            && value.length > 0
            && value[0].contentEquals("true");
    }
}
