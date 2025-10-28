package org.example.app;

import org.example.app.spring.PersistenceConfig;
import org.example.app.spring.RestConfig;
import org.example.app.spring.WorkerConfiguration;
import org.example.app.spring.jms.JmsConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.brandmaker.*"))
@Import({
        PersistenceConfig.class,
        RestConfig.class,
        JmsConfiguration.class,
        WorkerConfiguration.class
})
@EnableConfigurationProperties({
        JmsProperties.class
})
public class SpringBootApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(SpringBootApp.class, args);
    }
}
