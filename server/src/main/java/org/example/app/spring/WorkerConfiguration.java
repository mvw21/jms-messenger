package org.example.app.spring;

import jakarta.validation.Validator;
import org.example.AdministrationEndpoint;
import org.example.ChatEndpoint;
import org.example.DaoProviderFactory;
import org.example.worker.AdministrationEndpointWorker;
import org.example.worker.ChatEndpointWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfiguration
{
    @Bean
    public AdministrationEndpoint administrationEndpointWorker (Validator validator,
            DaoProviderFactory daoProviderFactory)
    {
        return new AdministrationEndpointWorker(validator,daoProviderFactory);
    }

    @Bean
    public ChatEndpoint chatEndpointWorker(Validator validator,DaoProviderFactory daoProviderFactory)
    {
      return new ChatEndpointWorker(validator,daoProviderFactory);
    }

}
