package org.example.app.jms.sender;

import jakarta.jms.*;
import org.example.SerializationProcessor;
import org.example.constants.MsgConstants;
import org.example.dtos.AbstractDto;
import org.example.dtos.ErrorCodesDto;
import org.example.dtos.ErrorDto;
import org.example.exception.MessengerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;

/**
 * @author martin on 16.02.22 г.
 */
public class OperationSender implements AutoCloseable
{
    private static final Logger logger = LoggerFactory.getLogger(OperationSender.class);

    private final ConnectionFactory connectionFactory;
    private final Destination       requestDestination;
    private final Destination responseDestination;
    private final String clientId;

    public OperationSender(ConnectionFactory connectionFactory, Destination requestDestination, String clientId,
            Destination responseDestination)
    {
        this.connectionFactory = connectionFactory;
        this.requestDestination = requestDestination;
        this.clientId = clientId;
        this.responseDestination = responseDestination;
    }

    public String send(AbstractDto payload, String operationName)
    {
        try(JMSContext ctx = connectionFactory.createContext())
        {
            final TextMessage message = createTextMessage(ctx,operationName);

            if (payload != null) {
                final String msgContent = SerializationProcessor.instance.serialize(payload);
                message.setText(msgContent);
            }

            ctx.createProducer().send(requestDestination, message);
            return message.getJMSCorrelationID();
        } catch (Exception e)
        {
            final ErrorDto error = new ErrorDto(ErrorCodesDto.INTERNAL_SERVER_ERROR, Collections.singletonList(e.getLocalizedMessage()));
            throw new MessengerException(e, error);
        }

    }

    private TextMessage createTextMessage(JMSContext jmsContext, String operationName) throws JMSException
    {
        final TextMessage message = jmsContext.createTextMessage();
        attachHeaders(message, operationName);
        return message;
    }

    private void attachHeaders(TextMessage message, String operationName) throws JMSException
    {
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        message.setStringProperty(MsgConstants.OPERATION_NAME, operationName);
        message.setStringProperty(MsgConstants.CLIENT_ID,clientId);
        message.setJMSReplyTo(responseDestination);

    }

    @Override
    public void close() throws Exception
    {

    }

}
