package com.core;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.example.EndpointRegistry;
import org.example.app.EndpointRegistryImpl;
import org.example.dtos.message.MessageRequestDto;
import org.example.dtos.message.MessagesFilterDto;
import org.example.dtos.user.SignInDto;
import org.example.dtos.user.SignUpDto;

import java.util.UUID;

/**
 * @author martin on 16.02.22 г.
 */
public class Main
{
    public static void main(String[] args) throws JMSException
    {
         ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("tcp://0.0.0.0:61616","admin","admin");

        EndpointRegistry endpointRegistry = new EndpointRegistryImpl(connectionFactory,"martin");
        //1.instancirane na takova registry i injectvane v testovete



        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("chat-input-queue");
        Queue queue1 = session.createQueue("message-input-queue");

        MessageRequestDto messageRequestDto = new MessageRequestDto();
        messageRequestDto.setContent("SOMECONTENT");
        messageRequestDto.setAuthorId(UUID.randomUUID());
        messageRequestDto.setRecipientId(UUID.randomUUID());

        endpointRegistry.getChatEndpoint().sendMessage(messageRequestDto);
        endpointRegistry.getChatEndpoint().loadMessages(new MessagesFilterDto());
        endpointRegistry.getAdministrationEndpoint().signIn(new SignInDto());
        endpointRegistry.getAdministrationEndpoint().signUp(new SignUpDto());






//        String clientId = UUID.randomUUID().toString();
//        String operationName = "sendMessage";
//
//
//
//        OperationSender operationSender = new OperationSender(connectionFactory,queue, clientId,queue1);
//        operationSender.send(messageRequestDto,operationName);

    }

}
