package ua.com.foxminded.university.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String APPLICATION_TITLE = "University Timetable";
    public static final String API_VERSION = "1.0.0";
    public static final String DEVELOPER_EMAIL = "i.s.ivanov2307@gmail.com";
    public static final String DEVELOPER = "Ivan Ivanov";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(
                new Info()
                    .title(APPLICATION_TITLE)
                    .version(API_VERSION)
                    .contact(
                        new Contact()
                            .email(DEVELOPER_EMAIL)
                            .name(DEVELOPER)
                    )
            );
    }
}
