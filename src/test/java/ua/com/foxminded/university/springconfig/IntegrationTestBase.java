package ua.com.foxminded.university.springconfig;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.com.foxminded.university.container.PostgresContainer;

@SpringJUnitConfig(TestHibernateRootConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
public abstract class IntegrationTestBase {

    @Container
    public static PostgreSQLContainer<PostgresContainer> container
        = PostgresContainer.getInstance();

}
