package ua.com.foxminded.university.ui.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

public final class ResponseUtil {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String REDIRECT = "redirect:";

    private ResponseUtil() {
    }

    public static String defineRedirect(String uri) {
        return REDIRECT + uri;
    }

    public static String defineRedirect(HttpServletRequest request) {
        return REDIRECT + getRedirectUrl(request);
    }

    public static ResponseEntity<String> getResponseEntityWithRedirectUrl(HttpServletRequest request) {
        return new ResponseEntity<>("{\"redirect\": \"" + getRedirectUrl(request) + "\"}",
            HttpStatus.OK);
    }

    public static ResponseEntity<Object> getPostResponseRedirectUrl(HttpServletRequest request,
                                                                    String path,
                                                                    UriComponentsBuilder builder,
                                                                    long id) {
        URI location = builder.path(path).buildAndExpand(id).toUri();
        String redirect = "{\"redirect\": \"" + getRedirectUrl(request) + "\"}";
        return ResponseEntity.created(location).body(redirect);
    }

    public static String getRedirectUrl(HttpServletRequest request) {
        return request.getHeader("referer");
    }
}
