package ua.com.foxminded.university.springconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:app.properties")
@ComponentScan({"ua.com.foxminded.university.dao", "ua.com.foxminded.university.domain"})
public class RootConfig {

    public static final String JDBC_URL = "jdbc.url";
    public static final String PACKAGE_WITH_ENTITY = "ua.com.foxminded.university.domain.entity";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    public static final String HIBERNATE_HIGHLIGHT_SQL = "hibernate.highlight_sql";
    public static final String HIBERNATE_USE_SQL_COMMENTS = "hibernate.use_sql_comments";
    public static final String HIBERNATE_AUTO_SCHEMA = "hibernate.hbm2ddl.auto";
    public static final String HIBERNATE_STATISTICS = "hibernate.generate_statistics";

    private Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Bean(destroyMethod = "")
    public DataSource dataSource() throws NamingException {
        return (DataSource) new JndiTemplate()
            .lookup(Objects.requireNonNull(env.getProperty(JDBC_URL)));
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
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
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
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

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty(HIBERNATE_DIALECT, env.getProperty(HIBERNATE_DIALECT));
        properties.setProperty(HIBERNATE_FORMAT_SQL, env.getProperty(HIBERNATE_FORMAT_SQL));
        properties.setProperty(HIBERNATE_HIGHLIGHT_SQL, env.getProperty(HIBERNATE_HIGHLIGHT_SQL));
        properties.setProperty(HIBERNATE_USE_SQL_COMMENTS, env.getProperty(HIBERNATE_USE_SQL_COMMENTS));
        properties.setProperty(HIBERNATE_AUTO_SCHEMA, env.getProperty(HIBERNATE_AUTO_SCHEMA));
        properties.setProperty(HIBERNATE_STATISTICS, env.getProperty(HIBERNATE_STATISTICS));

        return properties;
    }

}
