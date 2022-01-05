package ua.com.foxminded.university.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ValidationProperties {

    @Value("${validation.time.start.from}")
    private String timeStartFrom;

    @Value("${validation.time.start.to}")
    private String timeStartTo;

    @Value("${validation.time.end.from}")
    private String timeEndFrom;

    @Value("${validation.time.end.to}")
    private String timeEndTo;

    @Value("${validation.group.max-number-students}")
    private int maxNumberStudent;
}
