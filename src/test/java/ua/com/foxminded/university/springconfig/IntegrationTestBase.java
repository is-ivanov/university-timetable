package ua.com.foxminded.university.springconfig;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@ActiveProfiles("pg-test")
public abstract class IntegrationTestBase {

    public static final String IMAGE = "postgres:13.1-alpine";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "pass";
    public static final int PORT = 5432;
    public static final String DB_NAME = "test";

    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(IMAGE)
        .withDatabaseName(DB_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withExposedPorts(PORT);

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", () -> container.getJdbcUrl());
        registry.add("spring.datasource.username", () -> USERNAME);
        registry.add("spring.datasource.password", () -> PASSWORD);
    }
}
