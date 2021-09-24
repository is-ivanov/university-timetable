package ua.com.foxminded.university.ui;

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
        return REDIRECT + request.getHeader("referer");
    }
}
