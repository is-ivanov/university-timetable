package ua.com.foxminded.university.ui.util;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

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

    public static void addRedirectUrl(HttpServletRequest request,
                                      RepresentationModel<?> model) {
        String redirectUrl = getRedirectUrl(request);
        if (redirectUrl != null) {
            model.add(Link.of(redirectUrl, "redirect"));
        }
    }

    public static URI getLocation(RepresentationModel<?> model) {
        return model.getRequiredLink(IanaLinkRelations.SELF).toUri();
    }

    private static String getRedirectUrl(HttpServletRequest request) {
        return request.getHeader("referer");
    }

}
