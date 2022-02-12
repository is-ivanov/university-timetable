package ua.com.foxminded.university.springconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"ua.com.foxminded.university.domain.mapper", "ua.com.foxminded.university.ui.restcontroller.link"})
public class TestMapperConfig {

}
