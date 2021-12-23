package ua.com.foxminded.university.ui;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Util {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String REDIRECT = "redirect:";

    private Util() {
    }

    public static String defineRedirect(String uri) {
        return REDIRECT + uri;
    }

    public static String defineRedirect(HttpServletRequest request) {
        return REDIRECT + request.getHeader("referer");
    }

    public static Map<String, String> getErrors(BindingResult result) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
            fieldError -> fieldError.getField() + "Error",
            FieldError::getDefaultMessage
        );
        return result.getFieldErrors().stream().collect(collector);
    }
}
