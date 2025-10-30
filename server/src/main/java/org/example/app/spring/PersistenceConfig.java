package org.example.app.spring;

import com.zaxxer.hikari.HikariDataSource;
import org.example.DaoProviderFactory;
import org.example.app.DaoProviderFactoryImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig
{
    @Bean
    @DependsOn("flyway")
    public LocalSessionFactoryBean sessionFactory(HikariDataSource dataSource) {

        return createSessionFactory(dataSource);
    }

    @Bean
    public DaoProviderFactory daoProviderFactory(SessionFactory sessionFactory) {
        return new DaoProviderFactoryImpl(sessionFactory);
    }


    public static LocalSessionFactoryBean createSessionFactory(DataSource dataSource) {
        final var sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        sessionFactory.setDataSource(dataSource);
//        sessionFactory.getHibernateProperties().setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
//        sessionFactory.getHibernateProperties().setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        sessionFactory.getHibernateProperties().setProperty(AvailableSettings.SHOW_SQL, "false");

        return sessionFactory;
    }

}
