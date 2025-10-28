package org.example.app.response;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.TextMessage;
import org.example.SerializationProcessor;
import org.example.dtos.AbstractDto;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.exception.MessengerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class ResponseSender
{
    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    private final ConnectionFactory connectionFactory;

    public ResponseSender(ConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }

    public void sendResponse(AbstractDto payload, Destination responseDestination,String correlationId)
    {

        try(JMSContext ctx = connectionFactory.createContext())
        {
            final TextMessage message = ctx.createTextMessage();

            if (payload != null) {
                final String msgContent = SerializationProcessor.instance.serialize(payload);
                message.setText(msgContent);
            }

            message.setJMSCorrelationID(correlationId);
            ctx.createProducer().send(responseDestination, message);
        } catch (Exception e)
        {
            final ErrorDto error = new ErrorDto(ErrorCodesDto.INTERNAL_SERVER_ERROR, Collections.singletonList(e.getLocalizedMessage()));
            throw new MessengerException(e, error);
        }

    }

}
