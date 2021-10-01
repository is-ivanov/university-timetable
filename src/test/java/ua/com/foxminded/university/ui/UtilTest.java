package ua.com.foxminded.university.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtilTest {

    public static final String URI = "string";
    public static final String EXPECTED_REDIRECT_STRING = "redirect:string";

    @Mock
    private MockHttpServletRequest request;

    @Nested
    @DisplayName("test 'defineRedirect' method")
    class TestDefineRedirect {

        @Test
        @DisplayName("when call with string input then should return expected string")
        void defineRedirectStringInput() {
            assertThat(Util.defineRedirect(URI), is(equalTo(EXPECTED_REDIRECT_STRING)));
        }

        @Test
        @DisplayName("when call with HttpServletRequest then should return expected string")
        void defineRedirectHttpServletRequestInput() {
            when(request.getHeader("referer")).thenReturn(URI);
            assertThat(Util.defineRedirect(request), is(equalTo(EXPECTED_REDIRECT_STRING)));
        }
    }

}