package ua.com.foxminded.university.ui.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class Util {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String REDIRECT = "redirect:";

    private Util() {
    }

    public static String defineRedirect(String uri) {
        return REDIRECT + uri;
    }

    public static String defineRedirect(HttpServletRequest request) {
        return REDIRECT + getRedirectUrl(request);
    }

    public static ResponseEntity<String> getResponseEntityWithRedirectUrl(HttpServletRequest request) {
        return new ResponseEntity<>("{\"location\": \"" + getRedirectUrl(request) + "\"}",
            HttpStatus.OK);
    }

    private static String getRedirectUrl(HttpServletRequest request) {
        return request.getHeader("referer");
    }
}
