package ua.com.foxminded.university.ui;

import javax.servlet.http.HttpServletRequest;

public class Util {

    public static final String REDIRECT = "redirect:";

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static String defineRedirect(String uri) {
        return REDIRECT + uri;
    }

    public static String defineRedirect(HttpServletRequest request) {
        return REDIRECT + request.getHeader("referer");
    }
}
