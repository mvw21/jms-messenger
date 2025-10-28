package org.example.app.spring.jms;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.example.AdministrationEndpoint;
import org.example.ChatEndpoint;
import org.example.app.JmsProperties;
import org.example.app.response.ResponseSender;
import org.example.app.spring.MsgDestination;
import org.example.app.spring.RequestListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
public class JmsConfiguration
{

    @Bean
    public ConnectionFactory connectionFactory(JmsProperties jmsProperties){
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsProperties.getBrokerUrl(), jmsProperties.getUsername(), jmsProperties.getPassword());
        return connectionFactory;
    }

    @Bean
    public DefaultMessageListenerContainer adminRouterListenerContainer(
            ConnectionFactory connectionFactory,
            ResponseSender responseSender,
            AdministrationEndpoint administrationEndpointWorker
    ) {
        final DefaultMessageListenerContainer listenerContainer = JmsConfigUtils.createListenerContainer(connectionFactory);
        listenerContainer.setDestinationName(MsgDestination.ADMIN_PROCESSING.getDestinationName());
        listenerContainer.setMessageListener(new RequestListener(responseSender, administrationEndpointWorker));
        return listenerContainer;
    }

    @Bean
    public DefaultMessageListenerContainer chatRouterListenerContainer(
            ConnectionFactory connectionFactory,
            ResponseSender responseSender,
            ChatEndpoint chatEndpointWorker
    ) {
        final DefaultMessageListenerContainer listenerContainer = JmsConfigUtils.createListenerContainer(connectionFactory);
        listenerContainer.setDestinationName(MsgDestination.CHAT_PROCESSING.getDestinationName());
        listenerContainer.setMessageListener(new RequestListener(responseSender, chatEndpointWorker));
        return listenerContainer;
    }

    @Bean
    public ResponseSender responseSender(ConnectionFactory connectionFactory)
    {
        return new ResponseSender(connectionFactory);
    }
}
