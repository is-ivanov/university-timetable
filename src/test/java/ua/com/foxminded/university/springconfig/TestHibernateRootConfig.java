package ua.com.foxminded.university.springconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:hibernate_test_db.properties")
@ComponentScan({"ua.com.foxminded.university.dao"})
@EnableTransactionManagement
public class TestHibernateRootConfig {
    private static final String DRIVER = "db.driver";

    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String HIBERNATE_HBM_DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String PACKAGE_WITH_ENTITY = "ua.com.foxminded.university.domain.entity";
    public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    public static final String HIBERNATE_HIGHLIGHT_SQL = "hibernate.highlight_sql";
    public static final String HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
    public static final String HIBERNATE_STATISTICS = "hibernate.generate_statistics";

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(DRIVER));
        dataSource.setUrl(System.getProperty("DB_URL"));
        dataSource.setUsername(System.getProperty("DB_USERNAME"));
        dataSource.setPassword(System.getProperty("DB_PASSWORD"));
        return dataSource;

    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em =
            new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(PACKAGE_WITH_ENTITY);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty(HIBERNATE_DIALECT, env.getProperty(HIBERNATE_DIALECT));
        properties.setProperty(HIBERNATE_HBM_DDL_AUTO, env.getProperty(HIBERNATE_HBM_DDL_AUTO));
        properties.setProperty(HIBERNATE_FORMAT_SQL, env.getProperty(HIBERNATE_FORMAT_SQL));
        properties.setProperty(HIBERNATE_HIGHLIGHT_SQL, env.getProperty(HIBERNATE_HIGHLIGHT_SQL));
        properties.setProperty(HIBERNATE_USE_SQL_COMMENTS, env.getProperty(HIBERNATE_USE_SQL_COMMENTS));
        properties.setProperty(HIBERNATE_STATISTICS, env.getProperty(HIBERNATE_STATISTICS));

        return properties;
    }
}